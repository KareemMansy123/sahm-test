# Sahm Food POS — Architecture

## 1. High-Level Diagram

```
┌─────────────────────────────────────────────────────────────┐
│  composeApp (Android + iOS via Compose Multiplatform)       │
│  • Theme + Components + Screens + App navigation             │
│  • Koin injection of stores                                  │
└──────────────────┬──────────────────────────────────────────┘
                   │  observes StateFlow, dispatches Intent
                   ▼
┌─────────────────────────────────────────────────────────────┐
│  :shared:presentation (KMP)                                 │
│  • MVI stores: CatalogStore, CheckoutStore, HistoryStore     │
│  • BaseStore with SupervisorJob scope                        │
│  • One-shot Effects via SharedFlow                           │
└──────────────────┬──────────────────────────────────────────┘
                   │  invokes use cases
                   ▼
┌─────────────────────────────────────────────────────────────┐
│  :shared:domain (KMP, no platform deps)                     │
│  • Entities (Money, Product, Order, Receipt, …)              │
│  • Repository INTERFACES                                     │
│  • Use cases (CalculateOrderTotals, CheckoutOrder, …)        │
│  • Service interfaces (Clock, IdGenerator, PrinterService)   │
└──────────────────┬──────────────────────────────────────────┘
                   │ depended-upon (never depends back)
                   ▼
┌─────────────────────────────────────────────────────────────┐
│  :shared:data (KMP)                                          │
│  • SQLDelight DB + queries                                   │
│  • Repository IMPLEMENTATIONS                                │
│  • MockPrinterService, StubRemoteApiService                  │
│  • SyncWorker (outbox processor)                             │
│  • expect/actual DatabaseDriverFactory (Android/iOS/JVM)     │
└─────────────────────────────────────────────────────────────┘
```

`:shared:presentation` deliberately depends on `:shared:domain` but NOT on
`:shared:data`. The Gradle module boundary is the wall: presentation can
only see repository *interfaces*. Wiring concrete repos into stores happens
in `composeApp` via Koin.

## 2. Module Map

| Module                  | Kotlin source roots                         | Purpose                                       |
|-------------------------|---------------------------------------------|-----------------------------------------------|
| `:shared:domain`        | commonMain, commonTest                      | Entities, repo interfaces, use cases          |
| `:shared:data`          | commonMain, androidMain, iosMain, jvmMain   | SQLDelight DB, repo impls, printer, sync      |
| `:shared:presentation`  | commonMain, commonTest                      | MVI stores + base store framework             |
| `:composeApp`           | commonMain, androidMain, iosMain            | Compose UI, theme, navigation, Koin wiring    |
| `iosApp`                | Swift                                       | Xcode shell hosting Compose iOS framework     |

## 3. State Management — MVI

Each store extends `BaseStore<State, Intent, Effect>`:

- **State** — immutable data class, single `StateFlow<S>` source of truth
- **Intent** — sealed interface; only way to mutate state
- **Effect** — one-shot signal (navigate, toast). `SharedFlow` with buffer 16

```kotlin
abstract class BaseStore<S, I, E>(initial: S, scope: CoroutineScope) {
    val state: StateFlow<S>
    val effects: SharedFlow<E>
    fun dispatch(intent: I)
    protected abstract suspend fun handle(intent: I)
    protected fun updateState(reducer: (S) -> S)
    protected suspend fun emitEffect(effect: E)
}
```

The Android side does NOT use Android `ViewModel`. The stores own their own
`CoroutineScope` with `SupervisorJob`. The Compose `DisposableEffect` in
`App.kt` cancels all three stores when the composition leaves the tree. This
keeps the presentation layer fully platform-agnostic — the same store classes
run unchanged on iOS.

If you need androidx.lifecycle ViewModel integration (e.g. to survive config
change), wrap each store in a tiny `class XViewModel(val store: XStore) :
ViewModel() { override fun onCleared() = store.cancel() }`. The contract is
the same.

## 4. Offline-First Strategy

- **Storage**: SQLDelight, single SQLite file `sahm_pos.db`. Currency stored
  as `INTEGER` (piastres / minor units) — never `REAL`. Tables: `Product`,
  `PosOrder`, `PosOrderItem`, `SyncQueueEntry`.
- **Money**: `data class Money(amount: Long, currency: String)`. All
  arithmetic is integer; `percent(bps: Int)` is half-up rounded.
- **Order lifecycle**: created with `status = PAID` immediately on checkout
  (transaction is final from the cashier's perspective). The sync worker
  later transitions to `SYNCED` or `SYNC_FAILED`.
- **Outbox**: every successful checkout writes an Order row AND an entry to
  `SyncQueueEntry`. Same DB transaction (`db.transaction { … }`) — no
  outbox-vs-business-write split-brain.
- **SyncWorker**: drains the queue on (a) app start, (b) connectivity
  observed online, (c) manual `triggerSync()`. Mutex guards re-entry.
- **Retry**: exponential backoff `1s → 2s → 4s → 8s → 16s` capped at 30s,
  max 5 attempts, then the entry and its order are marked `FAILED`.
- **Conflict resolution**: server authoritative. The stub
  `RemoteApiService.push` echoes back an Ack; a real backend rejecting a
  duplicate would respond with the existing record's id and the client
  treats it as success. No client-side LWW that could destroy a peer order.

## 5. Threading Contract

| Layer        | Dispatcher          | Notes                                  |
|--------------|---------------------|----------------------------------------|
| SQLDelight   | `Dispatchers.IO`    | every repo `withContext(IO)`           |
| Repository Flows | `flowOn(IO)`    | downstream collection independent      |
| Use cases    | caller-controlled   | thin; no context switch                |
| Store handle | `Dispatchers.Default` | CPU-bound state reduction           |
| Compose UI   | `Dispatchers.Main`  | `collectAsState()` reads on main       |
| SyncWorker   | `Dispatchers.IO`    | network + DB                           |

## 6. Resolved Open Questions

| #  | Question                              | Resolution                                                                                |
|----|---------------------------------------|-------------------------------------------------------------------------------------------|
| 1  | Cart→Checkout handoff                 | `CatalogEffect.NavigateToCheckout(cart, totals)` → `App.kt` collects → `CheckoutIntent.Initialize` |
| 2  | Draft order recovery on restart       | Out of scope — cart lives in memory. Not persisted. Documented.                            |
| 3  | UUID generation                       | `RandomIdGenerator` in data layer (32-char hex). Domain depends on `IdGenerator` interface. |
| 4  | `SyncWorker` scope ownership          | Owns its own `SupervisorJob + Dispatchers.IO`. Survives screen lifecycle. App-scoped.       |
| 5  | Discount code validation              | Out of scope — `CalculateOrderTotals` accepts optional `Money` discount, no code lookup.    |
| 6  | Rounding                              | Half-up rounding via `Money.percent(bps)` — matches local POS conventions.                 |
| 7  | iOS `startKoin` placement             | Lazy init in `MainViewController()` (first invocation only).                                |
| 8  | Compose Multiplatform iOS gaps        | Sticking to grid/list/sheet/textfield primitives. No exotic effects.                       |

## 7. Test Strategy

- **Domain unit tests** (commonTest): `MoneyTest`, `CalculateOrderTotalsTest`,
  `CartOperationsTest`, `CheckoutOrderTest`. Pure Kotlin; no DB, no Android.
- **Data tests** (jvmTest): `OrderRepositoryImplTest` using
  `JdbcSqliteDriver(IN_MEMORY)`. Runs on host JVM.
- **Presentation tests** (commonTest): `CatalogStoreTest`,
  `CheckoutStoreTest` using Turbine + fake repositories.
- **What we don't test**: the mock printer (it IS the test double), the stub
  remote API, generated SQLDelight code.

## 8. Trade-offs Acknowledged

- **Stores cancel via `DisposableEffect` instead of androidx ViewModel** —
  cleaner KMP story, costs Activity-recreation persistence. Acceptable for
  a POS where rotation is rare on tablets.
- **No real ConnectivityObserver** — `AlwaysOfflineConnectivityObserver`
  ships with a `setOnline()` toggle so demos can flip online to drain the
  queue, but no actual NetworkCallback / NWPathMonitor integration.
- **Cart not persisted across kill** — if the app dies mid-cart, the cart
  is lost. A draft-order table would solve this but adds complexity beyond
  the assignment scope.
- **SQLDelight over Room** — Room is Android-only on stable. SQLDelight
  works across Android, iOS, JVM with one schema.
- **Koin over Hilt** — Hilt is Android-only. Koin has KMP support.
- **No real auth / multi-user** — single-cashier device assumed.

## 9. Scaling to Multi-Branch Restaurants

What this design buys you for free:

- **Branch-tagged sync** — add a `branch_id` column to `SyncQueueEntry`; the
  outbox already serializes the order as JSON, so the payload schema can
  evolve without migration.
- **Per-branch SQLite** — each terminal has its own DB. Conflict resolution
  is server-side, not client-side, so terminals never need to gossip.
- **Menu pushes** — the same outbox pattern reversed: server publishes menu
  updates, each terminal pulls and runs `ProductRepository.upsertAll()`.
- **Offline-by-default UX** — cashiers never notice when WAN drops; orders
  keep flowing into the local DB. The only failure mode is the sync queue
  growing, which the dashboard could surface as "n orders pending sync".

Hard parts NOT solved here:

- **Real-time multi-terminal stock sync** — e.g. "last 3 Margherita pizzas"
  reservation across two cashiers. Needs server-side optimistic locking or
  inventory tokens, neither of which fits an offline-first model cleanly.
- **End-of-day reconciliation** — the X/Z report flow is unimplemented.
- **PCI scope for card payments** — currently a no-op for `CARD`. Real
  integration requires a payment SDK and out-of-band terminal certification.

## 10. File Index

Key files to read for understanding:

- `shared/domain/src/commonMain/kotlin/com/sahmfood/pos/domain/entities/Money.kt` — integer-money discipline
- `shared/domain/src/commonMain/kotlin/com/sahmfood/pos/domain/usecases/CheckoutOrder.kt` — orchestration
- `shared/data/src/commonMain/sqldelight/com/sahmfood/pos/data/db/SahmPosDatabase.sq` — schema + queries
- `shared/data/src/commonMain/kotlin/com/sahmfood/pos/data/sync/SyncWorker.kt` — outbox processor
- `shared/presentation/src/commonMain/kotlin/com/sahmfood/pos/presentation/common/Store.kt` — MVI base
- `shared/presentation/src/commonMain/kotlin/com/sahmfood/pos/presentation/catalog/CatalogStore.kt` — main flow
- `composeApp/src/commonMain/kotlin/com/sahmfood/pos/App.kt` — composition root + navigation
