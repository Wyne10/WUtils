# Events

Most of `me.wyne.wutils.common.event` is vendored from lucko's helper — `Events`,
`Subscription` and everything under `event/functional/` and `event/filter/`. See
[Vendored helper Library](helper.md) for that, and the
[upstream wiki](https://github.com/lucko/helper/wiki) for its API.

This page covers the four first-party classes WUtils adds alongside it:
`EventRegistry`, `ListenerRegistry`, `RegisterableEvent` and `RegisterableListener`.

## Two registries, two different jobs

| Class | Registers with Bukkit | Reflection | Use when |
|---|---|---|---|
| `ListenerRegistry` (`ListenerRegistry.java:22`) | one `registerEvents` call per listener | none | you just want normal Bukkit listeners that all get unregistered together |
| `EventRegistry` (`EventRegistry.java:34`) | one `registerEvent` per distinct event/priority/ignoreCancelled combination, with itself as the listener | yes — it invokes handler methods directly | you need non-public handler methods, or listeners that come and go at runtime |

Both implement `Terminable`, so both can be bound to a
[`CompositeTerminable`](helper.md) and closed with the plugin. That is the intended way
to use them — see [Plugin Composition](plugin.md).

Neither is a replacement for helper's `Events`. `Events` builds a single fluent,
filtered, self-expiring subscription; these two manage *sets* of conventional
`@EventHandler` listeners.

## `ListenerRegistry` — the thin one

Three methods. `register` calls `Bukkit.getPluginManager().registerEvents` and remembers
the listener (`ListenerRegistry.java:37-40`); `unregister` calls
`HandlerList.unregisterAll(listener)` and forgets it (`ListenerRegistry.java:45-48`);
`close` unregisters everything it holds and clears the set
(`ListenerRegistry.java:54-57`).

The set is a `HashSet`, so registering the same listener instance twice stores it once —
but **Bukkit is called both times**, and Bukkit does not deduplicate. The listener's
handler methods will then fire twice per event, while a single `unregister` removes both
registrations. Register each listener once.

## `EventRegistry` — the reflective one

`EventRegistry` inserts itself as the only real Bukkit listener and dispatches to
registered listeners itself. That indirection buys two things: handler methods do not
have to be `public`, and listeners can be added and removed without touching Bukkit's
`HandlerList` each time.

**Registering.** `register(Listener)` (`EventRegistry.java:51-59`) scans the listener
for `@EventHandler` methods and derives each event type from the method's first
parameter. `register(Listener, Class, Method)` (`EventRegistry.java:67-85`) is the
explicit form. Both call `setAccessible(true)` on the method, which is what allows
private handlers.

**The dispatch key.** `RegisterableEvent` (`RegisterableEvent.java:16`) is a record of
the event class plus the `@EventHandler` annotation, and its `equals`
(`RegisterableEvent.java:19-29`) treats two entries as the same when the event class,
`priority` and `ignoreCancelled` all match. So one Bukkit registration is created per
*distinct combination* of those three (`EventRegistry.java:120-145`), no matter how many
listeners share it.

**Exceptions are swallowed per handler.** A handler that throws has its stack trace
logged through the plugin logger and dispatch continues to the next listener
(`EventRegistry.java:136-138`). That is more forgiving than Bukkit's behaviour; it also
means a persistently failing handler is easy to miss.

### Sharp edges

**`register(Listener)` only sees declared methods.** It uses `getDeclaredMethods`
(`EventRegistry.java:52`), so `@EventHandler` methods **inherited from a superclass are
silently ignored**. A listener hierarchy with handlers on a base class will half-work.
Use the three-argument `register` for those.

**A no-argument `@EventHandler` method throws.** The event type comes from
`method.getParameterTypes()[0]` (`EventRegistry.java:55`) with no arity check, so an
annotated method with no parameters fails with `ArrayIndexOutOfBoundsException` during
registration rather than a clear error.

**`unregister` does not unregister from Bukkit.** It only drops the listener from the
internal maps (`EventRegistry.java:107-110`); the `registerEvent` hook installed for that
event/priority/ignoreCancelled combination stays live for the rest of the registry's
life. Only `close()` (`EventRegistry.java:91-101`) actually removes them, by reflectively
calling each event class's static `getHandlerList()` and unregistering itself. A
long-lived registry that cycles through many distinct combinations therefore accumulates
Bukkit hooks that dispatch to nobody.

**Dispatch iterates live collections.** The handler loop walks the registry's sets
directly (`EventRegistry.java:126-141`), so a listener that registers or unregisters
another listener *while an event is being handled* can trigger
`ConcurrentModificationException`. Defer such changes to the next tick.

## The two interfaces

`RegisterableListener` (`RegisterableListener.java:13`) is an empty marker extending
Bukkit's `Listener`. It exists so a type can advertise that it is meant to be handed to
these registries; `EventPromisedTask` implements it — see [Scheduler](scheduler.md).

`RegisterableEvent` is described above. It is a key type, not something you normally
construct.

## See also

- [Vendored helper Library](helper.md) — `Events`, `Subscription`, and the functional
  subscription builders.
- [Scheduler](scheduler.md) — `EventPromisedTask`, which uses an `EventRegistry`
  internally to wait for an event.
- [Plugin Composition](plugin.md) — binding a registry so it closes with the plugin.
- [WUtils Common](common.md) — module overview.
