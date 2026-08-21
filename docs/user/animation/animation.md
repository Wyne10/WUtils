# Animations

`wutils-animation` chains timed effects — particles, sounds, fireworks, titles — into a
sequence that can run one step after another, run several steps alongside each other, or
mix both. Reach for it whenever you're building more than a single one-shot effect: a
level-up sequence, a boss-death fireworks show, a countdown. For a single sound or
particle, just call the Bukkit/Adventure API directly — the animation machinery only
earns its keep once you're sequencing more than one thing.

`animation` requires **Paper**, not plain Spigot — several built-in effects take
Adventure `Audience`/`Sound`/`Title` types directly, which Paper implements natively on
`Player` but plain Spigot does not expose without a bridge library.

## Adding it to your build

```kotlin
dependencies {
    implementation("io.github.wyne10:wutils-animation:2.2.3")
}
```

Paper API is the only dependency you need to supply yourself (`compileOnly`); everything
else the module needs is bundled.

## A complete example

A level-up sequence: a title and sound play immediately, a trail of particles sparkles
around the player for the next three seconds, and once the title has been up for two
seconds a firework goes off.

```java
Animation animation = new Animation(plugin);

AnimationRunnable titleAndSound = new CompositeRunnable(List.of(
        new TitleEffect(player, Title.title(
                Component.text("Level Up!", NamedTextColor.GOLD),
                Component.text("You reached level 10", NamedTextColor.GRAY))),
        new SoundEffect(player, Sound.sound(
                Key.key("entity.player.levelup"), Sound.Source.MASTER, 1f, 1f))
));
animation.addStep(new BlockingAnimationStep(titleAndSound, 0, 0, 40)); // 40 ticks = 2s

AnimationParticle sparkle = new AnimationParticle(
        Particle.TOTEM, 10, 0.5, new Vector(0.3, 0.3, 0.3));
animation.addStep(new ParallelAnimationStep(
        new WorldParticle(player.getLocation(), sparkle), 0, 5, 60)); // every 5 ticks, for 3s

FireworkMeta meta = ((org.bukkit.inventory.meta.FireworkMeta)
        new ItemStack(Material.FIREWORK_ROCKET).getItemMeta());
meta.addEffect(FireworkEffect.builder().withColor(Color.YELLOW).with(FireworkEffect.Type.BALL_LARGE).build());
animation.addStep(new BlockingAnimationStep(new Firework(player.getLocation(), meta), 10));

animation.run();
```

Three steps, three different shapes:

- The title/sound step is **blocking** — the firework step doesn't start until it
  finishes.
- The particle step is **parallel** — it starts alongside the title/sound step instead of
  waiting for it, and keeps repeating in the background while later steps run.
- `CompositeRunnable` bundles the title and the sound into one runnable so a single step
  drives both together.

## The step model

An `Animation` is a queue of `AnimationStep`s, each pairing an `AnimationRunnable` (the
effect) with timing and a scheduling strategy. You don't loop over the queue yourself —
each step starts the next one automatically once it's done (or, for parallel steps, as
soon as it starts). Call `animation.run()` once to kick off the first step; it will not
loop back over anything, so build the whole sequence before calling `run()`.

`Animation` implements `AutoCloseable` — `close()` is the same as `stop()` — so
try-with-resources works if you want a scope-bound animation, though most animations are
launched fire-and-forget instead.

### Four concrete step types

Two independent choices — does the next step wait, and does the effect run on the main
thread — give you four classes:

| | Sync | Async |
|---|---|---|
| **Blocking** (next step waits) | `BlockingAnimationStep` | `AsyncBlockingAnimationStep` |
| **Parallel** (next step starts immediately) | `ParallelAnimationStep` | `AsyncParallelAnimationStep` |

All four share the same constructor shape: `(runnable, delay)`, `(runnable, delay,
period, duration)`, or just `(runnable)` for an immediate one-shot with no repeat. Leaving
`period` at `0` (or omitting it) makes the step run its runnable exactly once; setting
`period > 0` makes it repeat every `period` ticks until `duration` has elapsed.

**Async steps only take the effect itself off the main thread.** The built-in effects
below all call regular Bukkit API and are written assuming a main-thread caller — do not
pair them with an async step. Async steps exist for a runnable that does its own I/O or
computation before touching Bukkit API, not as a way to make Bukkit calls "faster".
Cleanup (`close()`) always runs back on the main thread regardless of which step type
scheduled the effect, so a runnable's cleanup can always use Bukkit API safely.

### The timing trap: everything is in ticks

`delay`, `period`, and `duration` are **ticks**, not milliseconds or seconds — 20 ticks
per second on a normally-running server. There is no separate seconds-based constructor.
Passing `2000` expecting "2 seconds" gives you 100 seconds instead; passing `2` expecting
"2 seconds" gives you a tenth of a second. Convert explicitly: multiply your intended
seconds by 20.

**A repeating blocking step needs a positive `duration`, or it never advances.** If you
set `period > 0` but leave `duration` at `0` (or negative), `BlockingAnimationStep`'s
repeat condition (`ticksElapsed >= duration`) never becomes true — the step repeats
forever and the rest of your animation never runs, until you call `animation.stop()`
externally. A one-shot step (`period == 0`) doesn't have this problem.

## Built-in runnables

Ten ready-made effects live in the `runnable` package, all Java records, all stateless —
none need `close()`/`_finalize()` cleanup:

| Class | Constructor fields | Effect |
|---|---|---|
| `WorldParticle` | `location`, `particle` (`AnimationParticle`) | Spawns one particle burst at a location |
| `ParticleArray` | `world`, `points` (`Set<Vector>`), `particle` | Spawns the same particle burst at every point in a set |
| `ParticleMap` | `world`, `particles` (`Map<Vector, AnimationParticle>`) | Like `ParticleArray`, but each point has its own independent particle settings |
| `LocalSound` | `location`, `sound`, `volume`, `pitch` | Plays a Bukkit `Sound` at a fixed world location, not tied to a listener |
| `SoundEffect` | `audience`, `sound` (Adventure `Sound`) | Plays a sound to an `Audience`, client-relative rather than world-fixed |
| `MessageEffect` | `audience`, `message` | Sends a chat `Component` |
| `TitleEffect` | `audience`, `title` | Shows a title/subtitle |
| `Firework` | `location`, `fireworkMeta` | Spawns a firework entity |
| `AnchorCharge` | `location`, `amount` | Adjusts a respawn anchor's charge, clamped to its valid range |
| `ForceField` | `location`, `radius`, `velocity` | Pushes every player within `radius` outward from `location` |

`AnimationParticle` is the reusable descriptor `WorldParticle`/`ParticleArray`/
`ParticleMap` build on: particle type, count, `extra` (Bukkit's speed/data parameter), an
`offset` spread vector, and optional particle-specific data (e.g. `DustOptions` for
colored dust). Several convenience constructors let you skip trailing fields.

All ten assume a main-thread caller — see the async warning above.

## Writing your own runnable

`AnimationRunnable` extends `Runnable`, so any `Runnable` lambda or method reference
already satisfies it:

```java
AnimationStep step = new BlockingAnimationStep(() -> player.sendMessage("Boo!"), 0);
```

Override the default `run(long delay, long period, long duration)` instead of plain
`run()` if your effect needs to know its own timing — steps always call the three-arg
overload, so that's the only way a runnable finds out its delay/period/duration without
you passing them into its constructor separately.

Implement `AutoCloseable` and/or `Finalizable` on your runnable if it holds a resource
that needs releasing:

- **`close()`** runs once your runnable actually started — on normal completion, or if
  `animation.stop()` cuts it short mid-run.
- **`_finalize()`** (a WUtils-specific interface, unrelated to Java's deprecated
  `Object.finalize()`) runs instead, for a runnable whose step was still waiting in the
  queue when `stop()` was called and so never got to run at all.

None of the ten built-in effects implement either — they're stateless, fire-and-forget
calls with nothing to release.

### The `CompositeRunnable` trap

`CompositeRunnable` bundles several runnables into one (as in the example above), and
correctly implements the three-arg `run(delay, period, duration)` that steps call — but
its plain no-arg `run()` (required to satisfy `Runnable`) is a hardcoded no-op. Steps
never call it, so this is invisible in normal use, but if you ever invoke a
`CompositeRunnable` directly through its `Runnable` supertype — passing it to
`Bukkit.getScheduler()` yourself, say — it will silently do nothing.

## `stop()` and in-flight steps

Calling `animation.stop()` (or `close()`, which just calls `stop()`) tears down whatever's
running immediately:

1. The current blocking step's task is cancelled, and its runnable's `close()` runs — its
   effect had already started, so it gets a chance to clean up.
2. Every currently-running parallel step is cancelled and closed the same way.
3. Every step still sitting in the queue, never started, gets `_finalize()` instead of
   `close()`.

You don't need to call `stop()` yourself for an animation that runs to completion — a
synthetic terminating step calls it automatically once every queued step has finished, so
the `Animation` object cleans itself up on its own.

## See also

- [the contributor wiki](../../dev/animation/steps.md) — the full step-advancement
  mechanism, exact `close()`/`_finalize()` call sites, and threading details.
- [the contributor wiki's Runnables page](../../dev/animation/runnables.md) — every
  built-in effect's exact no-op/edge-case behavior.
