# Events

Bukkit gives you one way to register a listener: `PluginManager.registerEvents`, with
`public @EventHandler` methods, torn down by hand with `HandlerList.unregisterAll`.
WUtils adds two small registries on top for the common case of "a group of listeners
that should all disappear together when something closes" — plus, vendored from lucko's
`helper`, a fluent functional-subscription API for one-off event handling. This page
covers all three, and tells you which one to reach for.

## `ListenerRegistry` — grouping ordinary listeners

If all you want is normal Bukkit listeners (`public @EventHandler` methods) that get
unregistered together, `ListenerRegistry` is the thin option:

```java
import me.wyne.wutils.common.event.ListenerRegistry;

ListenerRegistry registry = new ListenerRegistry(plugin);
registry.register(new PlayerJoinListener());
registry.register(new PlayerQuitListener());

// later, e.g. in onDisable()
registry.close(); // unregisters everything it holds
```

Register each listener instance once — Bukkit doesn't deduplicate, so registering the
same instance twice makes its handlers fire twice per event, even though a single
`unregister`/`close()` call cleans it up correctly afterward.

## `EventRegistry` — when handlers don't need to be `public`

`EventRegistry` inserts itself as the single real Bukkit listener and dispatches to your
registered listeners itself, using reflection. That buys you two things ordinary Bukkit
registration doesn't: handler methods don't have to be `public`, and you can add/remove
listeners at runtime without touching Bukkit's `HandlerList`.

```java
import me.wyne.wutils.common.event.EventRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

EventRegistry registry = new EventRegistry(plugin);

registry.register(new Listener() {
    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("welcome");
    }
});
```

A handler that throws has its stack trace logged and dispatch continues to the next
listener — more forgiving than Bukkit's own behavior, but also easier to miss a
persistently-failing handler in your logs.

### Things to know before you rely on it

- **Inherited `@EventHandler` methods are ignored.** `register(Listener)` only sees
  methods declared directly on the listener's own class. If your listener hierarchy puts
  handlers on a base class, use the three-argument `register(Listener, Class, Method)`
  instead, or those handlers silently never fire.
- **An `@EventHandler` method with no parameters throws at registration**, not with a
  helpful message — give the handler its event parameter.
- **`unregister()` doesn't unregister from Bukkit.** It only stops routing to that
  listener internally; the underlying Bukkit hook for that event stays installed for the
  registry's lifetime. Only `close()` actually removes Bukkit-level hooks. A long-lived
  registry that registers and unregisters many different event types will accumulate
  hooks that dispatch to nobody — harmless, but worth knowing if you're auditing
  `HandlerList` contents.
- **Don't register/unregister a listener from inside a handler that's currently firing**
  — it can throw `ConcurrentModificationException`. Defer the change to the next tick
  (see [Scheduling and Async Work](async.md)) instead.

Both `ListenerRegistry` and `EventRegistry` implement `Terminable`, so the idiomatic way
to use either is to bind it to your plugin's terminable registry — see
[Plugin Setup](plugin.md#binding-things-to-close-automatically) — rather than calling
`close()` by hand.

## One-off subscriptions: the vendored `Events` API

Neither registry above replaces `Events`, the vendored fluent subscription builder — that
one builds a single filtered, self-expiring subscription for a single event, rather than
managing a set of conventional listeners. It's part of the lucko `helper` stack this
module vendors; the full API — filtering, merging multiple event types, expiring after N
calls — is documented on the
[upstream helper wiki](https://github.com/lucko/helper/wiki). Reach for `Events` when you
want a quick "listen for this event a few times and then stop" without writing a whole
`Listener` class; reach for `EventRegistry`/`ListenerRegistry` when you're managing a
standing set of conventional listeners.

## See also

- [Scheduling and Async Work](async.md) — `EventPromisedTask`, which uses an
  `EventRegistry` internally to wait for a single event.
- [Plugin Setup](plugin.md) — binding a registry so it closes automatically with your
  plugin.
- [contributor wiki: Events](../../dev/common/events.md) — internals and more sharp
  edges.
