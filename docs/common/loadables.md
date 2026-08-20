# Loadables

`me.wyne.wutils.common.loadable` is a small registry for "things that read themselves
out of a config section when the plugin loads". A class implements `Loadable`,
registers itself, and the `Loader` calls it with the right `ConfigurationSection` at
the right time, in a controlled order.

It is deliberately independent of the [`config`](../config/config.md) module: `config`
*generates and writes* YAML from annotated fields, while `Loadable` is about *pulling*
values out of a section a plugin already has. A class can use both.

## The three pieces

| Type | Role |
|---|---|
| `Loadable` (`Loadable.java:13`) | The interface you implement. One required method, `load(ConfigurationSection)`, plus four defaulted metadata methods. |
| `LoadableMeta` (`LoadableMeta.java:23-34`) | A `RUNTIME`-retained, `TYPE`-targeted annotation supplying those four metadata values declaratively. |
| `Loader` (`Loader.java:18`) | Holds the registrations and drives one loading pass. |

## Metadata, and how it resolves

`Loadable` gives every implementation four defaulted methods, and each one has the
same shape: if the class carries `@LoadableMeta`, read the value from the annotation;
otherwise fall back to a hardcoded default.

| Method | `@LoadableMeta` element | Default when the annotation is absent |
|---|---|---|
| `getPath()` (`Loadable.java:19-24`) | `path()` | `Loader.DEFAULT_PATH`, i.e. `config.yml` (`Loader.java:20`) |
| `getPriority()` (`Loadable.java:27-32`) | `priority()` | `0` |
| `isLate()` (`Loadable.java:35-40`) | `late()` | `false` |
| `persist()` (`Loadable.java:43-48`) | `persist()` | `true` |

The annotation's own defaults (`LoadableMeta.java:25-33`) match the interface's
fallbacks exactly, so annotating a class and leaving every element unset behaves
identically to not annotating it at all.

Because these are `default` methods reading `getClass()`, an implementation is free to
override any of them directly and ignore the annotation — useful when a value has to
be computed rather than declared.

## Registration

`Loader` keeps two maps and exposes a shared instance, `Loader.global`
(`Loader.java:22`):

- `registerConfig(path, section)` (`Loader.java:35-37`) associates a path string with a
  `ConfigurationSection`.
- `registerLoadable(loadable)` (`Loader.java:28-30`) registers a loadable, **capturing
  its `getPath()` at registration time** into the map's value. A loadable whose path
  changes afterwards keeps loading from the path it had when it registered.

The loadable map is a `LinkedHashMap`, so registration order is retained and becomes
the tiebreak for equal priorities.

## What `load(Plugin)` actually does

`Loader.load` (`Loader.java:44-56`) runs one pass:

1. **Sort by ascending priority.** Lower numbers load first. `Comparator.comparingInt`
   over a stream of the `LinkedHashMap`'s entries with `forEachOrdered`, so ties keep
   registration order and the whole pass is deterministic.
2. **Skip anything whose path has no registered config.** The check is
   `configMap.containsKey` (`Loader.java:48`) — a loadable registered for a path that
   was never given a section is silently passed over. No warning, no exception. This is
   the most likely reason a `load` implementation "never runs".
3. **Dispatch, immediately or late.** If `isLate()` is true the call is wrapped in
   `Bukkit.getScheduler().runTask(plugin, ...)` and runs on the **next server tick**;
   otherwise it runs inline on the calling thread (`Loader.java:49-52`).
4. **Unregister the non-persistent.** After the loop,
   `loadableMap.keySet().removeIf(loadable -> !loadable.persist())`
   (`Loader.java:55`) drops every loadable that declared `persist() == false`, so it
   loads exactly once and is skipped by any later `load` call.

### Two consequences worth planning around

**`isLate()` breaks the priority ordering.** Late loadables are not sorted after the
immediate ones — they are *deferred to a later tick*. Priority orders the scheduling,
not the execution. Every late loadable therefore runs after every immediate one
regardless of its priority number, and late loadables run relative to each other in
priority order only because they were scheduled in that order. Use `late()` for work
that needs other plugins to have finished enabling, not as a sorting mechanism.

**Removal is unconditional, not conditional on having loaded.** Step 4 does not check
whether the loadable actually ran. A non-persistent loadable that was skipped at step 2
because its config was missing is still unregistered, so registering the config
afterwards and calling `load` again will not pick it up.

## See also

- [WUtils Common](common.md) — module overview.
- [Config Utilities](config-utils.md) — reading typed values inside a `load`
  implementation.
- [WUtils Config](../config/config.md) — the separate annotation-driven config
  generation module.
- [Plugin Composition](plugin.md) — `Step`/`StepScope`, the other ordering mechanism in
  this module, which sequences plugin startup work rather than config loading.
