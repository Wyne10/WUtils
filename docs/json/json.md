# WUtils Json

`json` is a minimal annotation-driven Gson (de)serialization helper: mark a
field with `@JSON(path=...)`, register the object that owns it, and a
`JsonRegistry` can then write that field's current value to a JSON file and
load it back, one file per registered field.

- Directory: `json/`
- Gradle project: `:WUtils-json`
- Maven artifact: `io.github.wyne10:wutils-json`
- Version: `1.2.1`
- Root package: `me.wyne.wutils.json`

Source of these facts: `json/build.gradle`.

## Dependencies

| Dependency | Scope | Notes |
|---|---|---|
| `com.google.code.gson:gson:2.14.0` | `compileOnly` | Consumer must supply Gson on the runtime classpath — it is not bundled. |

`json` has **no dependency on any other WUtils module**, and — unlike almost
everything else in this project — no dependency on Bukkit/Paper at all. It is
plain Java and works in any JVM context, not just inside a Minecraft plugin.

## Package inventory

The whole module is one package, `me.wyne.wutils.json`, three classes:

| Class | Role |
|---|---|
| `JSON` | Runtime, field-targeted annotation. `path()` names the backing JSON file. `json/src/main/java/me/wyne/wutils/json/JSON.java` |
| `JsonObject` | Record holding one registration: `holder`, `field`, `type`. `json/src/main/java/me/wyne/wutils/json/JsonObject.java` |
| `JsonRegistry` | The entire implementation — registration bookkeeping, `write()`, `load()`. `json/src/main/java/me/wyne/wutils/json/JsonRegistry.java` |

## The model

A `JsonRegistry` (`JsonRegistry.java:29-237`) holds one `Map<String, JsonObject>`,
keyed by path. Getting a field into that map happens one of two ways:

- **`registerObject(Object)`** (`JsonRegistry.java:61-67`) scans the object's
  own declared fields for `@JSON`, and for each annotated field calls
  `registerField(holder, field)`, which registers it under the annotation's
  `path()`.
- **`registerField`/`register`** (`JsonRegistry.java:94-125`) register a
  single field without scanning. `registerField` still reads the path from
  the field's `@JSON` annotation; `register` takes the path as an explicit
  argument and ignores annotations entirely. Each has an overload taking an
  explicit `Type` to use for Gson (de)serialization instead of the field's
  own generic type — useful when reflection's default type inference isn't
  enough, e.g. for generic collection fields.

Once registered, a field is fully described by a `JsonObject`: the instance
that owns it (`holder`), the `Field` itself, and the `Type` Gson should use.

- **`write()`** (`JsonRegistry.java:145-161`) iterates every registration,
  reads the field's current value via reflection (`setAccessible(true)`),
  and serializes it with Gson to a file at `<directory>/<path>`, creating
  parent directories and the file itself if they don't exist.
- **`load()`** and its overloads (`JsonRegistry.java:170-211`) do the
  reverse: read the file at `<directory>/<path>`, deserialize with Gson, and
  reflectively set the field on its holder. `load(String, JsonObject)`
  (`JsonRegistry.java:199-211`) is where the other two overloads bottom out;
  it does nothing if the file doesn't exist or is empty, rather than
  erroring. `load(Object holder)` restricts the reload to whatever fields
  are registered for that one holder.

## Registration keying — read this before registering more than one thing

The backing map is keyed by **path alone**, shared across every
registration made on a given `JsonRegistry` instance — including `global`,
below. This has consequences worth knowing up front:

- **Path collisions are silent.** A second `register`/`registerField`/
  `registerObject` call that lands on a path already in use replaces the
  earlier entry with no warning, even if the new registration belongs to a
  completely unrelated object (`JsonRegistry.java:35-37`, `114-125`).
- **`unregisterObject` removes by path, not by identity.** It walks
  `object`'s `@JSON`-annotated declared fields and removes whatever is
  currently stored at each field's path (`JsonRegistry.java:81-87`) — it
  never checks that the stored entry still belongs to `object`. If another
  registration was later stored under that same path, `unregisterObject`
  evicts *that* live registration instead, not the one it was meant to
  clean up.
- **Registrations made under an explicit path can't be individually
  unregistered.** `unregisterObject` derives the paths it removes by reading
  `@JSON` off the object's declared fields. The two `register` overloads
  (`JsonRegistry.java:114-125`) take the path as an argument instead, so a
  field registered that way — whether or not it carries `@JSON`, and under
  any path differing from its annotation's — ends up under a key
  `unregisterObject` will never derive. There is no per-path removal method,
  so the only way to remove such an entry is `clear()`, which wipes every
  registration on the instance.
- **`registerField` requires `@JSON`.** Both `registerField` overloads read
  the path from `field.getAnnotation(JSON.class).path()`
  (`JsonRegistry.java:94-96`, `105-107`) with no null check; a field lacking
  `@JSON` throws `NullPointerException`, as their JavaDoc states.
- **Only declared fields are scanned.** `registerObject` calls
  `object.getClass().getDeclaredFields()` (`JsonRegistry.java:62`), so
  fields inherited from a superclass are never picked up, `@JSON`-annotated
  or not.

## Directory setup is two-phase

A `JsonRegistry` needs a backing directory before `write()` or the
directory-dependent `load` overloads can do anything. Set it either by
constructing with `JsonRegistry(Gson, File)` or by calling
`setDirectory(File)` afterward; the no-arg constructor leaves it `null`.
`getDirectory()` is `@Nullable` for exactly this reason — it returns `null`
until a directory has been set.

The two sides check at different points. `write()`
(`JsonRegistry.java:145-147`) tests the directory before touching any
registration, so it always throws `NullPointerException` when none is set.
`load()` and `load(Object)` have no check of their own — both funnel into
`load(String, JsonObject)` (`JsonRegistry.java:199-201`), where the check
actually lives. The difference is only observable on an empty registry:
`write()` still throws, while `load()` iterates nothing and returns quietly.
With anything registered, the first entry trips the check, and neither side
does partial work.

## The `global` instance

`JsonRegistry.global` (`JsonRegistry.java:31`) is a static instance meant
for shared use across a plugin, so unrelated code can register fields
against one common registry instead of each owning its own. It starts out
exactly like any `new JsonRegistry()` — no directory set, so
`setDirectory(...)` is still required before `write()`/`load()` will work.

The backing map is a plain `HashMap`. That makes `global` — and any
`JsonRegistry` instance touched from more than one place — unsafe to
register on, write, or load concurrently; the class JavaDoc says so
explicitly. Nothing in this module synchronizes access.

## Relationship to `config`

`json` and the [`config`](../config/config.md) module share the same shape: annotate
declared fields, register the owning object by reflection, then have the registry
read or write those fields for you. A reader already familiar with
`config`'s annotation-driven model will recognize the pattern here —
`@JSON`/`registerObject` doing for individual JSON files roughly what
`config` does for YAML. The two are independent implementations with their
own rules, though (see the sharp edges above).
