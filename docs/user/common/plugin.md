# Plugin Setup

Before writing your main class, decide how much of `me.wyne.wutils.common.plugin` you
want. There are three levels:

| Option | What it buys you |
|---|---|
| A plain `JavaPlugin` | nothing from this package — use `PluginUtils`/`ConfigUtils`/etc. directly, no bootstrap changes |
| Extend `CompositeJavaPlugin<T>` | declarative, ordered `LOAD`/`ENABLE`/`DISABLE`/`RELOAD` steps, a built-in terminable registry, `PluginReloadEvent` |
| Wrap an existing plugin with `CompositePlugin<T>` | the same step pipeline as a `Plugin` decorator instead of a base class — see the caveat below before choosing this |

If you don't need ordered startup phases or automatic teardown of subscriptions/tasks,
you can skip this whole package and use a plain `JavaPlugin`. The rest of `common/` (and
`configurables`, `structure`) doesn't require you to use `CompositeJavaPlugin` — it's an
optional convenience.

## Extending `CompositeJavaPlugin`

```java
import me.wyne.wutils.common.plugin.CompositeJavaPlugin;
import me.wyne.wutils.common.plugin.Step;
import me.wyne.wutils.common.plugin.StepScope;

public class MyPlugin extends CompositeJavaPlugin<MyPlugin> {

    @Step(scope = StepScope.LOAD, priority = 0)
    private void loadConfig() {
        saveDefaultConfig();
    }

    @Step(scope = StepScope.ENABLE, priority = 0)
    private void registerListeners() {
        bind(new MyEventRegistry()); // see below
    }

    @Step(scope = StepScope.DISABLE)
    private void saveState() {
        // resources bound via bind()/bindModule() are still open here
    }
}
```

Annotate any no-argument method with `@Step(scope = ..., priority = ...)` and
`CompositeJavaPlugin` finds it automatically and runs it at the right phase — no manual
registration needed. `LOAD`/`ENABLE`/`DISABLE` fire exactly when Bukkit calls
`onLoad()`/`onEnable()`/`onDisable()`. Steps sharing a scope run in ascending `priority`
order, ties broken by declaration/registration order.

**`RELOAD` is never fired by Bukkit itself.** It only runs when you call the plugin's own
`reload()` method — typically from a `/reload` command you write. If you don't call
`reload()` somewhere, `RELOAD`-scoped steps never run.

### The lambda trap

`PluginStep` is a functional interface, so it's tempting to register one as a lambda.
Don't, for anything you need a specific scope/priority on — a lambda is backed by a
synthetic class that can never carry the `@Step` annotation, so it silently defaults to
`ENABLE` priority `0` no matter what you intended. Use an `@Step`-annotated method (as
above), a named class, or wrap the lambda in `ModifiedStep` if you need to override the
scope/priority explicitly.

### Ordering guarantee inside `onDisable()`

`onDisable()` runs every `DISABLE` step **before** closing the terminable registry. That
means a `DISABLE` step can still safely use anything you `bind`/`bindModule`'d earlier —
those bindings aren't torn down until after your disable steps finish.

## Binding things to close automatically

`bind`/`bindModule` register an `AutoCloseable`/`TerminableModule` with the plugin's
built-in terminable registry, so it closes automatically when the plugin disables:

```java
@Step(scope = StepScope.ENABLE)
private void setUp() {
    bind(new me.wyne.wutils.common.event.EventRegistry(this));
}
```

**Only call `bind`/`bindModule` after `onLoad()` has run** — the registry doesn't exist
before that, and calling either earlier throws a `NullPointerException`. In practice this
means: don't call them from your constructor or from field initializers, only from an
`@Step` method or later.

## `CompositePlugin`: wrapping instead of extending

`CompositePlugin<T>` gives you the same step pipeline as a `Plugin` decorator around an
existing plugin instance, for cases where you can't make your main class extend
`CompositeJavaPlugin` directly. Be aware: because it implements `Plugin` rather than
extending `JavaPlugin`, **Bukkit's loader cannot instantiate it as your main class.**
Nothing in this repository currently uses it end-to-end, so treat the wrapping pattern
as unproven — if you reach for it, you're responsible for calling its
`onLoad()`/`onEnable()`/`onDisable()` yourself at the right points. For most plugins,
extending `CompositeJavaPlugin` is the supported path.

## Loading config in a controlled order

`me.wyne.wutils.common.loadable` is a separate, smaller piece: a registry for "things
that read themselves out of a config section when the plugin loads," in a controlled
order. It's independent of the step system above and of the
[`config`](../config/config.md) module — `config` *generates and writes* YAML from
annotated fields, `Loadable` *pulls* values out of a section you already have. A class
can use both if it needs to.

Implement `Loadable`, register it and its config section with a `Loader`, then call
`load`:

```java
import me.wyne.wutils.common.loadable.Loadable;
import me.wyne.wutils.common.loadable.LoadableMeta;
import org.bukkit.configuration.ConfigurationSection;

@LoadableMeta(priority = 10)
public class ArenaLoader implements Loadable {
    @Override
    public void load(ConfigurationSection config) {
        // read arena definitions out of config
    }
}
```

```java
Loader.global.registerConfig("config.yml", getConfig());
Loader.global.registerLoadable(new ArenaLoader());
Loader.global.load(this);
```

`@LoadableMeta` is optional — every `Loadable` method has a sensible default (path
`"config.yml"`, priority `0`, not late, persists). `Loader.global` is a shared instance
you can use if you don't need an isolated one.

Two behaviors worth planning around:

- **A loadable registered for a path that has no matching `registerConfig` call is
  silently skipped** — no warning, no exception. If a `load()` implementation "never
  runs," check that the path matches something you actually registered.
- **`late()` reorders relative to execution timing, not to other loadables' priority.**
  A loadable marked `late()` (in `@LoadableMeta` or overridden) is deferred to the next
  server tick — every late loadable runs after every immediate one, regardless of
  priority number. Use `late()` when you need other plugins to have finished enabling
  first, not as a general ordering mechanism.
- **Non-persistent loadables (`persist() == false`) are dropped after one `load()` call
  whether or not they actually ran** — including one skipped because its config was
  never registered. Registering the config afterward and calling `load()` again won't
  pick it back up.

## Commands

`CommandUtils` gives you two [CommandAPI](https://commandapi.jorel.dev/) player
arguments with target-selector syntax (`@a`, `@s`, `@e`, ...) stripped out of tab
completion — useful because CommandAPI's own player arguments offer selectors alongside
real names, which is rarely what a "pick a player" command author wants.

```java
import me.wyne.wutils.common.command.CommandUtils;
import dev.jorel.commandapi.CommandAPICommand;

new CommandAPICommand("heal")
        .withArguments(CommandUtils.onlinePlayer("target"))
        .executes((sender, args) -> {
            Player target = (Player) args.get("target");
            target.setHealth(20);
        })
        .register();
```

`onlinePlayer` keeps CommandAPI's real entity-selector argument underneath — it still
resolves and validates server-side — and only replaces what tab completion suggests.
Someone who types a selector by hand will still have it parsed.

`offlinePlayer` is stricter: it's a plain string argument, not a selector at all, so
`@a` is just text that will fail to resolve. Nothing validates it for you automatically —
resolve it yourself with `CommandUtils.getOfflinePlayer(args, "target")`, which returns
`null` if the argument's missing, doesn't resolve to a known player, or names someone
who's never played on your server.

**Requires CommandAPI on your classpath** — it's `compileOnly` in `wutils-common`, so
calling into `CommandUtils` without CommandAPI shaded/available fails at class-load time,
not at compile time.

## `LoggerWrapper`

`LoggerWrapper` wraps an SLF4J logger and is *intended* to gate `trace`/`debug`/`info`/
`warn`/`error` calls behind a configurable `LevelWrapper` threshold (mirroring log4j's
eight levels). **Don't rely on it to suppress output — the gating doesn't work as
documented:**

- `trace`/`debug` do check the threshold before doing anything, but when they pass, they
  forward to the wrapped logger's `info(...)` — there's no way to actually emit at
  `TRACE`/`DEBUG` on the underlying logger through this class.
- `info`/`warn`/`error` are forwarded **unconditionally**, with no threshold check at
  all, despite the class computing the right `isXEnabled()` values internally.

Net effect: setting the threshold to `ERROR` suppresses nothing except `trace`/`debug`
calls (which get relabeled as `info` anyway). If you need real log-level filtering,
configure it on the underlying SLF4J/log4j backend directly rather than through
`LoggerWrapper`.

**Requires log4j-core on your classpath** for `LevelWrapper`'s type — `compileOnly` in
`wutils-common`.

## `PluginUtils`

`PluginUtils.getPlugin()`/`getLogger()` are how the vendored scheduler/promise/event
stack finds its owning plugin — see
[Scheduling and Async Work](async.md#which-plugin-a-task-belongs-to) for what that means
in practice and when you need `PluginUtils.setPlugin(this)` instead of the default
classloader-based lookup. `PluginUtils.getServerVersion()` parses `Bukkit.getBukkitVersion()`
into a comparable integer (`1.16.5` → `1165`) if you need to branch on server version at
runtime.

## See also

- [WUtils Common](common.md) — module overview and dependency table.
- [Scheduling and Async Work](async.md) — the vendored `Terminable`/`CompositeTerminable`
  machinery `bind`/`bindModule` build on, and the plugin-lookup caveat.
- [Events](events.md) — `EventRegistry`/`ListenerRegistry`, independent of this package
  but commonly bound alongside it.
- [Ranges, Durations and Values](values.md) — loading typed config values inside a
  `LOAD`-scoped step.
- [contributor wiki: Plugin Composition](../../dev/common/plugin.md) — internals,
  including the `CompositeStep` scope-nesting trap this page omits.
- [contributor wiki: Loadables](../../dev/common/loadables.md) and
  [Commands](../../dev/common/commands.md) — internals for the sections above.
