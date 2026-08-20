# Steps and scheduling

The step model is the core of the `animation` module. This page covers the
`Animation`/`AnimationStep` types, how a chain of steps advances itself,
what units the timing fields use, the four concrete step classes, and the
`close()`/`_finalize()` cleanup contract.

See also: [Animation module overview](animation.md), [Runnables](runnables.md).

## Units

`delay`, `period` and `duration` on every step are `long` values in
**ticks** (20 ticks = 1 second on a normally-running server) — they are
passed straight through to the Bukkit scheduler in each concrete step's
`runOnce`/`runRepeating` (e.g. `BlockingAnimationStep.java:27` and `:42`).
There is no separate seconds- or millisecond-based API; get a tick value
wrong and the animation runs 20x too fast or too slow.

## `AnimationStep` and `BaseAnimationStep`

`AnimationStep` (`AnimationStep.java`) is a functional interface with a
single abstract method, `run(Animation animation)`, plus `AutoCloseable`
and `Finalizable` with no-op default implementations. A step can in
principle be a bare lambda if it needs no cleanup.

`BaseAnimationStep` (`BaseAnimationStep.java`) is the abstract base every
built-in step extends. It holds the `AnimationRunnable`, `delay`,
`period`, `duration`, and a `ticksElapsed` counter used by repeating
steps. `run(animation)` resets `ticksElapsed` to 0 and calls
`createTask`, which picks `runOnce` when `period == 0` and `runRepeating`
otherwise — that single field is what distinguishes a one-shot step from
a repeating one.

`BaseAnimationStep` also implements the shared halves of the cleanup
contract (see below): `close()` closes the wrapped `AnimationRunnable` if
it implements `AutoCloseable`; `_finalize()` calls `_finalize()` on it if
it implements `Finalizable`. Concrete step classes never override these —
only `runOnce`/`runRepeating`.

## The four concrete steps

Two independent axes produce four classes:

|  | Sync | Async |
|---|---|---|
| **Blocking** | `BlockingAnimationStep` | `AsyncBlockingAnimationStep` |
| **Parallel** | `ParallelAnimationStep` | `AsyncParallelAnimationStep` |

- **Blocking** — the next step does not start until this one finishes
  (its full `delay` + `duration`, or its repeat cycle, has elapsed).
- **Parallel** — the next step starts immediately, while this one
  continues running in the background.
- **Sync** — scheduled with `Bukkit.getScheduler().runTaskLater`/
  `runTaskTimer`: runs on the main server thread.
- **Async** — scheduled with `runTaskLaterAsynchronously`/
  `runTaskTimerAsynchronously`: the `AnimationRunnable`'s code runs off
  the main thread.

### `BlockingAnimationStep` (`BlockingAnimationStep.java`)

- `runOnce` (period `0`): after `delay` ticks, runs the runnable once,
  then after a further `duration` ticks, calls `close()` and advances to
  the next step. Total time before the next step starts is
  `delay + duration`.
- `runRepeating` (period `> 0`): runs the runnable every `period` ticks.
  When `ticksElapsed >= duration` (and `duration > 0`), it closes,
  cancels the repeating task, and advances. **If `duration` is `0` or
  negative, this condition never becomes true — the step repeats forever
  and never advances the chain on its own.** It must be stopped
  externally (e.g. `Animation.stop()`).

### `AsyncBlockingAnimationStep` (`AsyncBlockingAnimationStep.java`)

Same blocking semantics as above, but scheduled asynchronously. This is
where the module lets user code run off the main thread — see
[Threading](#threading) below.

Only the `AnimationRunnable` itself runs async. In both modes, `close()`
and the advance to the next step are handed back to the main thread: in
`runOnce` via the nested `runTaskLater` that fires after `duration`, and
in `runRepeating` via a `runTask` scheduled once the duration is reached
(the repeating task is cancelled first, so no further async ticks fire
while that is pending). A runnable's `close()` may therefore safely use
Bukkit API regardless of which mode the step is in.

### `ParallelAnimationStep` (`ParallelAnimationStep.java`)

Runs on the main thread. Unlike the blocking steps, it calls
`startNext(animation)` immediately after submitting its task — right
after registering it in `Animation`'s `parallelTasks` map — regardless of
whether it is a one-shot or repeating step. The next step in the chain
therefore starts on the same tick this one is scheduled, not when it
finishes. Because more than one parallel step can be in flight at once,
these steps are tracked in `Animation.getParallelTasks()` (a map keyed by
step), not the single-slot `currentTask` field blocking steps use.

### `AsyncParallelAnimationStep` (`AsyncParallelAnimationStep.java`)

Same immediate-advance parallel semantics, scheduled asynchronously.
As with `AsyncBlockingAnimationStep`, only the `AnimationRunnable` runs
off the main thread — `close()` is always scheduled back onto the main
thread in both modes. There is no advance-to-next-step to hand back,
since a parallel step starts the next one when it is submitted.

## How the chain advances

`Animation` does **not** iterate over its steps. Each `BaseAnimationStep`
subclass calls `startNext(animation)` itself once its own condition for
"done" (or, for parallel steps, "started") is met.
`startNext` (`BaseAnimationStep.java:80-84`) polls the next step off the
animation's internal queue and calls `run` on it — which is how the
recursion continues. `Animation.run()` (`Animation.java:45-51`) only
seeds this process: it copies the accumulated `steps` queue into a
working `runSteps` queue, appends a synthetic terminating
`BlockingAnimationStep` that wraps `this::stop`, then polls and runs the
very first step. Every subsequent step is started by the previous step's
own completion logic, not by `Animation`. The terminating step is what
calls `stop()` once the whole real chain has run through, so `Animation`
cleans itself up without external code having to notice the animation
finished. Because the original `steps` queue is never cleared (only
`runSteps`, the working copy, is consumed), the added steps remain on the
`Animation` object after a run completes.

## `close()` vs. `_finalize()`

Two distinct cleanup paths exist, and they apply to different situations:

- **`close()`** is for a step that **started running** — whether it ran
  to completion normally, or was cut short by `Animation.stop()`. It is
  called on natural completion (inside each step's `runOnce`/
  `runRepeating`, once its work is done) and, separately, by
  `Animation.stop()` on whichever step(s) are currently running when
  `stop()` is invoked — the currently-blocking step and every in-flight
  parallel step. The comment at `Animation.java:60` is explicit about
  why: "Close blocking task, since it may not have been finished."
  `close()` releases whatever the runnable acquired *while running*.
- **`_finalize()`** is for a step that **never got a chance to run at
  all**. `Animation.stop()` calls `_finalize()` (not `close()`) on every
  step still sitting unstarted in the queue (`Animation.java:67-68`,
  with the comment "Run steps are not closed since they were not
  started"). It lets a runnable release resources it prepared ahead of
  time (typically in its own constructor) even though `run()`/`close()`
  never happened for it.

In short: a step that ran gets `close()`; a step that was only ever
queued gets `_finalize()`. `Finalizable` (`Finalizable.java`) is an
unrelated, WUtils-specific interface — despite the name, it has nothing
to do with Java's deprecated `Object.finalize()` or garbage collection.

`BaseAnimationStep` forwards both calls to the wrapped
`AnimationRunnable` if it implements the matching interface
(`AutoCloseable` for `close()`, `Finalizable` for `_finalize()`); none of
the built-in `animation.runnable` effects implement either, since they
are stateless one-shot calls with nothing to release. `CompositeRunnable`
implements both and forwards to its children — see
[Runnables](runnables.md).

## `Animation.stop()`

`Animation.stop()` (`Animation.java:57-69`), also reachable via `close()`
(the class implements `AutoCloseable`):

1. Cancels the current blocking `BukkitTask`, if any, and `close()`s its
   step.
2. Cancels and `close()`s every step still tracked in `parallelTasks`,
   then clears the map.
3. `_finalize()`s every step still waiting in `runSteps`, then clears the
   queue.

This is the only place `_finalize()` is ever called in the module, and it
only fires for steps that never ran.

## Threading

- **Sync steps** (`BlockingAnimationStep`, `ParallelAnimationStep`) run
  the `AnimationRunnable` on the main server thread. Any Bukkit API is
  safe to call from inside them.
- **Async steps** (`AsyncBlockingAnimationStep`,
  `AsyncParallelAnimationStep`) run the `AnimationRunnable` off the main
  thread. Most Bukkit API (world/block/entity/inventory mutation) is
  **not** thread-safe and must not be called from an async runnable.
  Async steps exist for runnables that do off-thread work (I/O,
  computation) before or between effects — not as a way to make Bukkit
  calls faster. The built-in `animation.runnable` effects (particles,
  entity spawning, block data mutation, velocity changes) all call
  regular Bukkit API and are written assuming a main-thread caller; using
  them inside an async step is not safe. See the threading note in
  [Runnables](runnables.md).
- **On normal completion, `close()` always runs on the main server
  thread** — for every step type and both modes. Async steps deliberately
  schedule it back rather than calling it from the async callback, so
  runnable cleanup can use Bukkit API freely.
- Cleanup driven by `Animation.stop()` is the exception: it calls
  `close()` and `_finalize()` directly, so both run on whichever thread
  invoked `stop()`. When `stop()` comes from the chain's own terminating
  step that is the main thread, but plugin code calling `stop()`/`close()`
  from an async context gets async cleanup.
