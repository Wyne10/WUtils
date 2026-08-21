# Anvil

`AnvilUtils` (`common/src/main/java/me/wyne/wutils/common/anvil/AnvilUtils.java`) is the entire
`anvil/` package. It exists to make **custom anvil recipes** work — it reimplements the anvil
result-slot pickup from scratch, so a plugin-supplied result can actually be taken out of the
anvil.

## Why it exists

A plugin that wants its own anvil recipe puts a custom item into the anvil's result slot. The
player then cannot take it: the server never computed that result itself, so it refuses the
result-slot click and Bukkit reports the click as `InventoryAction.NOTHING`. The item sits there,
visible and unobtainable.

`AnvilUtils` closes that gap. `isClickValid` recognises the blocked click, and `getResult`
performs the whole pickup by hand — moving the item to the right destination, charging the repair
cost, consuming the inputs, and playing the sound — reproducing what the server would have done
if the recipe had been a vanilla one.

Because the pickup is reimplemented rather than delegated, the surrounding behaviour had to be
reimplemented too. That is where the rest of this page comes from: the creative/spectator cost
exemption and the progressive anvil damage are parts of the vanilla behaviour being reproduced,
not the reason the class exists.

Call both from an `InventoryClickEvent` listener: `getResult` is a no-op when the click is not one
of these blocked custom-result pickups, so it is safe to call unconditionally.

## isClickValid

`isClickValid(InventoryClickEvent)` (`common/src/main/java/me/wyne/wutils/common/anvil/AnvilUtils.java:54-63`)
returns `true` only when all of these hold:

- the inventory is an `AnvilInventory` and the click targets the `RESULT` slot,
- Bukkit resolved the click as `InventoryAction.NOTHING`,
- the result slot is not empty (checked via `ItemUtils.isNullOrAir` — see [Items](items.md)),
- the clicking player's level covers the repair cost, or the player's game mode is in
  `ANVIL_DAMAGE_IMMUNITY`.

## getResult

`getResult(InventoryClickEvent)` (`common/src/main/java/me/wyne/wutils/common/anvil/AnvilUtils.java:75-149`)
is a no-op unless `isClickValid` passes for the same event. When it proceeds, it:

1. Cancels the original event and works out an equivalent `InventoryAction` for the click type
   (hotbar swap, off-hand swap, drop, control-drop, or plain pickup/shift-move), mirroring vanilla
   behavior for each.
2. Bails out early if the destination for that action is already occupied (target hotbar slot,
   off-hand, cursor, or no free inventory slot for a shift-click) — matching vanilla's refusal to
   perform a pickup that would overwrite something.
3. Fires a synthetic `InventoryClickEvent` with `Event.Result.ALLOW` so other plugins still see a
   normal click event for the pickup, and stops if that event is cancelled by a listener.
4. Places the result item at the appropriate destination, dropped items get a small outward
   velocity, a 40-tick pickup delay, and the player set as thrower.
5. Zeroes out the result stack, deducts the repair cost from the player's level (skipped for
   `ANVIL_DAMAGE_IMMUNITY` game modes), and consumes the matching amount from both input slots.
6. Plays `Sound.BLOCK_ANVIL_USE`.

## Anvil damage

Outside of immune game modes, each successful pickup has `ANVIL_DAMAGE_CHANCE` (0.12) probability
of advancing the anvil block one stage through `ANVIL_DAMAGE_QUEUE`
(`ANVIL` → `CHIPPED_ANVIL` → `DAMAGED_ANVIL` → `AIR`). The transition fires a Paper
`AnvilDamagedEvent`; if a listener cancels it, the block type is rolled back to its pre-transition
state. Reaching `AIR` (the anvil destroyed) also plays `Sound.BLOCK_ANVIL_DESTROY`. Damage is
skipped entirely if the anvil's location is unknown or its current block type is not one of
`ANVIL_TYPES`.

## Constants

| Field | Value |
|---|---|
| `ANVIL_DAMAGE_CHANCE` | `0.12` |
| `ANVIL_DAMAGE_IMMUNITY` | `{CREATIVE, SPECTATOR}` |
| `ANVIL_TYPES` | `{ANVIL, CHIPPED_ANVIL, DAMAGED_ANVIL}` |
| `ANVIL_DAMAGE_QUEUE` | `[ANVIL, CHIPPED_ANVIL, DAMAGED_ANVIL, AIR]` |

See `common/src/main/java/me/wyne/wutils/common/anvil/AnvilUtils.java:38-44`.

## See also

- [Items](items.md) for the `isNullOrAir`/`isNotNullOrAir` checks used here.
- [Blocks](blocks.md) for the sibling "actually natural" block-break helper.
