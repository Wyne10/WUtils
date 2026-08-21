# Runnables

This page covers `AnimationRunnable` and its supporting types, and the ten
ready-made effects in `me.wyne.wutils.animation.runnable`.

See also: [Animation module overview](animation.md), [Steps and scheduling](steps.md).

## `AnimationRunnable`

`AnimationRunnable` (`AnimationRunnable.java`) is a functional interface
extending `java.lang.Runnable`. Its single abstract method is inherited
from `Runnable` — `run()` — and it adds one default method,
`run(long delay, long period, long duration)`, which by default just
calls `run()`. Steps always invoke the three-argument overload
(`getRunnable().run(getDelay(), getPeriod(), getDuration())`, e.g.
`BlockingAnimationStep.java:30`), so overriding it is how a runnable
finds out its own timing without having those values passed to its
constructor separately.

Because the abstract method is plain `Runnable.run()`, any `Runnable`
lambda or method reference already satisfies `AnimationRunnable`
directly — no adapter is needed. `AnimationRunnable.EMPTY` is a no-op
instance, useful as a default/placeholder value. `AnimationRunnable
.runnable(Runnable)` (`AnimationRunnable.java:38-41`) is a static factory
that wraps a plain `Runnable`, ignoring the timing arguments — it is
marked `@Deprecated`, consistent with it being redundant given the point
above.

A runnable can optionally implement `AutoCloseable` and/or `Finalizable`
to participate in step cleanup — see [`close()` vs.
`_finalize()`](steps.md#close-vs-_finalize) in Steps and scheduling.
None of the built-in effects on this page do; they are stateless,
fire-and-forget calls with nothing to release.

## `CompositeRunnable`

`CompositeRunnable` (`CompositeRunnable.java`) is a record wrapping a
`Collection<AnimationRunnable>`. It implements `AnimationRunnable`,
`AutoCloseable`, and `Finalizable`, and is how several independent
effects are attached to a single step:

- `run(delay, period, duration)` forwards the call to every contained
  runnable.
- `close()` forwards to `close()` on every contained runnable that
  implements `AutoCloseable`, wrapping any checked exception in a
  `RuntimeException`.
- `_finalize()` forwards to `_finalize()` on every contained runnable
  that implements `Finalizable`.
- Its no-argument `run()` (required by `Runnable`) is an empty no-op —
  only the three-argument overload does anything, which is fine since
  that is the one steps call, but calling `run()` directly on a
  `CompositeRunnable` (e.g. via its `Runnable` supertype) silently does
  nothing.

## `AnimationParticle`

`AnimationParticle` (`data/AnimationParticle.java`) is a record describing
one particle spawn: `Particle` type, `count`, `extra` (Bukkit's
speed/data parameter), an `offset` `Vector` (spread on each axis,
defaulted to a zero vector if `null` is passed to the canonical
constructor), and an optional `@Nullable data` object (particle-specific
data such as `DustOptions`, per Bukkit's particle API). Several
convenience constructors fill in fewer fields.

`spawnParticle` has four overloads, thin wrappers over `World#spawnParticle`:

- to a `Location` (world is read from the location; no-ops if the
  location's world is `null`),
- to a `Location` with an explicit `List<Player>` of receivers and a
  `source` player,
- to a `World` + `Vector` position,
- to a `World` + `Vector` position with explicit receivers and a source.

All take a trailing `force` boolean (bypasses the client's particle-count
setting). `AnimationParticle` does not itself schedule anything — it is a
plain value object used by the `WorldParticle`, `ParticleArray`, and
`ParticleMap` runnables below.

## Built-in effects (`animation.runnable`)

All ten classes are Java records implementing `AnimationRunnable`. None
implement `AutoCloseable` or `Finalizable`. Each is a thin, single-purpose
wrapper around one Bukkit or Adventure call — construct with the target
and effect parameters, and use as a step's runnable.

| Class | Fields | Effect |
|---|---|---|
| `AnchorCharge` | `location`, `amount` | Adjusts a respawn anchor's charge by `amount` (may be negative), clamped to `[0, max charges]`. No-ops if the block at `location` is not a `RespawnAnchor`. `runnable/AnchorCharge.java` |
| `Firework` | `location`, `fireworkMeta` | Spawns a firework entity at `location` with the given `FireworkMeta`. No-ops if the world is `null`. `runnable/Firework.java` |
| `ForceField` | `location`, `radius`, `velocity` | Finds every player within `radius` of `location` and sets their velocity to the vector from `location` to the player, scaled by `velocity` — a knockback/repulsion pulse. `runnable/ForceField.java` |
| `LocalSound` | `location`, `sound`, `volume`, `pitch` | Plays a Bukkit `Sound` at a fixed world location via `World#playSound` (not tied to a specific listening player). A convenience constructor defaults `volume`/`pitch` to `1.0`. A second constructor accepts a `net.kyori.adventure.sound.Sound` and converts it by scanning `org.bukkit.Sound.values()` for a matching key — **this throws `NoSuchElementException` if no vanilla `Sound` constant matches the Adventure sound's key**, e.g. for a resource-pack-only sound. `runnable/LocalSound.java` |
| `MessageEffect` | `audience`, `message` | Sends an Adventure `Component` to an `Audience` via `sendMessage`. `runnable/MessageEffect.java` |
| `ParticleArray` | `world`, `points` (`Set<Vector>`), `particle` (`AnimationParticle`) | Spawns the same `AnimationParticle` at every point in a set of world-relative vectors. `runnable/ParticleArray.java` |
| `ParticleMap` | `world`, `particles` (`Map<Vector, AnimationParticle>`) | Like `ParticleArray`, but each point carries its own independent `AnimationParticle` (different type/settings per point). `runnable/ParticleMap.java` |
| `SoundEffect` | `audience`, `sound` (Adventure `Sound`) | Plays an Adventure `Sound` to an `Audience` via `playSound`. Contrast with `LocalSound`: this is audience/client-based rather than a fixed world location. `runnable/SoundEffect.java` |
| `TitleEffect` | `audience`, `title` | Shows an Adventure `Title` to an `Audience` via `showTitle`. `runnable/TitleEffect.java` |
| `WorldParticle` | `location`, `particle` (`AnimationParticle`) | Spawns one `AnimationParticle` at a single `Location`, delegating to `AnimationParticle#spawnParticle`. `runnable/WorldParticle.java` |

### Threading note

Every effect above calls regular Bukkit or Adventure API directly and is
written assuming it runs on the main server thread. World/block/entity
mutation calls (`AnchorCharge`, `Firework`, `ForceField`, `WorldParticle`,
`ParticleArray`, `ParticleMap`) are Bukkit APIs that are unsafe to call
off the main thread. None of these classes do their own thread-safety
checking, so pairing them with `AsyncBlockingAnimationStep` or
`AsyncParallelAnimationStep` (see [Threading](steps.md#threading) in
Steps and scheduling) is not safe. Treat all ten as main-thread-only
unless you have independently verified an exception for your server
platform.
