# JSON Storage

`wutils-json` is a minimal, annotation-driven Gson wrapper: mark a field with
`@JSON(path = ...)`, register the object that owns it, and a `JsonRegistry` writes that
field's current value to its own JSON file and reads it back. Reach for it when you want
one field — a `Map`, a `List`, a settings object — persisted to its own small JSON file
without hand-writing Gson calls. It is not a database and has no query capability; for
anything relational, see [Databases](../jdbc/jdbc.md).

## Adding it

```kotlin
dependencies {
    implementation("io.github.wyne10:wutils-json:1.2.1")
}
```

You also need Gson on your runtime classpath — WUtils declares it `compileOnly`, so it
isn't bundled in `wutils-json`'s jar:

```kotlin
dependencies {
    implementation("com.google.code.gson:gson:2.14.0")
}
```

Paper already ships Gson internally, but relying on the server's copy rather than
declaring your own is fragile across versions — add it explicitly. `wutils-json` has no
dependency on any other WUtils module and no dependency on Bukkit/Paper at all; it works
in any plain JVM context.

## A complete round trip

Annotate the field you want backed by its own JSON file, then register the owning
object with a `JsonRegistry`:

```java
public class PlayerStats {
    @JSON(path = "stats.json")
    private Map<UUID, Integer> scores = new HashMap<>();
}

PlayerStats stats = new PlayerStats();

JsonRegistry registry = new JsonRegistry(new Gson(), new File(dataFolder, "data"));
registry.registerObject(stats);

registry.write(); // writes data/stats.json with the current (empty) map

stats.getScores().put(playerUuid, 42);
registry.write(); // overwrites data/stats.json

registry.load(); // reads data/stats.json back into stats.scores
```

`write()` serializes every registered field's *current* value; `load()` reads each
file back and reflectively sets the field, so it works on private fields without a
getter/setter. Both use reflection (`setAccessible(true)`) — you don't need to expose
the field publicly.

### Using the shared `global` registry

`JsonRegistry.global` is a static instance meant for unrelated parts of a plugin to
register fields against a single shared registry instead of each owning one:

```java
JsonRegistry.global.setDirectory(new File(dataFolder, "data"));
JsonRegistry.global.registerObject(stats);
JsonRegistry.global.write();
```

It starts exactly like `new JsonRegistry()` — with no directory set — so
`setDirectory(...)` is still required before it does anything useful.

## Registering without an annotation

`register`/`registerField` let you register a single field directly, optionally with an
explicit `Type` when reflection's inferred generic type isn't enough (a common case for
generic collection fields):

```java
Field field = PlayerStats.class.getDeclaredField("scores");
Type mapType = new TypeToken<Map<UUID, Integer>>() {}.getType();

registry.register(stats, field, "stats.json", mapType);
```

`registerField` (both overloads) still reads the path from the field's `@JSON`
annotation, so the field must carry one — calling it on an unannotated field throws
`NullPointerException`. `register` takes the path as an explicit argument instead and
ignores `@JSON` entirely, so it works on any field, annotated or not.

## Sharp edges

**Directory setup is required before use, but checked differently on each side.**
`write()` always throws `NullPointerException` if no directory has been set, even with
nothing registered. `load()` doesn't check up front — it silently does nothing on an
empty registry, and only throws once it reaches the first registered entry. Either way,
call `setDirectory(...)` (or use the `JsonRegistry(Gson, File)` constructor) before doing
anything else.

**`registerField` requires `@JSON` and throws `NullPointerException` without it.** If you
want to register a plain field with no annotation, use `register` instead.

**Registrations are keyed by path alone, shared across the whole registry — including
`global`.** A second registration that lands on an already-used path silently replaces
the first one, even if it belongs to a completely different object. Give every field a
distinct path within one registry, especially on `global`, where unrelated code might
collide without realizing it.

**`unregisterObject` removes by path, not by identity.** It reads the object's `@JSON`
paths and removes whatever is currently stored there — it never checks that the stored
entry still belongs to the object you passed in. If another registration landed on the
same path afterward, `unregisterObject` evicts *that* one instead. Fields registered
through `register`/`registerField(Object, Field, Type)` with an explicit path can't be
individually unregistered at all this way — `clear()` is the only way to remove them,
and it wipes every registration on the instance.

**Only declared fields are scanned.** `registerObject` only looks at fields declared
directly on the object's own class — fields inherited from a superclass are never
picked up, `@JSON` or not.

**Not thread-safe.** The backing map is a plain `HashMap`. Registering, writing or
loading the same `JsonRegistry` — including `global` — from more than one thread at once
is unsafe; nothing in this module synchronizes access.

## See also

- [Configuration](../config/config.md) — the same annotate-and-register shape, applied
  to YAML config files instead of individual JSON files. If you're already familiar with
  one, the other will feel immediately recognizable — but the two are independent
  implementations with their own rules.
- [Databases](../jdbc/jdbc.md) — the other plain-Java, no-Bukkit module, for anything
  that needs real querying instead of whole-file persistence.
