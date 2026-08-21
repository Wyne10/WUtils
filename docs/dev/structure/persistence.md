# Persistence

A spawned `WorldStructure` holds its clipboard and its pre-spawn rollback
snapshot only in memory. If the server restarts, that state is gone — the
structure is still pasted in the world, but nothing can `close()` it back
out cleanly anymore. `structure.persistence` solves this by writing a
spawned structure's full state to disk, so it can be closed and rolled back
even after a restart.

Requires WorldEdit on the runtime classpath (`compileOnlyApi` — the
consumer must supply it, see [Dependencies](structure.md#dependencies)),
which is also what brings Gson onto the classpath transitively — this
module does not declare Gson itself.

## `capture()` requires a prior `spawn()`

`WorldStructure#capture()`
(`structure/src/main/java/me/wyne/wutils/structure/WorldStructure.java:110-113`)
packages a structure's current state — unique key, location, region,
clipboard region, transform, clipboard, and the pre-spawn snapshot — into a
`WorldStructureMemento`. It throws `NullPointerException` if called before
`spawn()`, because the snapshot field it reads doesn't exist yet.

## `WorldStructureMemento`

`WorldStructureMemento` (`structure/src/main/java/me/wyne/wutils/structure/persistence/WorldStructureMemento.java:17-25`)
is the plain data carrier: everything `capture()` produces and
`WorldStructure.restore(WorldStructureMemento)`
(`structure/src/main/java/me/wyne/wutils/structure/WorldStructure.java:120-122`)
needs to rebuild a `WorldStructure` without re-running `Structure#create`.
A restored structure carries its restore-snapshot (so `close()` still
works) but has **no modifiers attached** — `spawn/snapshot/paste/edit`
modifiers are transient config, not persisted state, so a restored
structure's own `spawn()` would run with an empty modifier set if called
again (it normally isn't; the paste already exists from before restart).

## `WorldStructureMementoSerializer`

`WorldStructureMementoSerializer`
(`structure/src/main/java/me/wyne/wutils/structure/persistence/WorldStructureMementoSerializer.java:44`)
is a Gson `JsonSerializer`/`JsonDeserializer` for `WorldStructureMemento`.
It splits the memento's state across two kinds of file:

- **Schematic files.** The clipboard and the rollback snapshot are each
  written out as a Sponge-format `.schem` file under a configured
  `schematicDirectory`, named `<uniqueKey>-structure.schem` and
  `<uniqueKey>-snapshot.schem`
  (`structure/src/main/java/me/wyne/wutils/structure/persistence/WorldStructureMementoSerializer.java:82-85`).
  Writing them out is a side effect of `serialize()` — it happens even
  though the method's declared job is producing a `JsonElement`.
- **Everything else as JSON** — unique key, world name, location, transform
  (as an `AffineTransform`'s coefficients, or `null` for the identity
  transform), the protected region's id/transient flag/bounds, and the
  clipboard region's bounds
  (`structure/src/main/java/me/wyne/wutils/structure/persistence/WorldStructureMementoSerializer.java:87-107`).

`deserialize()`
(`structure/src/main/java/me/wyne/wutils/structure/persistence/WorldStructureMementoSerializer.java:117-151`)
reverses this: it reads the schematic files back and reconstructs the
memento's other fields from JSON.

**The world resolver.** Because a `World` (both Bukkit's and WorldEdit's)
isn't itself serializable, the memento stores just the world's name, and a
`Function<String, World>` resolves it back on load
(`structure/src/main/java/me/wyne/wutils/structure/persistence/WorldStructureMementoSerializer.java:47`).
`WorldStructureMementoSerializer.of(File)`
(`structure/src/main/java/me/wyne/wutils/structure/persistence/WorldStructureMementoSerializer.java:60-65`)
builds the default resolver, which looks the world up via
`Bukkit.getWorld(name)` and fails deserialization
(`JsonParseException`) if that world isn't currently loaded — restoring a
structure whose world hasn't finished loading yet (e.g. very early in
server startup) will throw. Pass a custom resolver to the two-argument
constructor if you need different behavior, such as waiting for the world
or mapping renamed worlds.

## `WorldStructurePersistence`

`WorldStructurePersistence`
(`structure/src/main/java/me/wyne/wutils/structure/persistence/WorldStructurePersistence.java:24`)
is the file-level API most consumers actually use — it wraps a
`WorldStructureMementoSerializer` in a pretty-printing `Gson` instance:

- `save(File jsonFile, WorldStructure worldStructure)`
  (`structure/src/main/java/me/wyne/wutils/structure/persistence/WorldStructurePersistence.java:49-56`) —
  calls `worldStructure.capture()` (so it inherits `capture()`'s
  precondition: the structure must already be spawned) and writes the
  result, and its two schematic files, to disk. Creates the parent
  directory if needed.
- `load(File jsonFile)`
  (`structure/src/main/java/me/wyne/wutils/structure/persistence/WorldStructurePersistence.java:63-68`) —
  reads the JSON and schematic files back and calls
  `WorldStructure.restore(...)`. **The returned structure is not
  re-spawned** — the paste already exists in the world from before the
  restart, so calling `spawn()` on it would paste it a second time. Use
  `load()` purely to get back an object you can later `close()` (or hand to
  your own rollback logic), not to re-place the structure.
- `getGson()`
  (`structure/src/main/java/me/wyne/wutils/structure/persistence/WorldStructurePersistence.java:70-72`) —
  exposes the underlying `Gson` for consumers who want to serialize other
  data alongside a memento with the same type adapter registered.

Three constructors let you supply either just a `schematicDirectory`
(default world resolver), a `schematicDirectory` plus a custom world
resolver, or a fully custom `WorldStructureMementoSerializer`
(`structure/src/main/java/me/wyne/wutils/structure/persistence/WorldStructurePersistence.java:28-42`).

## Typical restart-survival flow

1. On spawn, call `worldStructurePersistence.save(file, worldStructure)` to
   persist it.
2. On the next startup, once the target world is loaded, call
   `worldStructurePersistence.load(file)` to get back a `WorldStructure`
   handle for the still-pasted structure.
3. When you want to remove it, call `close()` on the loaded structure —
   this unregisters its WorldGuard region and repastes its rollback
   snapshot, exactly as it would for a structure that never left memory.

## See also

- [WUtils Structure](structure.md) — `WorldStructure#spawn()`/`close()`, and why `capture()` requires a prior spawn.
