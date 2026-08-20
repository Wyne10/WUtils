# Gson Serializers

`me.wyne.wutils.common.serialization` holds four Gson adapters for types that Gson
cannot handle by reflection: two Bukkit types that need Bukkit's own serialization, and
two WUtils cooldown types whose meaning depends on when they are read.

All four implement both `JsonSerializer<T>` and `JsonDeserializer<T>`, so each is
registered once and works in both directions.

## Registration

Gson is not a declared dependency of `common` — it arrives transitively through
paper-api. Register the adapters on a `GsonBuilder` with `registerTypeAdapter`, passing
the target class and an instance, then call `create()`. `CooldownMapSerializer` is the
exception: it is generic and needs its key class at construction
(`CooldownMapSerializer.java:22-24`), so you register one instance per key type against
the appropriate parameterised type.

If you need Gson-based serialization driven by annotations rather than hand-registered
adapters, that is the separate [`json`](../json/json.md) module.

## The Base64 pair

| Adapter | Handles |
|---|---|
| `Base64ItemStackSerializer` (`Base64ItemStackSerializer.java:21`) | a single `ItemStack` |
| `Base64InventorySerializer` (`Base64InventorySerializer.java:23`) | a whole `Inventory` |

Both write a **single JSON string primitive**: the object is pushed through
`BukkitObjectOutputStream` into a byte array, which `Base64Coder.encodeLines` turns
into Base64 text (`Base64ItemStackSerializer.java:40-49`,
`Base64InventorySerializer.java:47-59`). Reading reverses it through
`BukkitObjectInputStream`.

The inventory form additionally writes the inventory's size as an `int` before the
items and reconstructs the container with `Bukkit.createInventory(null, size)` on the
way back (`Base64InventorySerializer.java:33-45`). That has two effects worth knowing:
the restored inventory has **no holder** — the `null` first argument — and only size
and contents survive. A restored inventory is a plain container, not the block or
entity inventory it came from.

Both adapters wrap any `IOException` or `ClassNotFoundException` in a
`RuntimeException` rather than Gson's `JsonParseException`
(`Base64ItemStackSerializer.java:46-48`, `Base64InventorySerializer.java:44-46`), so a
corrupt payload escapes as an unchecked exception that a `try`/`catch` around Gson
looking for `JsonParseException` will not catch.

### Portability is the real constraint

This format is Bukkit's own Java serialization, Base64-wrapped. It is compact and
handles every item attribute including NBT, but it is **only readable by a server that
can deserialize those same classes**. Data written on one server version is not
guaranteed to load on another, and the failure is an exception at read time, not a
graceful degradation. Treat Base64 item data as a same-version storage format — fine
for a database of player inventories on a live server, risky as a long-term archive or
an interchange format between servers.

## The cooldown pair

These two are more interesting than they look, because both persist **remaining time,
not an absolute timestamp**.

`PeriodSerializer` (`PeriodSerializer.java:13`) writes `period.getRemaining()` as a
number (`PeriodSerializer.java:22-24`) and, on the way back, reconstructs the period as
`System.currentTimeMillis() + <stored value>` (`PeriodSerializer.java:17-19`).

`CooldownMapSerializer` (`CooldownMapSerializer.java:17`) does the same per entry: it
writes a JSON object of `key.toString()` → remaining millis
(`CooldownMapSerializer.java:41-50`) and rebuilds by `put(key, remaining)`
(`CooldownMapSerializer.java:27-38`), deserializing each key string back into `T`
through the supplied key class and the surrounding `JsonDeserializationContext`.

**The clock stops while the data is at rest.** A cooldown with 30 seconds left, saved
and reloaded an hour later, still has 30 seconds left. For a server restart that is
almost always what you want — players do not get their cooldowns wiped by a restart,
and they do not lose them either. It is the wrong behaviour if a cooldown is meant to
track wall-clock time, such as a daily reward: use an absolute timestamp for those.

**`CooldownMapSerializer` prunes as it writes.** Entries that are no longer cooldowned
are skipped (`CooldownMapSerializer.java:45-46`), so expired entries never reach the
JSON. Given that `CooldownMap` itself never evicts anything (see
[Durations and Cooldowns](durations.md)), a save/load cycle is in practice the only
thing that clears expired entries out of a long-lived map.

**Keys round-trip through their string form.** Serialization uses `key.toString()`;
deserialization feeds that string to Gson as a `JsonPrimitive` and asks for the key
class. This works for `UUID`, `String` and enums, and it silently misbehaves for any
key type whose `toString()` is not a faithful, parseable representation.

## See also

- [WUtils Common](common.md) — module overview.
- [Durations and Cooldowns](durations.md) — `CooldownMap` and `Period` themselves.
- [Items](items.md) and [Inventories](inventories.md) — the Bukkit types the Base64
  adapters cover.
- [WUtils Json](../json/json.md) — annotation-driven Gson serialization as a module.
