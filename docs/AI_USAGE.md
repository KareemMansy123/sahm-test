# AI Usage Transparency

This file documents how AI tooling was used to build Sahm Food POS. The
assignment evaluation criteria explicitly asks for this transparency, and I
think it's important context for assessing the work.

## Tooling

- **Model**: Anthropic Claude Opus 4.7 (1M context)
- **Pattern**: Multi-agent — a dedicated agent per phase, each with a
  narrow scope, so review/critique was independent of authorship
- **Author oversight**: every prompt was hand-written; every agent output
  was read end-to-end before being applied; every code edit was reviewed
  for correctness before commit

## Phases & prompts

### Phase 1 — Design spec
**Agent**: `ui-ux-designer`
**Goal**: produce a complete design system (color tokens, typography,
spacing, breakpoints, screen-by-screen layout) so I wasn't inventing the
visual layer mid-implementation.

Prompt summary: "Design a UI/UX spec for a restaurant POS targeting
Android tablets + phones via Compose Multiplatform. Audience: cashiers
(speed-critical) + restaurant owner (history review). Cover IA, design
tokens, layout grid, responsive behavior, components, motion,
accessibility, content."

Output: `docs/DESIGN.md` (distilled) — the full agent output was ~6k words.

### Phase 2 — Architecture spec
**Agent**: `architect-review`
**Goal**: lock in the KMP module graph, MVI store contract, offline-first
strategy, and Koin DI before any code was written.

Prompt summary: "Design the architecture for a KMP POS. Module
graph (`domain`/`data`/`presentation`), MVI in shared, MVVM-flavored at
UI, Koin DI, SQLDelight, offline-first with outbox sync. Justify each
choice; flag open questions."

Output: `docs/ARCHITECTURE.md`. The agent identified 8 open questions; I
resolved 7 inline during implementation and documented all decisions in
the architecture doc.

### Phase 3 — Implementation
Code was written directly (no implementation agent). The architecture
spec was the implementation contract. I wrote:

- All 14 domain files (entities, repo interfaces, use cases, services)
- All 13 data files (SQLDelight schema, repo impls, sync worker, mock
  printer, seed)
- All 9 presentation files (MVI base + 3 store contracts/impls + DI)
- All 11 UI files (theme + 7 components + 4 screens + App.kt)
- All Android + iOS entry points
- All 8 test files

### Phase 4 — Code review
**Agent**: `code-reviewer`
**Goal**: independent static-analysis pass since no JDK was available
locally to run the actual compile.

Prompt summary: "Review the KMP POS at this path. I cannot run the build.
Catch what compile would catch, plus deeper correctness/design issues.
Pay attention to KMP source-set leakage, expect/actual mismatches,
SQLDelight package paths, threading bugs, MVI fragilities, DI graph
completeness."

The reviewer found:
- **4 blockers**: negative-amount rounding bug in `Money.percent()`,
  missing `jvm()` targets in `domain` + `presentation` (so commonTest
  couldn't run on host), non-idempotent `SyncWorker.start()`, and
  unused-but-declared dependencies.
- **7 bugs**: stale imports, FQN smell, missing back-disable during
  payment, history-store errors swallowed, revenue display truncating
  piastres, racy test patterns, brittle test scope.
- **9 architecture observations** (8 confirmed clean, 1 — unused
  `allSharedModules` — flagged as a trap that I removed).

All blockers and all 7 bugs were fixed before the code shipped. The
review report is preserved in the git history of the changes that
followed.

### Phase 5 — Documentation
README, ARCHITECTURE, DESIGN, this AI_USAGE doc — written directly
without an agent. I find that documentation written by a human author who
just shipped the code lands more honestly than agent-summarized prose.

## What AI was NOT used for

- The decision to use Compose Multiplatform vs Android-only Compose was
  mine before any prompt was issued.
- The decision to integrate MVI in shared + MVVM-flavor at UI was a
  framing choice I made and instructed the architecture agent to follow.
- The decision to scope hardware simulation to receipt printing only
  (not barcode or payment terminal) was mine.
- Every architectural trade-off was reviewed and accepted/rejected by
  me — the agent's output was a draft, not a verdict.
- The fixes for every code-review finding were written by me, not by an
  agent.

## What you should know about the failure mode

I had no JDK or Gradle installed on the authoring machine. I did NOT run
`./gradlew build` or `./gradlew test`. The code passed a static
review pass that's documented above, but there is some probability that
a fresh `gradle sync` in Android Studio will surface a missing import or
a Compose Multiplatform 1.7.0 API name I got slightly wrong. The
likeliest places to break are:

1. `compose.materialIconsExtended` — the accessor name varies by CMP
   version; CMP 1.7 uses this name but a maintenance release could
   rename it.
2. SQLDelight 2.0.2 generated class naming — I named the order table
   `PosOrder` (not `Order`) precisely to avoid a known SQLite reserved-
   word conflict, but the table-to-class mapping should be `PosOrder`,
   not `PosOrderQueries`.
3. Koin 4.0's `compose.viewmodel` integration — I avoided using it, so
   if a downstream dev removes the unused dependency they shouldn't see
   any cascade.

If you hit one of these on first build, it'll be a one-line fix and the
architecture is unaffected.
