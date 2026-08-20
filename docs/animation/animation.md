# WUtils Animation

`animation` is a lightweight API for orchestrating sequential and parallel
animations in Bukkit/Paper plugins — chains of timed effects such as
particles, sounds, fireworks and titles that can run one after another,
run alongside each other, or a mix of both.

- Directory: `animation/`
- Gradle project: `:WUtils-animation`
- Maven artifact: `io.github.wyne10:wutils-animation`
- Version: `2.2.3`
- Root package: `me.wyne.wutils.animation`

Source of these facts: `animation/build.gradle.kts`.

## Dependencies

| Dependency | Scope | Notes |
|---|---|---|
| `com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT` | `compileOnly` | Consumer must supply a Paper (or Paper-fork) runtime. See [Paper-only, below](#paper-only-not-spigot-compatible). |
| `org.javatuples:javatuples:1.2`                          | `implementation` | Bundled transitively. Supplies `Pair`, used internally to tie a running step to its `BukkitTask` handle. |

`animation` has **no dependency on any other WUtils module** — it is
usable standalone, and nothing else in this module's compileOnly/implementation
graph is optional besides Paper itself.

### Paper-only, not Spigot-compatible

The `compileOnly` dependency is `paper-api`, not `spigot-api`, and that is
not incidental: several of the built-in runnables
(`animation/src/main/java/me/wyne/wutils/animation/runnable/MessageEffect.java`,
`SoundEffect.java`, `TitleEffect.java`, and the second constructor of
`LocalSound.java`) take `net.kyori.adventure.audience.Audience` and other
Adventure types directly as parameters. Paper's `Player`/`CommandSender`
implement `Audience` natively as of 1.16.5; plain Spigot does not expose
Adventure without an extra bridge library. A plugin built only against
`spigot-api` cannot satisfy these method signatures.

## Package inventory

| Package | Contents |
|---|---|
| `me.wyne.wutils.animation` | The core model: `Animation`, the `AnimationStep` hierarchy, `AnimationRunnable`, `Finalizable`, `CompositeRunnable`. See [Steps and scheduling](steps.md). |
| `me.wyne.wutils.animation.data` | `AnimationParticle`, a reusable particle-spawn descriptor. See [Runnables](runnables.md). |
| `me.wyne.wutils.animation.runnable` | Ten ready-made `AnimationRunnable` effects (particles, sounds, fireworks, titles, etc). See [Runnables](runnables.md). |

`CompositeRunnable` physically lives in the root package alongside
`Animation`, but is documented in [Runnables](runnables.md) next to
`AnimationRunnable`, since its job is combining runnables rather than
scheduling.

## How the pieces fit together

An `Animation` is a queue of `AnimationStep`s. A step pairs an
`AnimationRunnable` (the effect — what to do) with timing (`delay`,
`period`, `duration`, all in ticks) and a scheduling strategy (sync/async,
blocking/parallel — see [Steps and scheduling](steps.md) for the full
model). Once started, steps drive each other forward: each step, when it
finishes (or, for parallel steps, as soon as it starts), triggers the next
one in the queue. `Animation` itself does not loop over the queue — it
only seeds the first step.

Effects are supplied as `AnimationRunnable` implementations. The
`animation.runnable` package ships ten common ones built on Bukkit/Adventure
APIs; `AnimationParticle` (`animation.data`) is a small reusable value type
several of them use to describe a particle spawn. Nothing stops a consumer
from implementing `AnimationRunnable` directly for custom effects.

## Entry points

- **`Animation`** (`animation/src/main/java/me/wyne/wutils/animation/Animation.java`) —
  construct with the owning `Plugin`, add steps with `addStep`,
  `addSteps`, or `addAnimation` (copies another `Animation`'s steps in,
  for composition), then call `run()` to start playback. Implements
  `AutoCloseable` (`close()` delegates to `stop()`), so it can be used in
  try-with-resources to guarantee cleanup.
- **`AnimationStep`** implementations — `BlockingAnimationStep`,
  `ParallelAnimationStep`, `AsyncBlockingAnimationStep`,
  `AsyncParallelAnimationStep` — are the four concrete ways to schedule an
  `AnimationRunnable`. Construct them directly and pass them to
  `Animation.addStep`/`addSteps`.
- **`AnimationRunnable`** — implement this (or use one of the
  `animation.runnable` classes) to define what a step actually does.

## See also

- [Steps and scheduling](steps.md) — the `Animation`/`AnimationStep`
  model: blocking vs. parallel, sync vs. async, how the chain advances,
  `close()` vs. `_finalize()`.
- [Runnables](runnables.md) — `AnimationRunnable`, `CompositeRunnable`,
  `AnimationParticle`, and the built-in effects.
