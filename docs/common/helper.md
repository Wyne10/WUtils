# Vendored helper Library

Forty-seven of `common/`'s 153 source files are not WUtils code. They are vendored
from **lucko's [`helper`](https://github.com/lucko/helper)** library (MIT), and they
supply the concurrency, event and lifecycle primitives that the rest of WUtils —
and `structure/`, `configurables/`, and consumer plugins — are built on.

This page exists so you can tell first-party code from vendored code, and knows where
the real documentation lives. **It deliberately does not re-explain the helper API.**
For that, read the [upstream helper wiki](https://github.com/lucko/helper/wiki).

## How to identify a vendored file

Every one carries this header as its first line:

> `This file is part of helper, licensed under the MIT License.`

Grep for it before editing anything in `common/`:
`grep -l 'part of helper' <file>`. The header is the authoritative marker — package
location is not, because several packages are a mix of vendored and first-party files.

## What is vendored, by package

| Package | Vendored | Also contains first-party code |
|---|---|---|
| `promise/` | all 3 | — |
| `terminable/`, `terminable/composite/`, `terminable/module/` | all 7 | — |
| `exception/`, `exception/types/` | all 5 | — |
| `event/`, `event/filter/`, `event/functional/**` | 20 of 24 | yes — see [Events](events.md) |
| `scheduler/`, `scheduler/builder/`, `scheduler/threadlock/` | 10 of 14 | yes — see [Scheduler](scheduler.md) |
| `interfaces/Delegate.java`, `Delegates.java` | both | — |

The two mixed packages are the ones to be careful in. In `event/`, only
`EventRegistry`, `ListenerRegistry`, `RegisterableEvent` and `RegisterableListener`
are WUtils's own; in `scheduler/`, only `Scheduler`, `ObservableTask`, `PromisedTask`
and `EventPromisedTask`.

## The four subsystems

| Subsystem | Entry point | What it gives you |
|---|---|---|
| Promises | `Promise` (`common/src/main/java/me/wyne/wutils/common/promise/Promise.java`, 1485 lines) | A `CompletableFuture` analogue that is aware of the Bukkit main thread, so a chain can hop between sync and async contexts explicitly. |
| Thread context | `ThreadContext` (`promise/ThreadContext.java:35-58`) | The `SYNC`/`ASYNC` enum every scheduler and promise call is parameterised by, plus `forCurrentThread()`. |
| Scheduling | `Schedulers` (`scheduler/Schedulers.java:60-103`) | `sync()`, `async()`, and a fluent `builder()`. Returns helper `Task` handles rather than Bukkit task ids. |
| Events | `Events` (`event/Events.java:53-120`) | Functional event subscription — `subscribe(Class)` and `merge(...)` returning builders that filter, expire and hand back a `Subscription`. |
| Lifecycle | `Terminable` (`terminable/Terminable.java:35-84`), `CompositeTerminable` (`terminable/composite/CompositeTerminable.java:42`), `TerminableModule` (`terminable/module/TerminableModule.java:37`) | An `AutoCloseable` refinement plus containers that close registered children as a group. Subscriptions and tasks are terminables, which is how they get unregistered on plugin disable. |

`ServerThreadLock` (`scheduler/threadlock/ServerThreadLock.java:38`) is the other piece
worth knowing about: it is a `Terminable` that blocks the server main thread while an
async section runs, for the rare case where you need the world to hold still.

`Delegate` (`interfaces/Delegate.java:33`) and `Delegates`
(`common/src/main/java/me/wyne/wutils/common/Delegates.java`) are plumbing — they let a
wrapped `Runnable`/`Consumer` expose the object it wraps, so the scheduler can unwrap
a task back to the callback a caller originally passed.

## Divergence from upstream — read this before assuming parity

The vendored files are **not** byte-identical to upstream helper. Two changes were made
when they were imported, and both matter:

**1. Repackaged.** Everything moved from `me.lucko.helper.*` to
`me.wyne.wutils.common.*`. No file references the original package. Upstream code
samples and Stack Overflow answers will not compile against these classes without
adjusting imports, and a project that also depends on real helper will end up with two
unrelated `Promise` types on the classpath.

**2. The plugin lookup was rewired.** Upstream helper resolves its hosting plugin
through its own internal loader. This copy substitutes WUtils's
`PluginUtils` (`me.wyne.wutils.common.plugin.PluginUtils`) at every such point:

| Site | Use |
|---|---|
| `Schedulers.java:125`, `Schedulers.java:154` | plugin passed to `runTaskTimer` / `runTaskTimerAsynchronously` |
| `HelperExecutors.java:62`, `HelperExecutors.java:69` | plugin for delayed and async dispatch |
| `HelperPromise.java:187`, `HelperPromise.java:195`, `HelperPromise.java:203` | plugin for every delayed continuation |
| `SingleHandlerListImpl.java:63` | plugin the listener registers under |
| `MergedHandlerListImpl.java:62` | same, for merged subscriptions |
| `ServerThreadLockImpl.java:50` | plugin for the sync signal task |
| `HelperExceptions.java:42` | logger every unhandled task/promise/event exception is reported to |

Two files also import WUtils's own root `Ticks` class for tick conversion
(`scheduler/Schedulers.java:28`, `promise/HelperPromise.java:32`).

### Why the plugin lookup is the thing to watch

`PluginUtils` holds a single static `Plugin` reference. Nothing sets it eagerly — on
first use it falls back to `JavaPlugin.getProvidingPlugin(PluginUtils.class)`, i.e. the
plugin whose classloader loaded WUtils (`common/src/main/java/me/wyne/wutils/common/plugin/PluginUtils.java:33-36`). There is also a
`setPlugin` setter (`common/src/main/java/me/wyne/wutils/common/plugin/PluginUtils.java:85`).

The consequences are worth stating plainly, because every scheduled task, every event
subscription and every promise continuation in the vendored stack passes through it:

- **Shaded into one plugin** — the fallback resolves to that plugin. Everything works
  and you never think about it. This is the intended deployment.
- **Loaded once and shared by several plugins** — the fallback resolves to whichever
  plugin provided the class, and *every* consumer's tasks and listeners register under
  that one plugin. Disabling it silently kills the others' subscriptions; disabling a
  different consumer leaves its listeners registered. Call `setPlugin` deliberately, or
  shade instead.
- **Called before the plugin is constructed** — `getProvidingPlugin` throws if no plugin
  owns the classloader, so touching any scheduler or promise API from a static
  initialiser that runs too early fails there rather than at an obvious place.

`getLogger()` inherits the same resolution, which is why unhandled exceptions from
helper's schedulers surface under that plugin's logger and not the one that scheduled
the work.

## Rules for changing this code

- **Do not edit vendored files.** Keeping the diff against upstream as small as it
  currently is — a repackage plus one substituted plugin accessor — is what makes it
  possible to pull fixes from upstream later. If you need different behaviour, add a
  first-party class alongside, as `Scheduler` and `EventRegistry` already do.
- Vendored files annotate nullability with `javax.annotation`. First-party files use
  `org.jetbrains.annotations`. Do not follow the neighbours' import when adding a file
  to a mixed package — see the [module overview](common.md) for the contract.

## See also

- [Events](events.md) — `EventRegistry` and `ListenerRegistry`, the WUtils registration
  layer built over `Events` and `Subscription`.
- [Scheduler](scheduler.md) — `Scheduler`, `PromisedTask` and `ObservableTask`, the
  WUtils additions over `Schedulers` and `Task`.
- [Plugin Composition](plugin.md) — `PluginUtils` itself, and the bootstrap that is the
  right place to call `setPlugin`.
- [Upstream helper wiki](https://github.com/lucko/helper/wiki) — the actual API
  documentation for everything on this page.
