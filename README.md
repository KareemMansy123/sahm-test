# Sahm Food POS

A point-of-sale app for a quick-service restaurant chain, built as a Kotlin
Multiplatform take-home assignment to evaluate real engineering thinking — not
just "make it work," but how the code is structured, tested, and presented.

Shared business logic targets **Android, iOS, and JVM (for unit tests)**. UI is
**Compose Multiplatform**, running on both Android and iOS from a single
Compose UI tree.

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
2. [Prompting methodology](#prompting-methodology)
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
- **Premium UI matching reference food-delivery apps like Nana and Sahm** —
  orange brand gradient, pastel-cycled category circles, signature hero
  banner, sliding bottom-nav pill, NFC tap-card pulse on checkout,
  animated order tracker. ✅
- **Multi-agent workflow** — implementation used dedicated specialist
  agents for design audit, architecture review, code review, and
  testing, orchestrated through a phase-gated pipeline (see prompting
  methodology below). ✅
- **Offline-first persistence** — Room stores cart, favorites, orders,
  chat history, and preferences; everything survives app kill. ✅
- **Bilingual + RTL** — every visible string and 15 seed menu items
  translated to Egyptian Arabic; toggling language re-composes the tree
  with new copy and flips layout direction. ✅
- **Animations** — single `SahmMotion` token file feeding press scales,
  staggered list enters, page transitions, status pill cross-fades,
  banner breathing, focus glows, status pulses. ✅

---

## Prompting methodology

I designed the architecture, module boundaries, design language, and
acceptance criteria up front. Implementation was accelerated with AI
coding assistants, but every prompt was a structured engineering
instruction — not a conversation. The methodology below is the
playbook I used. It treats the AI like a junior engineer on the team:
given a precise role, a phase to operate in, exact files to touch, and
a definition of done.

### 1. Role definition (the system prompt)

Every session opened by establishing **who the assistant is**, **what
they're allowed to do**, and **what they must never do**. The prompt
that opened every implementation session:

> You are a senior Kotlin Multiplatform engineer working on a
> production POS for a quick-service restaurant chain. Stack: KMP
> 2.0, Compose Multiplatform 1.7, Room 2.7 KMP, Koin 4, MVI.
>
> Hard rules:
> - `shared/domain` depends on NOTHING except kotlinx-coroutines,
>   kotlinx-datetime, kotlinx-serialization. No Android, no Room, no
>   Compose imports.
> - MVI stores never import repositories. They invoke use cases only.
> - All async work goes through the injected `DispatcherProvider`.
>   Zero `Dispatchers.IO` literals in shared code.
> - Repositories return `AppResult<T>`, never throw.
> - No new dependencies without explicit approval.
>
> When you don't know something, read the file. Don't guess. When you
> finish a change, build it. Don't claim completion without evidence.

### 2. Phase-gated pipeline

Work flowed through four explicit phases. Each phase has its own
prompt shape and its own success criterion. **No phase starts until
the previous one's artefact exists.**

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│   PLAN   │ ─▶ │  BUILD   │ ─▶ │  REVIEW  │ ─▶ │   TEST   │
└──────────┘    └──────────┘    └──────────┘    └──────────┘
   Plan          Diff against   Two-pass         Unit tests
   agent         the plan       review:          +
   produces      only.          architect-       on-device
   step list     No scope       review +         smoke
   + file        creep.         security-        check.
   manifest.                    audit.
```

#### Phase 1 — Plan

Use the `Plan` agent. Output is **a step list and a file manifest**,
not code:

> Plan the implementation of the runtime i18n system. Constraints:
> Compose-MPP XML resources don't support runtime locale swap without
> Activity restart. We need both runtime switch AND RTL flip without
> recreate.
>
> Deliverables:
> 1. List the files that will be created (interface, impls, provider).
> 2. List the files that will be modified (theme wrapper, every
>    screen, every component with hardcoded text).
> 3. Identify the failure modes (missing keys, persistence flash on
>    cold launch, AI store keeping English replies).
> 4. Specify the verification command for each phase.
>
> Under 250 words.

#### Phase 2 — Build

The build agent gets the plan, plus explicit scope guardrails:

> Implement steps 1-3 from the plan. Do not touch the AI store yet —
> that's step 5 and we'll do it after review. Use `git status` after
> every file to confirm scope.
>
> When done: run `./gradlew :composeApp:compileDebugKotlinAndroid`.
> If it fails, fix and re-run. Do not move on until green.

#### Phase 3 — Review (two passes)

A `code-reviewer` agent and an `architect-review` agent get
**independent** prompts so they don't anchor on each other:

> code-reviewer: Review the diff on this branch. Look for: implicit
> defaults that hide missing translations, places where a hardcoded
> English string slipped through, composables that read
> `Locale.getDefault()` instead of the CompositionLocal.

> architect-review: Verify the dependency rules hold. Specifically:
> grep `shared/presentation/` for `domain.repositories.` imports —
> there should be zero. Grep shared code for `Dispatchers.IO` —
> there should be zero. Report violations with file:line.

#### Phase 4 — Test

Before any commit:

> Run `:shared:domain:testDebugUnitTest` and
> `:shared:presentation:testDebugUnitTest`. Then
> `:composeApp:assembleDebug`. Then install on device:
> `./gradlew :composeApp:installDebug`. Report each step's exit
> status. Do not commit if any step is non-green.

### 3. Agent orchestration

Specialist agents stay sharper than the catch-all. Mapping I used:

| Phase                       | Agent type           | Why                                          |
| --------------------------- | -------------------- | -------------------------------------------- |
| Discovery / planning        | `Plan`               | Outputs structured plans, not implementation |
| Reference app teardown      | `general-purpose`    | Read-only repo crawl, file-by-file inventory |
| Architecture verification   | `architect-review`   | Knows clean-arch dependency rules            |
| Diff review                 | `code-reviewer`      | Catches issues that the implementer missed   |
| Test scaffolding            | `test-automator`     | Fixture + fake patterns built in             |
| Debugging device crashes    | `debugger`           | Stack-trace-first triage                     |

Every agent invocation specified the **type explicitly** — never the
default. Mixed-purpose agents drift; specialists deliver.

### 4. Constraint phrasing — what NOT to do

Negative constraints prevented more bugs than positive instructions:

- "Never delegate understanding. Don't write 'based on the findings,
  fix the bug' — include file paths and exact line numbers."
- "Stores never reference repositories directly — only use cases."
- "Don't add swipe-to-delete; that's been explicitly scoped out."
- "No new dependencies. Room, Koin, and Compose Animation are already
  on the classpath; use them."
- "Don't amend the previous commit. Create a new one."

### 5. Evidence-based completion

The single biggest lever for output quality: **demand artefacts, not
claims**.

> Don't tell me it's done. Show me:
> 1. `./gradlew :composeApp:assembleDebug` exit code.
> 2. Output of `grep -rn "Plaza\|plaza" --include="*.kt"` (must be
>    empty).
> 3. `git log --oneline -1` showing the commit you just made.

Without this, the assistant declares victory after edits that fail to
compile. With it, completion is mechanical.

### 6. Commit discipline

Every commit follows the same skeleton, enforced by the prompt:

> Commit message format:
> - One-line subject (under 70 chars, imperative mood).
> - Blank line.
> - **Why** the change was needed (the problem, in plain English).
> - **What** specifically changed (files, identifiers, behaviour).
> - **What stays intentionally untouched** (so future readers don't
>   wonder).

This prevented mega-commits and made `git log` a usable changelog.

### 7. Feedback loop — diagnose before implementing

When something looked wrong on device, the next prompt was always a
**diagnosis prompt** before any code change:

> Don't fix yet. First: enumerate every animation currently wired in
> the app. Then list every visible surface that has no animation.
> Then propose the smallest set of changes that closes the gap.
> Report under 400 lines. I'll approve the plan before you write
> code.

The audit output became the implementation TODO list. This eliminated
the "fix one thing, break two others" loop.

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
│               ├── components/       21 reusable Sahm-branded components
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
    │   ├── SahmBottomNav.kt             # Sliding indicator pill + icon pop
    │   ├── SahmHomeBanner.kt            # Breathing decorative circles
    │   ├── SahmSearchBar.kt             # Focus glow border
    │   ├── SahmOrderTracker.kt          # Animated step circles
    │   ├── SahmFloatingCartFab.kt       # Pulse trigger on cart add
    │   ├── AiFloatingButton.kt          # Radiating pulse ring
    │   ├── ProductCard.kt, CartLineItem.kt, CategoryStrip.kt, …
    │   └── (21 components total)
    ├── theme/
    │   ├── SahmColors.kt                # Brand orange + neutral ramp
    │   ├── SahmTypography.kt
    │   ├── SahmDimensions.kt            # Spacing, radius, duration tokens
    │   ├── SahmMotion.kt                # Springs, enter/exit specs, Modifier.pressScale
    │   ├── SahmShadows.kt               # Card shadow recipes
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

- **Browse** the home tab: pastel category circles, product grid,
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

### iOS simulator

Requires **Xcode 15+** and an iOS 14.1+ simulator runtime.

**Option A — Xcode (recommended for development):**

```bash
open iosApp/iosApp.xcodeproj
```

In Xcode, pick an iPhone simulator (e.g. iPhone 16) from the run-target
dropdown and press ⌘R. The project's *Compile Kotlin Framework* build
phase calls `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode`
automatically, so the Kotlin/Native framework is built before Xcode
links the Swift host. First build takes a few minutes (Kotlin/Native
caches a lot on first compile).

**Option B — command line (one-shot install on the booted simulator):**

```bash
# 1. Boot a simulator (skip if one is already booted)
xcrun simctl boot "iPhone 16"
open -a Simulator

# 2. Build the iOS app
cd iosApp
xcodebuild -project iosApp.xcodeproj \
           -scheme iosApp \
           -configuration Debug \
           -sdk iphonesimulator \
           -destination "platform=iOS Simulator,name=iPhone 16" \
           build

# 3. Install + launch on the booted simulator
APP_PATH=$(find ~/Library/Developer/Xcode/DerivedData/iosApp-* \
             -name iosApp.app -path "*/Debug-iphonesimulator/*" | head -1)
xcrun simctl install booted "$APP_PATH"
xcrun simctl launch  booted com.sahmfood.pos
```

**How the iOS side is wired (so you know what to debug if something
breaks):**

| Piece                                              | Where                                                      |
| -------------------------------------------------- | ---------------------------------------------------------- |
| Swift entry point                                  | `iosApp/iosApp/iosApp.swift` — wraps a `ComposeUIViewController` |
| Kotlin entry point                                 | `composeApp/src/iosMain/.../MainViewController.kt`         |
| Room driver (BundledSQLite)                        | `shared/data/src/iosMain/.../DatabaseFactory.ios.kt`       |
| `Dispatchers.IO` shim (Kotlin/Native has no `IO`)  | `shared/data/src/iosMain/.../IoDispatcher.ios.kt` returns `Dispatchers.Default` |
| Xcode build phase that compiles Kotlin             | `iosApp/iosApp.xcodeproj` → target *iosApp* → *Compile Kotlin Framework* phase |
| Required Info.plist key                            | `CADisableMinimumFrameDurationOnPhone=true` (Compose-MPP iOS hard-requires this) |

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

- **iOS polish pass** — the app installs and runs on the iOS
  simulator with the full Compose UI tree, but I haven't tuned
  scroll physics, hand-checked frame rate under load, or shipped
  iOS-specific touches (haptic feedback, swipe-back gesture).
- **Snapshot tests** for every UI component using Paparazzi-style
  rendering (or the new Roborazzi for Compose).
- **Real sync worker** — `SyncWorker` and `StubRemoteApiService` are
  scaffolded but never fire. Wiring them to a real backend (or a
  Ktor-server fake) would close the offline-first story.
- **Accessibility audit** — content descriptions exist but I haven't
  walked the app with TalkBack yet. Likely 5-10 things to fix.
- **Hardware printer** — `PrintReceipt` is a stub. A real ESC/POS
  driver via `expect`/`actual` would be a satisfying capstone.

---

Every commit follows the same skeleton: **why** the change was needed,
**what** specifically changed, **what stays intentionally untouched**.
Read `git log` for the full story.
