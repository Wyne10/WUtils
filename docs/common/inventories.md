# Inventories

`InventoryUtils` (`common/src/main/java/me/wyne/wutils/common/inventory/InventoryUtils.java`) is
the entire `inventory/` package: helpers for adding, dropping and inspecting `ItemStack`s against
inventories, players, and locations.

All of these are item-mutating operations that run on the calling thread — like any other Bukkit
inventory API, call them from the main server thread.

## addItem

Four overloads, all funnelling into `Inventory.addItem`:

- `addItem(Inventory, ItemStack...)`
  (`common/src/main/java/me/wyne/wutils/common/inventory/InventoryUtils.java:26-28`)
- `addItem(Inventory, Collection<ItemStack>)`
- `addItem(Player, ItemStack...)`
- `addItem(Player, Collection<ItemStack>)`

Each returns `true` only if every item fit. Leftovers that didn't fit are **not** returned or
tracked — call `Inventory.addItem` directly if you need the excess map back.

## addOrDrop

Four overloads — `addOrDrop(Player, ItemStack...)`, `addOrDrop(Player, Collection<ItemStack>)`,
and `boolean setOwner` variants of both
(`common/src/main/java/me/wyne/wutils/common/inventory/InventoryUtils.java:46-68`). Adds items to
`player`'s inventory, then drops whatever didn't fit at their feet via `drop` (below). When
`setOwner` is `true`, dropped items are marked as owned by `player`, subject to Bukkit's normal
pickup-delay-vs-owner semantics.

## drop

Four overloads: `drop(Player, ItemStack...)`, `drop(Player, Collection<ItemStack>)`,
`drop(Player, boolean setOwner, ItemStack...)`, `drop(Player, boolean setOwner,
Collection<ItemStack>)`
(`common/src/main/java/me/wyne/wutils/common/inventory/InventoryUtils.java:74-102`), plus a
location-based `drop(Location, Collection<ItemStack>)`
(`common/src/main/java/me/wyne/wutils/common/inventory/InventoryUtils.java:108-115`).

**Contract worth knowing:** the item collection/varargs argument is itself non-null, but its
*elements* may be `null`, and any element that is `null` or `Material.AIR` is silently filtered
out before dropping — via [`ItemUtils::isNotNullOrAir`](items.md). Callers can pass sparse arrays
or collections through unfiltered without pre-cleaning them. Every drop gets `pickupDelay` set to
`0`; the player-targeted overloads drop at the player's current location and world.

## getAffectedItems

`getAffectedItems(InventoryClickEvent)`
(`common/src/main/java/me/wyne/wutils/common/inventory/InventoryUtils.java:122-131`) collects the
item(s) a click event would actually move:

- the clicked slot's current item, always checked;
- additionally the hotbar slot's item, for a `ClickType.NUMBER_KEY` click;
- additionally the off-hand item, for a `ClickType.SWAP_OFFHAND` click.

Null/air items are omitted from the result at each step; the returned `List` is immutable
(`List.copyOf`).

## See also

- [Items](items.md) for `ItemUtils.isNotNullOrAir`/`isNullOrAir` and the matching null/air
  tolerance in `ItemUtils.dropActuallyNaturally`.
- [Anvil](anvil.md) for another consumer of `InventoryClickEvent` internals.
