# Blocks

The `block/` package (`common/src/main/java/me/wyne/wutils/common/block/`) reproduces vanilla
"natural" block-break behavior — drops, tool damage, experience orbs — for breaks that Bukkit
doesn't otherwise treat as natural, such as breaks triggered by custom mechanics instead of a real
`BlockBreakEvent`. It has two classes: `BlockUtils` and `NaturalBlockBreakEvent`.

## BlockUtils.breakActuallyNaturally

`breakActuallyNaturally(Block, ItemStack, Player)`
(`common/src/main/java/me/wyne/wutils/common/block/BlockUtils.java:46-73`) breaks `block` as if
mined naturally by `player` using `tool`:

1. `tool` may be `null`, treated as an empty hand.
2. No-op for any block type in `UNBREAKABLE_BLOCKS` (barrier, bedrock, jigsaw, structure/command
   blocks, portals, fluids, air variants, structure void — see
   `common/src/main/java/me/wyne/wutils/common/block/BlockUtils.java:29-36` for the full list).
3. Rolls `block.getDrops(tool, player)`, then fires a `NaturalBlockBreakEvent` that listeners can
   cancel or adjust before anything is applied — see below.
4. If the event isn't cancelled: clears a `Container` block's inventory when the event says not to
   drop items, sets the block to `AIR`, damages the tool via
   [`ItemUtils.damageNaturally`](items.md) if the event says to, spawns an experience orb sized by
   the event's `expToDrop` if positive, and drops the rolled items via
   [`ItemUtils.dropActuallyNaturally`](items.md) — skipped entirely if `player` is in creative
   mode.

## BlockUtils.setExpDrop

`setExpDrop(BlockBreakEvent)` (`common/src/main/java/me/wyne/wutils/common/block/BlockUtils.java:80-84`)
sets `event`'s experience drop to a random amount from `NaturalBlockBreakEvent.EXP_DROPS` for the
broken block's type. No-op in creative or adventure mode, and no-op for block types absent from
`EXP_DROPS`. `breakActuallyNaturally` calls this itself (skipping silk-touch tools) before firing
its event, so callers normally don't need to call it directly.

## BlockUtils.getYaw

`getYaw(BlockFace)` (`common/src/main/java/me/wyne/wutils/common/block/BlockUtils.java:90-98`)
converts a cardinal `BlockFace` to the yaw a player would need to face that direction: `SOUTH` →
`0°`, `WEST` → `90°`, `NORTH` → `180°`, `EAST` → `270°`. Any non-cardinal face, including
diagonals, yields `0.0f`.

## NaturalBlockBreakEvent

`NaturalBlockBreakEvent` (`common/src/main/java/me/wyne/wutils/common/block/NaturalBlockBreakEvent.java`)
extends Bukkit's `BlockBreakEvent` and is the event `breakActuallyNaturally` fires so listeners can
cancel or adjust the break before drops/damage are applied. Beyond the inherited
`setExpToDrop`/`setDropItems`/`setCancelled`, it adds one field:

- `damageTool()` / `setDamageTool(boolean)` (default `true`) — whether the tool used should take
  natural durability damage.

`EXP_DROPS` (`common/src/main/java/me/wyne/wutils/common/block/NaturalBlockBreakEvent.java:21-30`)
is a `Map<Material, Range<Integer>>` giving vanilla experience-orb count ranges for ore blocks and
spawners (nether gold ore, coal, redstone, lapis, nether quartz, diamond, emerald ores, and
spawners). `Range`/`ClosedIntRange` are documented in [Ranges](ranges.md).

## See also

- [Items](items.md) — `ItemUtils.damageNaturally` and `ItemUtils.dropActuallyNaturally`, both used
  by `breakActuallyNaturally`.
- [Ranges](ranges.md) — the `Range`/`ClosedIntRange` types backing `EXP_DROPS`.
