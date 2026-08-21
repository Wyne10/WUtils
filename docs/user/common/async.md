# Scheduling and Async Work

Two questions come up constantly in a Bukkit plugin: "I need to do this off the main
thread so I don't lag the server" and "I'm off-thread now, how do I get back on to touch
the Bukkit API safely." `me.wyne.wutils.common.scheduler` and the vendored promise stack
answer both.

## Getting a scheduler

`Schedulers.sync()` and `Schedulers.async()` return a `Scheduler` — an `Executor` with
extra methods for delayed and repeating work. Which one you call decides which thread
everything you schedule through it runs on:

```java
import me.wyne.wutils.common.scheduler.Schedulers;

// runs on the main server thread
Schedulers.sync().run(() -> player.sendMessage("done"));

// runs off the main thread
Schedulers.async().run(() -> doSomeSlowWork());
```

## Running something off the main thread and coming back

This is the pattern you'll reach for most. `supply`/`call`/`run` return a `Promise` you
can chain onto — compute off-thread, then hop back to sync to touch the Bukkit API:

```java
import me.wyne.wutils.common.scheduler.Schedulers;

Schedulers.async()
        .supply(() -> lookUpSomethingSlow(playerId))
        .thenAcceptSync(result -> player.sendMessage("Result: " + result));
```

`thenAcceptSync`/`thenApplySync` run their callback on the main thread regardless of
which scheduler started the chain; the `...Async` variants keep it off-thread. Use
`thenAccept`/`thenApply` with an explicit `ThreadContext` if you want to choose at
runtime instead of hardcoding sync vs. async.

The `Callable`/`Runnable` overloads (`call`, `run`) are convenience wrappers around the
same `Supplier`-based machinery — a `Runnable` still gets you back a `Promise<Void>` you
can chain on.

## Delaying and repeating

| You want | Call |
|---|---|
| run once, later | `supplyLater`/`callLater`/`runLater(..., delayTicks)` or `(..., delay, TimeUnit)` |
| run repeatedly | `runRepeating(Consumer<Task>, delayTicks, intervalTicks)` or the `TimeUnit` overload |

```java
import me.wyne.wutils.common.scheduler.Schedulers;

Schedulers.sync().runLater(() -> player.sendMessage("3 seconds later"), 60L);

Schedulers.sync().runRepeating(task -> {
    if (shouldStop()) {
        task.stop();
        return;
    }
    tick();
}, 0L, 20L);
```

`runRepeating` hands you the `Task` itself so the body can cancel itself — that's the
`Consumer<Task>` overload. If your repeating body never needs to self-cancel, pass a
plain `Runnable` instead and use whatever external mechanism to cancel it.

Delayed/repeating calls all take **ticks** (20 per second) unless you use the `TimeUnit`
overload. See [Ranges, Durations and Values](values.md#durations-and-ticks) if you're
parsing a delay out of config rather than hardcoding it — `Ticks.of(...)` and
`Durations.getTicks(...)` both convert into the tick counts these methods expect.

## The three task wrappers — when `runRepeating` isn't enough

If you need to know *when a task finished*, not just schedule it, three first-party
classes wrap Bukkit's scheduler directly (they're not part of the vendored stack):

| Class | Tells you |
|---|---|
| `PromisedTask` | fires one callback **on cancellation** — not on natural completion, see below |
| `ObservableTask` | notifies every subscriber on every run (repeating-friendly) |
| `EventPromisedTask` | completes when a chosen Bukkit event fires, or is cancelled |

**`PromisedTask`'s callback fires on cancel, not on finishing.** A one-shot task whose
body just returns normally never fires the promise — nothing cancelled it. If you want
"tell me when the body ran", use `ObservableTask` instead; if you specifically want
"tell me when this was cancelled" (including a restart, since launching again cancels
the previous run first), `PromisedTask` is right.

`EventPromisedTask` completes either when the event arrives (callback gets the event) or
when someone calls `cancel()`/`close()` (callback gets `null`). Always handle the `null`
case — it means "abandoned", not "satisfied", and there's no built-in timeout.

All three implement `Terminable`, so they play nicely with the `bind`/`bindModule`
pattern in [Plugin Setup](plugin.md).

## Which plugin a task belongs to

Every one of these calls — scheduler, promise, and the vendored event subscriptions in
[Events](events.md) — ultimately resolves a single static `Plugin` reference held by
`PluginUtils`. In the normal case (you shade WUtils into one plugin jar) this is
invisible: `PluginUtils` falls back to whichever plugin's classloader loaded the class,
and that's your plugin.

It stops being invisible in two situations:

- **You load `wutils-common` once and share it, unshaded, across several plugins.**
  Every consumer's tasks and listeners then register under whichever plugin happened to
  provide the class first. Disabling that plugin silently kills every other plugin's
  scheduled tasks and subscriptions too.
- **You call any scheduler/promise/event API before your plugin is fully constructed** —
  e.g. from a static initializer that runs too early. The lookup throws instead of
  failing somewhere more obvious.

If you're not shading, call `PluginUtils.setPlugin(this)` yourself, early, rather than
relying on the fallback. This affects `getLogger()` too — unhandled task/promise
exceptions log under whatever plugin `PluginUtils` currently resolves to.

## Going deeper on promises

`Promise` and the rest of the vendored concurrency stack (`ThreadContext`, `Terminable`,
`ServerThreadLock`) have a much larger API than shown here — chaining multiple stages,
combining promises, exception handling, blocking on completion. It's vendored from
lucko's `helper`; the full API is documented on the
[upstream helper wiki](https://github.com/lucko/helper/wiki). The entry points you'll
actually call from WUtils are `Schedulers.sync()`/`Schedulers.async()` shown above —
`Promise` itself you mostly just chain on, you rarely construct directly.

## See also

- [Events](events.md) — `EventRegistry`/`ListenerRegistry`, and how `EventPromisedTask`
  uses one internally.
- [Plugin Setup](plugin.md) — binding tasks and subscriptions to your plugin's lifecycle
  with `bind`/`bindModule`.
- [Ranges, Durations and Values](values.md#durations-and-ticks) — converting config
  strings into tick counts for delayed/repeating calls.
- [contributor wiki: Scheduler](../../dev/common/scheduler.md) and
  [Vendored helper Library](../../dev/common/helper.md) — internals and sharp edges in
  more depth.
