# Items

The `item/` package (`common/src/main/java/me/wyne/wutils/common/item/`) covers three concerns:
tool/armor material sets and null/air checks, natural tool damage and natural item drops, and
saving/restoring tile-entity (block-state) data on an `ItemStack`. Three classes: `ItemUtils`,
`TileStateLoader`, `SpawnerLoader`.

## Material sets

`ItemUtils` exposes unmodifiable `Set<Material>` constants built from Paper's `MaterialTags`
(`common/src/main/java/me/wyne/wutils/common/item/ItemUtils.java:34-50`):

| Constant | Contents |
|---|---|
| `AXES`, `PICKAXES`, `SHOVELS`, `HOES`, `SWORDS` | one `MaterialTags` group each |
| `TOOLS` | union of all five above |
| `ARMOR` | union of helmets, chestplates, leggings, boots |

## Null/air checks

`isNullOrAir(ItemStack)` and `isNotNullOrAir(ItemStack)`
(`common/src/main/java/me/wyne/wutils/common/item/ItemUtils.java:53-60`) are the building blocks
used throughout `common/` (see [Inventories](inventories.md), [Anvil](anvil.md)) to treat a `null`
reference and an explicit `Material.AIR` stack as the same "nothing here" case.

## damageNaturally

`damageNaturally(ItemStack, Player)` and `damageNaturally(ItemStack, Player, int damage)`
(`common/src/main/java/me/wyne/wutils/common/item/ItemUtils.java:68-100`; the first overload calls
the second with `damage = 1`) apply natural durability damage to `item` as if used by `player`:

- No-op for a `null` item, an item with no durability (`getMaxDurability() <= 0`), an unbreakable
  item, or a player in creative mode.
- Honors `Enchantment.DURABILITY`: each point of damage has a `1 / (level + 1)` chance of being
  negated.
- Fires `PlayerItemDamageEvent`; a cancelled event stops the damage from applying.
- If the damage breaks the item (durability reaches the max), decrements the stack's amount by
  one, fires `PlayerItemBreakEvent`, resets damage to `0`, and plays
  `Sound.ENTITY_ITEM_BREAK` at the player's location.

## dropActuallyNaturally

Four overloads, all converging on the five-argument-equivalent core
(`common/src/main/java/me/wyne/wutils/common/item/ItemUtils.java:102-139`):

- `dropActuallyNaturally(BlockBreakEvent, ItemStack...)` — drops at the event's block location.
- `dropActuallyNaturally(BlockBreakEvent, Location, ItemStack...)`
- `dropActuallyNaturally(Collection<ItemStack>, BlockBreakEvent)` — drops at the event's block
  location.
- `dropActuallyNaturally(Collection<ItemStack>, BlockBreakEvent, Location)`

**Contract worth knowing:** the `drops` collection/varargs is itself non-null, but its *elements*
may be `null`; null and `Material.AIR` entries are silently filtered out via
`ItemUtils::isNotNullOrAir` before anything is dropped — the same tolerant contract as
[`InventoryUtils.drop`](inventories.md).

Behavior: drops each surviving item naturally at `location` via `World.dropItemNaturally`, then
fires a `BlockDropItemEvent` carrying the resulting item entities so listeners can inspect, remove,
or add entries to the drop list. If the event is cancelled, every entity in its (possibly modified)
list is removed. If not cancelled but the list no longer contains all originally-dropped entities,
whichever ones were removed by a listener are removed from the world too. This is what
[`BlockUtils.breakActuallyNaturally`](blocks.md) uses to apply its rolled drops.

## Tile-state persistence

Some blocks carry state beyond what `BlockStateMeta` captures on its own (e.g. a spawner's
configured mob type). This trio lets that extra state ride along on an `ItemStack`:

- `TileStateLoader` (`common/src/main/java/me/wyne/wutils/common/item/TileStateLoader.java`) — an
  interface with `save(ItemStack, BlockState)` and `load(ItemStack, BlockState)`, each returning
  the object it wrote to.
- `SpawnerLoader` (`common/src/main/java/me/wyne/wutils/common/item/SpawnerLoader.java`) — the only
  built-in implementation, for `CreatureSpawner`. `save` sets the item's display name to the
  spawned entity type's translatable component (with italics stripped) and is a no-op for a
  non-spawner block state. `load` copies delay, min/max spawn delay, max nearby entities, spawn
  count, required player range, spawned type and spawn range from the item's saved state onto a
  live `CreatureSpawner`, then calls `update()`; it's a no-op if the target isn't a spawner, the
  item has no block-state meta, or the item's saved state isn't itself a spawner.
- `ItemUtils.TILE_STATE_LOADERS` (`common/src/main/java/me/wyne/wutils/common/item/ItemUtils.java:192-194`)
  — a `Map<Material, TileStateLoader>` registry, currently just `{SPAWNER: SpawnerLoader}`.

`ItemUtils` ties these together with four methods
(`common/src/main/java/me/wyne/wutils/common/item/ItemUtils.java:146-189`):

- `saveBlockState(ItemStack, BlockState)` — writes `blockState` onto the item's `BlockStateMeta`.
  No-op (returns the item unchanged) if the item type has no block-state meta.
- `saveBlockState(ItemStack, BlockState, TileStateLoader)` — the above, plus runs the given
  loader's `save`.
- `saveBlockStateExtended(ItemStack, BlockState)` — looks up `TILE_STATE_LOADERS` for the state's
  material and uses that loader if one is registered, otherwise falls back to the plain
  `saveBlockState`.
- `loadBlockState(ItemStack, BlockState)` — applies the item's saved `BlockStateMeta`, plus any
  registered loader's `load`, onto `blockState`. Returns `blockState` unchanged if the item has no
  block-state meta, or if no loader is registered for the block's type — so this method restores
  extra state only for types that have a `TileStateLoader`, not for plain `BlockStateMeta` data.
- `loadBlockState(ItemStack, BlockState, TileStateLoader)` — delegates straight to the given
  loader's `load`.

## See also

- [Blocks](blocks.md) — `BlockUtils.breakActuallyNaturally`, the main caller of
  `damageNaturally`/`dropActuallyNaturally`.
- [Inventories](inventories.md) — the sibling null/air-tolerant `drop` overloads.
