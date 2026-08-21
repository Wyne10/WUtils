# Scheduler

Ten of the fourteen files in `me.wyne.wutils.common.scheduler` are vendored from lucko's
helper — `Schedulers`, `Task`, `HelperExecutors`, `HelperAsyncExecutor`, the
`scheduler/builder/` package and `scheduler/threadlock/`. See
[Vendored helper Library](helper.md) and the
[upstream wiki](https://github.com/lucko/helper/wiki).

This page covers the four first-party classes: the `Scheduler` interface itself, and
three `BukkitTask` wrappers that add a completion callback.

## `Scheduler` — the interface helper's `Schedulers` hands back

`Scheduler` (`Scheduler.java:25`) extends `java.util.concurrent.Executor` and adds a
`ThreadContext` (`Scheduler.java:32`) saying whether it runs work on the main thread or
off it. You do not implement it; you obtain `Schedulers.sync()` or `Schedulers.async()`
and call these methods on the result.

The API is one small grid. Three ways to express the work, crossed with immediate or
delayed, all returning a `Promise`:

| | Immediate | Delayed (ticks or `TimeUnit`) |
|---|---|---|
| `Supplier<T>` | `supply` | `supplyLater` |
| `Callable<T>` | `call` | `callLater` |
| `Runnable` | `run` (gives `Promise<Void>`) | `runLater` |

The `Callable` and `Runnable` forms are thin adapters — they convert to a `Supplier` via
`Delegates` and hand off to the same `Promise` factory, which is why a `Runnable` still
produces a promise you can chain. Every one of them null-checks its argument eagerly, so
passing null fails at the call rather than inside the task later.

Repeating work is different: `runRepeating` returns a helper `Task` rather than a
`Promise`, since a repeating task has no single completion. The two abstract overloads
take a `Consumer<Task>` — the task passes itself in so the body can cancel it
(`Scheduler.java:156`, `Scheduler.java:168`) — and two defaulted overloads accept a plain
`Runnable` for bodies that never need to self-cancel (`Scheduler.java:178`,
`Scheduler.java:192`).

Which thread work runs on is entirely determined by which `Scheduler` you got it from.
Everything else on this page is explicit about it instead.

## Three task wrappers

These are not part of helper's model. They wrap Bukkit's scheduler directly and exist to
answer one question helper's `Task` does not: *tell me when this finished.* All three
implement `Terminable`.

| Class | Notifies | Shape of the callback |
|---|---|---|
| `PromisedTask` (`PromisedTask.java:23`) | one callback | `Runnable`, fired on cancel |
| `ObservableTask` (`ObservableTask.java:24`) | many subscribers | `Runnable` list, fired on every run |
| `EventPromisedTask` (`EventPromisedTask.java:35`) | one callback | `Consumer<T>`, fired when a Bukkit event arrives |

All three expose the same six launch methods — `runTask`, `runTaskAsynchronously`,
`runTaskLater`, `runTaskLaterAsynchronously`, `runTaskTimer`,
`runTaskTimerAsynchronously` — mirroring `BukkitScheduler`. The `Asynchronously` variants
run off the main thread; the others run on it. `getTask()` returns the underlying
`BukkitTask`, or null before anything has been launched.

### `PromisedTask`

Holds a body and a promise. The single-argument constructor
(`PromisedTask.java:41-43`) uses the body as its own promise, which is occasionally what
you want and otherwise surprising — read the constructor before assuming.

Each launch method calls `cancel()` first (`PromisedTask.java:49-52`), so a
`PromisedTask` is restartable: launching again cancels the previous run, which fires the
promise, then starts fresh.

`cancel()` (`PromisedTask.java:108`) is where the promise runs. It cancels under the
lock and then runs the promise **outside** it, so a promise that blocks or schedules more
work cannot deadlock against another thread launching the task. Note the consequence:
**the promise fires on cancellation, not on natural completion.** A one-shot
`runTask` whose body simply returns never fires the promise — nothing cancels it. Use
`ObservableTask` if you want notification when the body finishes.

### `ObservableTask`

Is itself the `Runnable` handed to Bukkit; `run()` invokes every subscriber
(`ObservableTask.java:105-107`). Subscribers live in a `CopyOnWriteArrayList`
(`ObservableTask.java:27`), so subscribing or unsubscribing during a run is safe and the
running iteration sees the old list.

`subscribe` (`ObservableTask.java:38`) adds; `clear` (`ObservableTask.java:45`) removes
all. Subscribers run on whichever thread the task was launched on — with
`runTaskTimerAsynchronously`, that is an async thread, so subscribers must not touch the
Bukkit API without hopping back.

Unlike `PromisedTask`, `cancel()` (`ObservableTask.java:112`) does **not** notify. Here
notification means "the body ran", so a repeating task notifies on every tick of it.

### `EventPromisedTask`

Runs a body and completes when a chosen Bukkit event fires. It builds a private
`EventRegistry` and registers its own `onEvent` method for the event class
(`EventPromisedTask.java:48-62`) — see [Events](events.md).

There are two completion paths and they pass different values:

- The event arrives → the private `cancel(Event)` closes the registry, cancels the task
  and calls `promise.accept(event)` (`EventPromisedTask.java:124-134`).
- Someone calls `cancel()` or `close()` → same teardown, but `promise.accept(null)`
  (`EventPromisedTask.java:139-147`).

That is why the promise is a `Consumer<@Nullable T>`: **a null argument means the wait
was abandoned rather than satisfied.** Handle it.

Unlike the other two, the launch methods are guarded by `if (task != null) return`
(`EventPromisedTask.java:72-77`), so an `EventPromisedTask` launches once and later
launch calls are silently ignored.

**The two families behave differently on completion, and both have a failure mode.** The
non-repeating overloads wrap the body so that when it finishes naturally it cancels its
own `BukkitTask` and closes the registry directly (`EventPromisedTask.java:72-107`) —
bypassing `cancel()`, so **the promise is never invoked** if the body completes before
the event fires. The repeating overloads pass the raw body with no such wrapper
(`EventPromisedTask.java:109-122`), so task and listener stay alive indefinitely until
the event fires or someone cancels. Neither has a timeout.

There is also a visibility hazard: the wrapping lambda captures the `task` field before
the assignment that sets it completes, and `task` is not `volatile`. For the
asynchronous variants there is no happens-before edge between the scheduling thread and
the callback thread, so a very fast async body can in principle observe `task` as null
and throw.

## See also

- [Vendored helper Library](helper.md) — `Schedulers`, `Task`, `Promise`,
  `ThreadContext`, and the plugin-resolution caveat that affects everything here.
- [Events](events.md) — `EventRegistry`, which `EventPromisedTask` uses.
- [Plugin Composition](plugin.md) — binding these terminables to the plugin lifecycle.
- [Durations and Cooldowns](durations.md) — tick conversion for delay and period
  arguments.
