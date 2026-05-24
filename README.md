# Sahm Food POS

A point-of-sale app for a quick-service restaurant chain, built as a Kotlin
Multiplatform take-home assignment to evaluate real engineering thinking — not
just "make it work," but how the code is structured, tested, and presented.

Shared business logic targets **Android, iOS, and JVM (for unit tests)**. UI is
**Compose Multiplatform**, currently rendered on Android with the iOS target
wired and compiling.

| Layer            | Tech                                                                        |
| ---------------- | --------------------------------------------------------------------------- |
| Language         | Kotlin 2.0.21 (K2 compiler)                                                 |
| UI               | Compose Multiplatform 1.7.0, Material 3                                     |
| Architecture     | Clean Architecture · MVI (Reducer + Middleware) at the boundary             |
| Persistence      | **Room 2.7 KMP** (`@ConstructedBy(RoomDatabaseConstructor)` + `BundledSQLiteDriver`) |
| DI               | Koin 4.0 (`dataModule`, `domainModule`, `presentationModule`, `platformModule`) |
| Async            | Kotlin Coroutines 1.9 + Flow, injected `DispatcherProvider`                 |
| i18n             | Hand-rolled `SahmStrings` interface, English + Egyptian Arabic, runtime switch + RTL |
| Tests            | JUnit 4 + Turbine for `Flow` assertions                                     |

---

## Table of contents

1. [Take-home task requirements](#take-home-task-requirements)
2. [How the AI prompts that built this app were structured](#how-the-ai-prompts-that-built-this-app-were-structured)
3. [Module map](#module-map)
4. [Architecture in 90 seconds](#architecture-in-90-seconds)
5. [Design patterns in play](#design-patterns-in-play)
6. [Folder structure](#folder-structure)
7. [Feature surface](#feature-surface)
8. [Running the project](#running-the-project)
9. [Testing](#testing)
10. [What I'd do with another week](#what-id-do-with-another-week)

---

## Take-home task requirements

The brief asked for a Kotlin Multiplatform POS module that demonstrates real
engineering thinking, not a tutorial-grade app. Concretely:

- **KMP with Compose Multiplatform** — Android primary, iOS via shared
  codebase. ✅ All three target sets (`androidMain`, `iosMain`, `commonMain`)
  compile, with `expect`/`actual` for the database driver.
- **Clean architecture, separated modules** — `domain` (entities + use cases
  + repository interfaces, zero framework deps), `data` (Room impls +
  mappers), `presentation` (MVI stores, no Compose imports). ✅
- **MVI / MVVM patterns** — every screen-level store is `BaseStore<S, I, E>`,
  with the checkout flow split further into a pure `CheckoutReducer` +
  side-effecting `CheckoutMiddleware`. ✅
- **Use cases & repository abstractions** — stores never reference
  repositories directly; they invoke `GetProductCatalog`, `AddToCart`,
  `CheckoutOrder`, `RankItemsByVolume`, etc. ✅
- **Dependency Injection** — Koin modules per concern, platform-specific
  Android Context + DB injected via `platformModule`. ✅
- **Premium UI matching Nana / Sahm / Plaza reference quality** — pixel-
  faithful port of the Plaza order-app design language: orange brand
  gradient, pastel-cycled category circles, Plaza-signature hero banner,
  sliding bottom-nav pill, NFC tap-card pulse on checkout, animated order
  tracker. ✅
- **Multi-agent workflow** — the build used dedicated agents for design
  audit, architecture review, code review, and testing (see prompts
  section below). ✅
- **Offline-first persistence** — Room stores cart, favorites, orders,
  chat history, and preferences; everything survives app kill. ✅
- **Bilingual + RTL** — every visible string and 15 seed menu items
  translated to Egyptian Arabic; toggling language re-composes the tree
  with new copy and flips layout direction. ✅
- **Animations** — single `SahmMotion` token file feeding press scales,
  staggered list enters, page transitions, status pill cross-fades,
  banner breathing, focus glows, status pulses. ✅

---

## How the AI prompts that built this app were structured

The app was built collaboratively with Claude over multiple sessions. The
prompts followed a few rules that made the output usable instead of generic.

### 1. State the brief once, then refer back to it

The very first prompt was the full assignment text — KMP, MVI/MVVM, clean
arch, use cases, DI, premium UI, multi-agent. Every later prompt referenced
"the brief" instead of restating requirements. This kept the model anchored.

### 2. Reference real apps, not adjectives

> "look at Plaza-app, Nana, Sahm — clone the design language, don't invent
> one"

Vague prompts like "make it beautiful" produce Material-default UIs. Naming
specific apps the model could pattern-match against produced the orange
gradient, pastel cycles, and signature decorative circles.

### 3. Delegate audits to dedicated agents

For non-trivial review tasks the prompts always specified a sub-agent type:

> "Use the general-purpose agent to audit `/Users/.../Plaza-app` and produce
> a 1300-line widget-anatomy breakdown to `/tmp/plaza-anatomy.md`. Don't
> propose changes, just enumerate."

> "Use the code-reviewer agent to second-opinion this migration."

Specialised agents stay focused; the catch-all sub-agent drifts.

### 4. Be specific about what NOT to do

The most effective constraint phrasings:

- "Never delegate understanding. Don't write 'based on findings, fix the
  bug' — include file paths and exact line numbers."
- "Stores never reference repositories directly — only use cases."
- "Don't add a swipe-to-delete, the user opted out of it."
- "No new dependencies — Room and Koin are already on the classpath."

### 5. Demand evidence before claiming completion

> "After implementing, run `./gradlew :composeApp:assembleDebug` and
> `:shared:presentation:testDebugUnitTest`. Don't commit until both are
> green."

Catches the model's tendency to declare victory before verifying.

### 6. Single-purpose commits with structured messages

Every commit message followed the same skeleton: **why we needed the change**,
**what specifically changed**, **what stays intentionally untouched**. This
prevented mega-commits and made history readable.

### 7. Iterate on real user feedback, not assumptions

When something looked wrong on device ("bottom nav is so bad", "language
switch did nothing visible"), the prompts forced a diagnosis pass first:

> "Survey what's ALREADY animated before adding anything, so I don't
> duplicate. Then list which surfaces are still static. Then we'll plan."

The audit output became the implementation TODO list.

---

## Module map

```
sahm-food-pos/
├── composeApp/                       Android entry point + all Compose UI
│   └── commonMain/
│       └── com/sahmfood/pos/
│           ├── App.kt                Root nav host (custom AnimatedContent route stack)
│           ├── di/AppModule.kt       Wires Koin modules together
│           └── ui/
│               ├── screens/          13 screens (Catalog, Cart, Checkout, …)
│               ├── components/       21 reusable Plaza components
│               ├── theme/            SahmColors, SahmTypography, SahmMotion
│               └── strings/          SahmStrings + English/Arabic implementations
│
├── shared/domain/                    Pure Kotlin — no framework deps
│   └── commonMain/
│       └── com/sahmfood/pos/domain/
│           ├── entities/             Product, Money, Order, CartItem, Receipt, …
│           ├── repositories/         7 interfaces (Cart, Product, Order, Favorites, …)
│           ├── usecases/             12 use case files, single-responsibility each
│           ├── services/             AppClock, IdGenerator, DispatcherProvider
│           ├── common/               AppResult<T>, AppError sealed hierarchy
│           └── di/DomainModule.kt    Use case bindings
│
├── shared/data/                      Room impls + mappers + platform glue
│   └── commonMain/
│       └── com/sahmfood/pos/data/
│           ├── db/                   SahmDatabase (@Database), DAOs, entities
│           ├── repositories/         7 repo impls (delegate to DAOs via mappers)
│           ├── mappers/              9 Mapper objects (entity ↔ domain), 1 base
│           ├── sync/                 Stub remote API + offline queue scaffold
│           ├── seed/                 MenuSeedData (15 bilingual products)
│           └── di/DataModule.kt + PlatformModule (expect/actual)
│
└── shared/presentation/              MVI stores — no Compose imports
    └── commonMain/
        └── com/sahmfood/pos/presentation/
            ├── common/               BaseStore, Reducer, Middleware contracts
            ├── catalog/              CatalogStore + CatalogContract
            ├── checkout/             CheckoutReducer (pure) + CheckoutMiddleware (effects)
            ├── favorites/, ai/, history/, settings/
            └── di/PresentationModule.kt
```

---

## Architecture in 90 seconds

```
┌────────────────────────────────────────────────────────────────────────┐
│  UI (composeApp)                                                       │
│                                                                        │
│  Composable ── collectAsState ──▶ Store.state (StateFlow<S>)           │
│      ▲                                                                 │
│      │ dispatch(intent)                                                │
└──────┼─────────────────────────────────────────────────────────────────┘
       │
┌──────▼─────────────────────────────────────────────────────────────────┐
│  Presentation (shared:presentation)                                    │
│                                                                        │
│  Store ──► Reducer (pure: S × I → S) ──► new state                     │
│       └──► Middleware (S × I → Flow<I>) ──► more intents               │
│                                  │                                     │
└──────────────────────────────────┼─────────────────────────────────────┘
                                   │ uses
┌──────────────────────────────────▼─────────────────────────────────────┐
│  Domain (shared:domain)                                                │
│                                                                        │
│  UseCase ──invoke()──▶ Repository (interface)                          │
│                                                                        │
│  Returns AppResult<T> = Success(T) | Failure(AppError)                 │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │ implemented by
┌──────────────────────────────────▼─────────────────────────────────────┐
│  Data (shared:data)                                                    │
│                                                                        │
│  RepositoryImpl ──► DAO (Room)                                         │
│                ──► Mapper (Entity ↔ Domain)                            │
└────────────────────────────────────────────────────────────────────────┘
```

Key rules enforced in the codebase:

- **Domain depends on nothing.** Only `kotlinx-coroutines`, `kotlinx-datetime`,
  and `kotlinx-serialization`. No Android, no Room, no Compose.
- **Stores never import repositories.** Only use cases. Grep for `import
  com.sahmfood.pos.domain.repositories` inside `shared/presentation/` —
  zero hits.
- **All side-effects via `DispatcherProvider`.** No `Dispatchers.IO`
  literals in shared code.
- **All failures via `AppResult<T>`.** No raw `try/catch` leaking from
  repositories.

---

## Design patterns in play

| Pattern                     | Where & why                                                                                       |
| --------------------------- | ------------------------------------------------------------------------------------------------- |
| **Clean Architecture**      | 3-tier module split, dependency rule strictly enforced (domain ← presentation ← UI; data ← domain) |
| **MVI**                     | `BaseStore<S, I, E>` produces `StateFlow<S>` + `SharedFlow<E>` for one-shot effects               |
| **Reducer + Middleware**    | `CheckoutStore` splits pure transition (`Reducer`) from async work (`Middleware`) — easy to test  |
| **Repository pattern**      | 7 interfaces in domain, 7 impls in data; swap Room for fake in tests                              |
| **Use case (Interactor)**   | Single-responsibility callable objects: `AddToCart`, `CheckoutOrder`, `RankItemsByVolume`         |
| **Mapper**                  | 9 dedicated `Mapper<E, D>` objects keep entity ↔ domain conversion in one place                   |
| **Result type**             | `AppResult<T>` + `AppError` sealed hierarchy replaces exception-based control flow                |
| **Dependency Injection**    | Koin modules per concern; `expect`/`actual` for platform-specific bindings                        |
| **Factory + expect/actual** | `DatabaseFactory` abstracts Room driver construction across Android/iOS                           |
| **CompositionLocal**        | `LocalSahmStrings` for runtime i18n; `LocalLayoutDirection` for RTL                               |
| **Strategy**                | `DispatcherProvider` interface + `DefaultDispatchers` impl — swap for `UnconfinedTestDispatcher` in tests |
| **Observer (Flow)**         | Repositories expose `Flow<T>` so screens auto-update; `StateFlow` for hot state                   |
| **Token pattern**           | `SahmSpacing`, `SahmRadius`, `SahmDurations`, `SahmSprings` — every magic number lives in one file |

---

## Folder structure

```
composeApp/src/commonMain/kotlin/com/sahmfood/pos/
├── App.kt                               # Root composable + AnimatedContent nav stack
├── di/
│   └── AppModule.kt                     # Aggregates Koin modules for the app
└── ui/
    ├── screens/
    │   ├── MainScreen.kt                # 5-tab HorizontalPager host
    │   ├── CatalogScreen.kt             # Home tab — banner, search, category strip, product grid
    │   ├── CartScreen.kt
    │   ├── CategoryProductsScreen.kt    # Full-screen drill-down from Categories tab
    │   ├── CheckoutScreen.kt            # Cash + tap-card flow with NFC pulse
    │   ├── ReceiptScreen.kt             # Animated success hero + thermal-print preview
    │   ├── OrderTrackingScreen.kt       # Auto-advancing Received → Preparing → Ready
    │   ├── OrderHistoryScreen.kt        # Filtered history + animated stat cards
    │   ├── FavoritesScreen.kt
    │   ├── AiChatScreen.kt              # Local-data assistant (no LLM call)
    │   ├── ProductDetailScreen.kt
    │   ├── ProfileScreen.kt
    │   └── SettingsStubScreens.kt       # Switch register, Printer, Preferences, Help
    ├── components/
    │   ├── PlazaBottomNav.kt            # Sliding indicator pill + icon pop
    │   ├── PlazaHomeBanner.kt           # Breathing decorative circles
    │   ├── PlazaSearchBar.kt            # Focus glow border
    │   ├── PlazaOrderTracker.kt         # Animated step circles
    │   ├── PlazaFloatingCartFab.kt      # Pulse trigger on cart add
    │   ├── AiFloatingButton.kt          # Radiating pulse ring
    │   ├── ProductCard.kt, CartLineItem.kt, CategoryStrip.kt, …
    │   └── (21 components total)
    ├── theme/
    │   ├── SahmColors.kt                # Brand orange + neutral ramp
    │   ├── SahmTypography.kt
    │   ├── SahmDimensions.kt            # Spacing, radius, duration tokens
    │   ├── SahmMotion.kt                # Springs, enter/exit specs, Modifier.pressScale
    │   ├── SahmShadows.kt               # Plaza card shadow recipes
    │   └── SahmTheme.kt                 # MaterialTheme wrapper + LocalSahmStrings provider
    └── strings/
        ├── SahmStrings.kt               # ~190-key interface
        ├── EnglishStrings.kt
        ├── ArabicStrings.kt             # Egyptian Arabic
        ├── LocalSahmStrings.kt
        └── ProductLocalization.kt
```

---

## Feature surface

What you can actually do in the app:

- **Browse** a Plaza-style home: pastel category circles, product grid,
  search with focus glow, breathing hero banner.
- **Open a product** in a detail sheet — quantity stepper with digit flip,
  bilingual name + category badge, Add-to-Order CTA.
- **Manage favorites** — heart toggle from the catalog, dedicated tab
  with animated list reorder + quick-add button.
- **Cart with persistence** — every line item is stored in Room and
  survives app kill; +/- buttons have press feedback, digit flips, list
  insertions and removals animate.
- **Checkout** — cash or tap-card. Card flow has the NFC ripple-pulse.
- **Receipt** — staggered success animation (check-circle pop → title
  slide → card fade → action row fade) followed by a thermal-print
  preview.
- **Order tracking** — Received → Preparing → Ready, auto-advances every
  4s for the demo, scooter floats on the live banner, status pill
  cross-fades.
- **Order history** — animated revenue/order stat cards, animated filter
  chips (all / cash / card / synced / pending), animated list rows.
- **AI assistant** — 4 quick-action chips (best sellers, pending orders,
  today's revenue, slowest item) backed by `RankItemsByVolume`,
  `GetTodayRevenueSummary`, `CountPendingSyncOrders` use cases — no LLM
  call, pure local data. Chat history is persisted.
- **Settings stubs** — Switch register, Printer settings, Preferences,
  Help & support — all UI-only mock data, intentionally not wired to
  hardware.
- **Theme + Language** — Light / Dark / System theme; English / Egyptian
  Arabic with full RTL flip and bilingual product names. Both persist in
  Room.

---

## Running the project

### Prerequisites

- JDK 17+ (Android Studio's bundled JBR works)
- Android SDK with API 34
- A device or emulator on API 24+

### Android

```bash
./gradlew :composeApp:installDebug
adb shell monkey -p com.sahmfood.pos -c android.intent.category.LAUNCHER 1
```

### iOS

Open `iosApp/iosApp.xcodeproj` in Xcode 15+ and run on a simulator. The
shared logic builds via `:shared:data:assembleXCFramework` and the iOS
UI lives in `iosApp/`.

### local.properties

If Gradle can't find the SDK, add:

```
sdk.dir=/Users/you/Library/Android/sdk
```

This file is gitignored.

---

## Testing

```bash
./gradlew :shared:domain:testDebugUnitTest \
          :shared:presentation:testDebugUnitTest
```

Existing tests:

- `MoneyTest` — currency arithmetic edge cases (zero, overflow, rounding)
- `CalculateOrderTotalsTest` — tax + discount math against fixed cases
- `CartOperationsTest` — add/update/remove invariants
- `CheckoutOrderTest` — full checkout use case with fakes
- `CatalogStoreTest` — MVI flow with `Turbine`

The pattern: every store gets a test that drives intents through a
`StandardTestDispatcher`, observes `state` via `Turbine`, and uses
hand-rolled `Fake*Repository` implementations from `commonTest`.

---

## What I'd do with another week

- **Real iOS rendering** — the iOS target compiles and the shared logic
  runs, but I haven't hand-tuned the SwiftUI host or verified the
  Compose Multiplatform iOS frame rate. Worth a few days.
- **Snapshot tests** for every Plaza component using Paparazzi-style
  rendering (or the new Roborazzi for Compose).
- **Real sync worker** — `SyncWorker` and `StubRemoteApiService` are
  scaffolded but never fire. Wiring them to a real backend (or a
  Ktor-server fake) would close the offline-first story.
- **Accessibility audit** — content descriptions exist but I haven't
  walked the app with TalkBack yet. Likely 5-10 things to fix.
- **Hardware printer** — `PrintReceipt` is a stub. A real ESC/POS
  driver via `expect`/`actual` would be a satisfying capstone.

---

Built with care over multiple sessions, with Claude as a pair programmer.
Every commit follows the same skeleton: **why** the change was needed,
**what** specifically changed, **what stays intentionally untouched**.
Read `git log` for the full story.
