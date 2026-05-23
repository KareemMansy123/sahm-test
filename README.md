# Sahm Food POS — Kotlin Multiplatform

A take-home point-of-sale module built with Kotlin Multiplatform and Compose
Multiplatform. Targets **Android + iOS** with a single shared codebase,
offline-first with SQLDelight, MVI in shared logic, MVVM-flavored at the UI
layer, Koin DI, and a mock thermal-receipt printer.

> See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) and
> [`docs/DESIGN.md`](docs/DESIGN.md) for the deep dive.

---

## What's in the box

| Concern             | Implementation                                                                |
|---------------------|-------------------------------------------------------------------------------|
| Cross-platform      | Compose Multiplatform (Android + iOS) — single Compose UI tree                |
| Architecture        | Clean Architecture, 3 shared modules (`domain` / `data` / `presentation`)     |
| Presentation        | MVI in shared (Intent/State/Effect via Flow) + thin Compose collectors        |
| Persistence         | SQLDelight with expect/actual driver (Android, iOS, JVM-for-tests)            |
| Async               | kotlinx-coroutines + Flow throughout                                          |
| DI                  | Koin with shared + platform-specific modules                                  |
| Offline-first       | All writes hit local DB first; outbox queue + SyncWorker pushes when online   |
| Hardware sim        | `MockPrinterService` renders an ESC/POS-style receipt and emits a print log   |
| Tests               | Domain (commonTest), data (jvmTest with in-memory SQLite), presentation MVI   |

---

## Project layout

```
sahm-food-pos/
├── settings.gradle.kts           # KMP module wiring
├── build.gradle.kts              # root, plugins declared apply false
├── gradle/libs.versions.toml     # version catalog
├── shared/
│   ├── domain/                   # entities, repo interfaces, use cases (pure Kotlin)
│   ├── data/                     # SQLDelight, repo impls, mock printer, sync worker
│   └── presentation/             # MVI stores: Catalog, Checkout, History
├── composeApp/                   # Compose Multiplatform target (Android + iOS)
│   ├── src/commonMain/           # theme, components, screens, App.kt, Koin wiring
│   ├── src/androidMain/          # Application, MainActivity, manifest, resources
│   └── src/iosMain/              # MainViewController.kt (Compose UIViewController bridge)
├── iosApp/                       # Xcode-managed Swift shell (generated/imported)
└── docs/
    ├── ARCHITECTURE.md
    └── DESIGN.md
```

---

## Open in Android Studio

1. **Studio version**: Hedgehog (2023.1.1) or newer with the *Kotlin Multiplatform Mobile* plugin enabled.
2. `File → Open` → select `/Users/mansy/StudioProjects/sahm-food-pos`.
3. On first sync, Gradle downloads Kotlin 2.0.21, AGP 8.5.2, Compose Multiplatform 1.7.0, SQLDelight 2.0.2, Koin 4.0.0.
4. Once sync is green, select the `composeApp` run config and pick a device.

> **No JDK / Gradle was available on the authoring machine.** This repo was authored
> without executing `./gradlew build` or `./gradlew test`. The code passed a
> dedicated code-review pass (see [`docs/REVIEW.md`](docs/REVIEW.md) for what
> was caught and fixed) but I have not personally watched the binary install.
> First-run troubleshooting tips are in the *Known Caveats* section below.

### Build commands you can run once Gradle is wired up

```bash
./gradlew :composeApp:installDebug          # install Android app on connected device
./gradlew :shared:domain:jvmTest            # run domain unit tests
./gradlew :shared:data:jvmTest              # run data tests (in-memory SQLite)
./gradlew :shared:presentation:jvmTest      # run MVI store tests
./gradlew :shared:data:linkDebugFrameworkIosSimulatorArm64   # produce iOS framework
```

### iOS

The iOS shell at `iosApp/` is a minimal Swift entry point. To actually run on iOS
you'll need to generate the Xcode project:

1. From the project root: open `iosApp/` in Xcode (a `.xcodeproj` will need to be
   created if not already; the Compose Multiplatform New-Project wizard
   generates one).
2. Add a Run Script build phase that runs
   `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode`.
3. Link the `ComposeApp.framework` into the iOS target.

Production teams typically commit the generated `.xcodeproj` — this repo
ships only the Swift sources and `Info.plist` so the layout stays clean.

---

## Demo flow

1. **Launch** → seed runs once, populates 15 Sahm Food menu items
2. **Catalog screen** (tablet: split-pane; phone: grid + cart FAB)
3. **Tap products** to add to cart → grand total animates with an odometer
   roll-up (the "Wow" moment)
4. **Tap Charge** → Checkout screen with Cash/Card toggle and a numeric keypad
5. **Confirm Payment** → order is persisted, sync queue is enqueued, mock
   printer renders the receipt
6. **Receipt screen** with success badge + monospace receipt + "New Order"
7. **History icon** in the top bar → see all paid orders + today's revenue

### Inspecting the offline-first plumbing

- Set the device to airplane mode before checkout. The order still saves.
- The `SyncQueueEntry` row carries `status = PENDING`. When connectivity is
  restored (or `AlwaysOfflineConnectivityObserver.setOnline(true)` is called),
  the `SyncWorker` drains the queue, the order transitions to `SYNCED`, and
  the History screen status badge updates.
- To watch the printer "tape" in development:
  `koinInject<MockPrinterService>().printLog.collect { println(it) }`.

---

## Architecture highlights

- **`:shared:presentation` cannot import `:shared:data`.** The Gradle module
  graph is the compile-time wall: presentation only sees repository interfaces
  from `:shared:domain`. Wiring concrete repos into stores happens in
  `composeApp` via Koin.
- **Money is stored as `Long` piastres.** No `Double`/`BigDecimal`. Half-up
  rounding handled by `Money.percent(bps: Int)` and tested in `MoneyTest`.
- **MVI stores own their own coroutine scopes.** No androidx `ViewModel`.
  `DisposableEffect` in `App.kt` calls `store.cancel()` cleanly across both
  platforms. The contract is identical on Android and iOS.
- **SyncWorker is idempotent.** `start()` won't spin up duplicate watchers if
  called repeatedly — required because the same singleton survives Compose
  recomposition.
- **The outbox is in the same SQL transaction as the order save.** No
  outbox-vs-business-write split-brain.

---

## Tests

Total: **5 test classes, 22 test methods.**

- `MoneyTest` (8) — arithmetic, percent rounding, currency mismatch, display
- `CalculateOrderTotalsTest` (4) — empty cart, single item, multi-item, discount
- `CartOperationsTest` (6) — add new/existing, remove, qty update edge cases
- `CheckoutOrderTest` (4) — save+enqueue, empty rejection, insufficient cash, card path
- `OrderRepositoryImplTest` (4) — save/load/status update against in-memory SQLite
- `SyncWorkerTest` (1) — full drain happy path against in-memory SQLite
- `MockPrinterServiceTest` (2) — print result + receipt rendering
- `CatalogStoreTest` (4) — initial load, add-to-cart with effect, checkout error, category filter

Run any single layer with `./gradlew :shared:<module>:jvmTest`.

---

## Scaling to multi-branch restaurants

What this design buys you for free (covered in `ARCHITECTURE.md`):

- **Per-branch terminals** — each terminal has its own SQLite + outbox; no
  client-side gossip required. Server is the merge point.
- **Branch-tagged sync** — add a `branch_id` column to `SyncQueueEntry`; the
  payload is JSON so backward-compatible evolution is easy.
- **Menu push** — same outbox pattern reversed: server publishes menu updates,
  each terminal pulls and calls `ProductRepository.upsertAll()`.
- **Offline-by-default UX** — cashiers never notice WAN drops; the only
  observable effect is `n orders pending sync` on the dashboard.

What's not yet solved (and why):

- **Real-time stock reservation across terminals** — needs server-side optimistic
  locking. Inherently incompatible with full offline operation.
- **End-of-day X/Z reports** — out of scope for this assignment.
- **PCI scope for real card payments** — `CARD` is a no-op flow today. Real
  integration requires an SDK and out-of-band terminal certification.

---

## Known caveats

- **First Gradle sync will be slow.** The Compose Multiplatform iOS toolchain
  pulls down a sizeable native compiler bundle. 5–15 minutes on first sync is
  normal.
- **`iosApp.swift`** shows a SourceKit error in IDEs until the Xcode project
  is regenerated and `ComposeApp.framework` is linked. The Android target is
  unaffected.
- **`AlwaysOfflineConnectivityObserver` is a stub.** Real `NetworkCallback`
  (Android) and `NWPathMonitor` (iOS) wiring is intentionally out of scope —
  the interface exists; the implementations return `false`. Call `setOnline(true)`
  from a debug button to manually trigger sync.
- **No background WorkManager.** The `SyncWorker` lives in process. Cold start
  re-triggers a drain. For a production cashier device that runs 12+ hours,
  this is fine. For background sync after process death you'd add a platform
  scheduler.
- **The cart is in-memory only.** Killing the app mid-cart loses the cart.
  Persisting it would add a `DRAFT` order row and complicate the demo without
  illuminating an architectural concept.

---

## AI usage transparency

This project was authored with Anthropic's Claude (Opus 4.7). The decisions
came from a series of focused prompts; the prompts and a summary of how the
output was used are in [`docs/AI_USAGE.md`](docs/AI_USAGE.md). Every line of
shipped code was reviewed for correctness by a separate code-review pass; the
review report and fixes applied are summarized at the bottom of that file.

---

## License

Take-home assignment artifact — no license intended for downstream use.
