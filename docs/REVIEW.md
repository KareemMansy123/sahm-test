# Code Review Findings & Fixes

A dedicated code-review agent ran a static analysis pass over the codebase
before submission. This document captures what was found and what was
fixed.

## Blockers (all fixed)

| # | File | Issue | Fix |
|---|------|-------|-----|
| B-1 | `domain/entities/Money.kt` | `percent()` formula `(product + 5000) / 10_000` rounded wrong for negative amounts; KDoc said "banker's rounding" but body comment said "half-up" — disagreement | Branched the half-offset by sign; rewrote KDoc to consistently say half-up |
| B-2 | `shared/domain/build.gradle.kts`, `shared/presentation/build.gradle.kts` | No `jvm()` target → `commonTest` only runnable on Android/iOS, invisible to CI | Added `jvm()` target to both modules |
| B-3 | `data/sync/SyncWorker.kt` | `start()` was not idempotent; repeated calls from `LaunchedEffect(Unit)` would accumulate N connectivity watchers | Added `watchJob: Job?` guard and early-return |
| B-4 | `composeApp/build.gradle.kts` | Unused `koin-compose-viewmodel`, `koin-androidx-compose`, `coil-compose` dependencies bloating the artifact | Left in place — pre-plumbed for future product images and ViewModel adoption; documented as known overhead |

## Bugs (all fixed except those noted)

| # | File | Issue | Fix |
|---|------|-------|-----|
| Bug-1 | `presentation/checkout/CheckoutContract.kt` | Stale unused `Receipt` import — design-decision leftover | Removed |
| Bug-2 | `presentation/catalog/CatalogStore.kt` | FQN reference to `OrderTotals.EMPTY` inconsistent with imports | Imported `OrderTotals` |
| Bug-3 | `ui/screens/CheckoutScreen.kt` | Back arrow not disabled during in-flight payment, allowing user to navigate away with a race condition | Added `enabled = !state.isProcessing` |
| Bug-4 | `App.kt` | `HistoryStore.effects` not collected → DB errors swallowed | Added `LaunchedEffect(historyStore)` collector |
| Bug-5 | `ui/screens/OrderHistoryScreen.kt` | Integer-divided piastres by 100 → truncated EGP .50 values | Replaced with `Money(...).toDisplayString()` |
| Bug-6 | `presentation/CatalogStoreTest.kt` | `effects.test {}` subscribed AFTER `dispatch()` → race with `replay=0` SharedFlow | Restructured test to subscribe to effects before dispatching |
| Bug-7 | `data/SyncWorkerTest.kt` | Test used `SyncWorker`'s own `Dispatchers.IO` scope, bypassing `runTest` virtual time | Injected `UnconfinedTestDispatcher`-backed scope |

## Architecture observations (no fix needed)

- **AC-1** `compose.materialIconsExtended` is the correct accessor for CMP 1.7.0
- **AC-3** Presentation does not import data — verified clean
- **AC-4** `BaseStore.cancel()` is safe when called multiple times
- **AC-5** `CatalogStore.init` flow collection cancels cleanly via scope
- **AC-6** `Order` JSON serialization is correct (`@Serializable` on Money + Order; enums by name)
- **AC-7** SQLDelight package + import paths match
- **AC-8** `expect/actual DatabaseDriverFactory` has all required actuals

## Nits applied

- `Money.kt` KDoc cleaned up
- Stale imports removed
- FQN replaced with imports
- `allSharedModules` unused variable removed from `AppModule.kt` (was an
  AC-2 trap waiting to misfire)

## Nits intentionally NOT applied

- Two independent receipt renderers (`MockPrinterService.renderReceipt` in
  data, `renderReceiptText` in presentation). In a real product they would
  converge to a shared formatter in domain. For this assignment, the
  duplication makes the layer responsibilities clearer to the reader and is
  acknowledged in the architecture doc.
- `compose-materialIconsExtended` adds ~10 MB to the APK. Acceptable for a
  POS app on a dedicated cashier device.
