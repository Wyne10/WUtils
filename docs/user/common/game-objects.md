---
description: >-
  Day-to-day helpers for ItemStacks, Players, Inventories, Blocks and Worlds, plus the
  anvil pickup fix and sound and particle parsing.
---

# Items, Players and Worlds

The bulk of `wutils-common`'s day-to-day surface: helpers organized around what you're
actually holding when you reach for them — an `ItemStack`, a `Player`, an `Inventory`, a
`Block`, a `World`/`Location`. Also covered here: the anvil pickup fix, sound/particle
helpers, and randomness/placeholder one-liners too small for their own page.

## Items

`ItemUtils` covers three things: material-set constants, null/air checks, and natural
tool damage/drops.

**Material sets.** `ItemUtils.TOOLS`, `.ARMOR`, and the individual `.AXES`/`.PICKAXES`/
`.SHOVELS`/`.HOES`/`.SWORDS` are unmodifiable `Set<Material>` built from Paper's
`MaterialTags` — useful for "is this a tool" checks without hand-rolling a switch.

**Null/air checks.** `ItemUtils.isNullOrAir(item)` / `isNotNullOrAir(item)` treat a
`null` reference and an explicit `Material.AIR` stack as the same "nothing here" case.
Used throughout this page's other helpers, and handy in your own code for the same
reason.

**`damageNaturally(item, player)`** applies durability damage as if the player used the
item normally — honoring unbreaking, creative-mode immunity, `PlayerItemDamageEvent`, and
breaking the item (with the vanilla break sound and a `PlayerItemBreakEvent`) when
durability runs out. It's what `BlockUtils.breakActuallyNaturally` (below) uses
internally, but you can call it directly for damage from your own mechanics.

**`dropActuallyNaturally(event, items...)`** drops items the way `World.dropItemNaturally`
would, but fires a `BlockDropItemEvent` around it so other plugins see the drop the same
way they'd see a real natural break — respecting cancellation and any listener edits to
the drop list:

```java
import me.wyne.wutils.common.item.ItemUtils;

ItemUtils.dropActuallyNaturally(blockBreakEvent, myCustomDrop);
```

Passed items may be `null` or `Material.AIR` — they're silently filtered out before
anything drops, so you don't need to pre-clean a sparse array yourself.

**Tile-state persistence.** If you're saving a block as an item (e.g. a "place this
configured spawner" feature) and need more than `BlockStateMeta` captures — like a
spawner's configured mob type — use `ItemUtils.saveBlockStateExtended`/`loadBlockState`
instead of the plain `saveBlockState`/`loadBlockState` overloads. The extended versions
look up a `TileStateLoader` for the block's material (currently only `SpawnerLoader`,
for `CreatureSpawner`) and use it if one's registered, otherwise fall back to plain
`BlockStateMeta` copying.

## Inventories

`InventoryUtils` — adding items, dropping the overflow, reading what a click affects.
All of it runs on the calling thread, like any Bukkit inventory call.

| Method | Use when |
|---|---|
| `addItem(Inventory\|Player, items...)` | you just want `Inventory.addItem` with a `boolean` "did it all fit" result |
| `addOrDrop(player, items...)` | add what fits, drop the rest at the player's feet |
| `drop(player, items...)` | skip adding, drop straight at the player |
| `drop(location, items)` | drop at an arbitrary location instead of a player |
| `getAffectedItems(clickEvent)` | figure out every item a click would move, including hotbar-swap and offhand-swap variants |

`addOrDrop` is the one to reach for by default when you're giving a player loot and don't
want overflow silently lost:

```java
import me.wyne.wutils.common.inventory.InventoryUtils;

InventoryUtils.addOrDrop(player, rewardItem1, rewardItem2);
```

A `setOwner` overload exists on both `addOrDrop` and `drop` — pass `true` to mark dropped
items as owned by the player, subject to Bukkit's normal pickup-delay-vs-owner rules.
`null`/air entries in the item collection/varargs are filtered out before dropping,
matching `ItemUtils.dropActuallyNaturally`'s tolerance above.

`addItem` does **not** return the leftover items — only whether everything fit. Call
`Inventory.addItem` directly if you need the actual overflow map back instead of just a
boolean.

## Players

`PlayerUtils` — vanilla experience-level math, plus an existence check.

| Method | Does |
|---|---|
| `levelToExp(level)` | total XP points needed to reach `level` from zero |
| `expToLevel(exp)` | inverse: level reached by a total point count |
| `expToLevelUp(level)` | points needed to go from `level` to `level + 1` |
| `currentExp(player)` | player's current level + fractional progress, as one total point count |
| `setExp(player, exp)` | resets to level 0, then grants `exp` total points |
| `addExp(player, exp)` | adds `exp` on top of current total (round-trips through `setExp`) |
| `exists(offlinePlayer)` | `true` if online, or has played on this server before |

```java
import me.wyne.wutils.common.player.PlayerUtils;

long total = PlayerUtils.currentExp(player);
PlayerUtils.addExp(player, 50);
```

These methods take and return `long`, but very large totals still hit `int` limits:
`levelToExp` caps at `Integer.MAX_VALUE` from level 21864 upward, and `setExp`/`addExp` wrap
if the total leaves the `int` range.

`exists` is the same "is this a real player" check `CommandUtils.getOfflinePlayer`
applies internally — see [Plugin Setup](plugin.md#commands) — useful outside a command
context too, e.g. validating a player name typed into a config file.

## Blocks

`BlockUtils.breakActuallyNaturally(block, tool, player)` is for breaking a block from
your own game logic (not a real `BlockBreakEvent`) while still getting vanilla-accurate
drops, tool damage, and experience orbs:

```java
import me.wyne.wutils.common.block.BlockUtils;

BlockUtils.breakActuallyNaturally(block, player.getInventory().getItemInMainHand(), player);
```

It rolls `block.getDrops(tool, player)`, fires a `NaturalBlockBreakEvent` (a
`BlockBreakEvent` subclass — cancel or adjust it like any other break event, plus its own
`setDamageTool(boolean)`), and on an uncancelled event: clears a container's inventory if
told not to drop items, sets the block to air, damages the tool via `ItemUtils.damageNaturally`
if told to, spawns XP, and drops items via `ItemUtils.dropActuallyNaturally`. It's a no-op
for `player` in creative mode, and for anything in the built-in unbreakable set (bedrock,
barriers, portals, fluids, and similar).

`tool` may be `null` (treated as an empty hand). `BlockUtils.getYaw(blockFace)` converts
a cardinal direction to the yaw a player would need to face it — useful for spawning
something oriented the way a block faces.

## Worlds and locations

**Finding the highest block, off-thread-safe.** `WorldUtils.getHighestBlockAtAsync`/
`getHighestLocationAtAsync` load the target chunk asynchronously first, so it's safe to
call from off the main thread — but the completion callback itself runs back on the
**main thread** (per Bukkit's own `getChunkAtAsync` contract), so you can touch the
Bukkit API directly inside `.thenAccept(...)` without hopping threads again:

```java
import me.wyne.wutils.common.world.WorldUtils;

WorldUtils.getHighestLocationAtAsync(world, x, z)
        .thenAccept(location -> spawnStructureAt(location));
```

If the server's height map is stale and reports air for that column, which
FastAsyncWorldEdit can leave behind after clearing blocks, the lookup walks down to the first
solid or liquid block instead of returning air.

Overloads accept `(World, int/double x, int/double z)`, a `Vector`, or a `Location`.
**Pass a `Location` with a non-null world** — the world-based overloads read
`location.getWorld()` directly, and Bukkit allows that to be `null` for a location whose
world got unloaded. Passing one throws a `NullPointerException` from inside `WorldUtils`,
not at your call site.

**Biome groups.** `BiomePreset` is an enum of named, precomputed biome sets — `COLD`,
`OCEAN`, `HIGHLAND`, `NETHER`, and so on — for config values like "spawn only in cold
biomes" without enumerating every `Biome` constant by hand. `BiomePreset.resolve(list)`
combines several by name, where a `!`-prefixed name (`"!SNOWY"`) subtracts instead of
adds, applied in list order. An unrecognized preset name, or a `Biome` constant name that
doesn't exist on the running server version, is silently skipped rather than erroring —
double check preset names in config if a preset seems smaller than expected. See
[the biomes in each preset](#biome-presets).

**Locations and vectors.** `LocationUtils`/`VectorUtils` offer matching relative-offset
math for `Location`s and `Vector`s: `addRelative(point, ..., face)` variants for
offsetting a point relative to a facing direction instead of world axes (useful for "2
blocks in front of the player" without hand-computing sin/cos), `getMin`/`getMax` for
component-wise bounds, and `LocationUtils.getRandomPointNear(center, radius)` for a
random point on a circle at the same Y. `VectorUtils.zero()` returns a fresh mutable
zero vector each call, safe to `.add(...)` onto without corrupting a shared instance.

### Biome presets

The lists below use Paper 1.16.5's biome names. On another server version, a listed name
that doesn't exist is dropped, and the rule-based presets follow that version's biomes.

| Preset | Biomes |
|---|---|
| `ALL` | Every biome except `MOUNTAIN_EDGE`, `THE_VOID` and `CUSTOM` (77 on 1.16.5) |
| `SNOWY` | `SNOWY_TAIGA`, `SNOWY_TUNDRA`, `SNOWY_BEACH`, `ICE_SPIKES`, `SNOWY_TAIGA_MOUNTAINS`, `FROZEN_RIVER` |
| `COLD` | `MOUNTAINS`, `GRAVELLY_MOUNTAINS`, `WOODED_MOUNTAINS`, `MODIFIED_GRAVELLY_MOUNTAINS`, `FROZEN_OCEAN`, `DEEP_FROZEN_OCEAN`, `TAIGA`, `TAIGA_MOUNTAINS`, `GIANT_TREE_TAIGA`, `GIANT_SPRUCE_TAIGA`, `STONE_SHORE`, `WOODED_HILLS`, `TAIGA_HILLS`, `SNOWY_TAIGA_HILLS`, `GIANT_TREE_TAIGA_HILLS`, `GIANT_SPRUCE_TAIGA_HILLS`, `SNOWY_MOUNTAINS` |
| `SNOWY_OR_COLD` | `SNOWY` and `COLD` combined |
| `LUSH` | `PLAINS`, `SUNFLOWER_PLAINS`, `FOREST`, `FLOWER_FOREST`, `BIRCH_FOREST`, `TALL_BIRCH_FOREST`, `DARK_FOREST`, `DARK_FOREST_HILLS`, `SWAMP`, `SWAMP_HILLS`, `JUNGLE`, `MODIFIED_JUNGLE`, `JUNGLE_EDGE`, `MODIFIED_JUNGLE_EDGE`, `BAMBOO_JUNGLE`, `RIVER`, `BEACH`, `MUSHROOM_FIELDS`, `MUSHROOM_FIELD_SHORE`, `JUNGLE_HILLS`, `BIRCH_FOREST_HILLS`, `TALL_BIRCH_HILLS`, `BAMBOO_JUNGLE_HILLS` |
| `WARM` | `DESERT`, `DESERT_LAKES`, `DESERT_HILLS`, `SAVANNA`, `SHATTERED_SAVANNA`, `BADLANDS`, `ERODED_BADLANDS`, `WOODED_BADLANDS_PLATEAU`, `MODIFIED_WOODED_BADLANDS_PLATEAU`, `SAVANNA_PLATEAU`, `BADLANDS_PLATEAU`, `MODIFIED_BADLANDS_PLATEAU`, `SHATTERED_SAVANNA_PLATEAU` |
| `LUSH_OR_WARM` | `LUSH` and `WARM` combined |
| `OCEAN` | Every biome with `OCEAN` in its name. On 1.16.5: `OCEAN`, `FROZEN_OCEAN`, `DEEP_OCEAN`, `WARM_OCEAN`, `LUKEWARM_OCEAN`, `COLD_OCEAN`, `DEEP_WARM_OCEAN`, `DEEP_LUKEWARM_OCEAN`, `DEEP_COLD_OCEAN`, `DEEP_FROZEN_OCEAN` |
| `HIGHLAND` | Every biome with `MOUNTAINS`, `HILLS`, `PEAKS`, `SLOPES`, `PLATEAU` or `ERODED` in its name. On 1.16.5: `MOUNTAINS`, `SNOWY_MOUNTAINS`, `DESERT_HILLS`, `WOODED_HILLS`, `TAIGA_HILLS`, `JUNGLE_HILLS`, `BIRCH_FOREST_HILLS`, `SNOWY_TAIGA_HILLS`, `GIANT_TREE_TAIGA_HILLS`, `WOODED_MOUNTAINS`, `SAVANNA_PLATEAU`, `WOODED_BADLANDS_PLATEAU`, `BADLANDS_PLATEAU`, `GRAVELLY_MOUNTAINS`, `TAIGA_MOUNTAINS`, `SWAMP_HILLS`, `TALL_BIRCH_HILLS`, `DARK_FOREST_HILLS`, `SNOWY_TAIGA_MOUNTAINS`, `GIANT_SPRUCE_TAIGA_HILLS`, `MODIFIED_GRAVELLY_MOUNTAINS`, `SHATTERED_SAVANNA_PLATEAU`, `ERODED_BADLANDS`, `MODIFIED_WOODED_BADLANDS_PLATEAU`, `MODIFIED_BADLANDS_PLATEAU`, `BAMBOO_JUNGLE_HILLS` |
| `WOODLAND` | `FOREST`, `FLOWER_FOREST`, `BIRCH_FOREST`, `TALL_BIRCH_FOREST`, `DARK_FOREST`, `DARK_FOREST_HILLS`, `TAIGA`, `TAIGA_MOUNTAINS`, `SNOWY_TAIGA`, `SNOWY_TAIGA_MOUNTAINS`, `GIANT_TREE_TAIGA`, `GIANT_SPRUCE_TAIGA`, `JUNGLE`, `MODIFIED_JUNGLE`, `JUNGLE_EDGE`, `MODIFIED_JUNGLE_EDGE`, `BAMBOO_JUNGLE`, `WOODED_MOUNTAINS`, `WOODED_HILLS`, `WOODED_BADLANDS_PLATEAU`, `MODIFIED_WOODED_BADLANDS_PLATEAU`, `TAIGA_HILLS`, `SNOWY_TAIGA_HILLS`, `JUNGLE_HILLS`, `BIRCH_FOREST_HILLS`, `TALL_BIRCH_HILLS`, `GIANT_TREE_TAIGA_HILLS`, `GIANT_SPRUCE_TAIGA_HILLS`, `BAMBOO_JUNGLE_HILLS` |
| `WETLAND` | `RIVER`, `FROZEN_RIVER`, `SWAMP`, `SWAMP_HILLS`, `BEACH`, `SNOWY_BEACH`, `STONE_SHORE` |
| `NETHER` | `NETHER_WASTES`, `SOUL_SAND_VALLEY`, `CRIMSON_FOREST`, `WARPED_FOREST`, `BASALT_DELTAS` |
| `END` | `THE_END`, `SMALL_END_ISLANDS`, `END_MIDLANDS`, `END_HIGHLANDS`, `END_BARRENS` |
| `OVERWORLD` | `ALL` without `NETHER` and `END` (67 on 1.16.5) |

## The anvil

If you've implemented a custom anvil recipe by putting your own item into the result
slot, players can't take it out — Bukkit doesn't recognize a result it didn't compute
itself, and refuses the click. `AnvilUtils` closes that gap by reimplementing the pickup
by hand, from an `InventoryClickEvent` listener:

```java
import me.wyne.wutils.common.anvil.AnvilUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventHandler;

@EventHandler
public void onClick(InventoryClickEvent event) {
    AnvilUtils.getResult(event); // no-op unless this is a blocked custom-result pickup
}
```

`getResult` is safe to call unconditionally — it checks `isClickValid` internally and
does nothing for any other click. When it does apply, it reproduces the whole vanilla
sequence: moves the result to the correct destination for the click type (hotbar swap,
offhand swap, drop, shift-click, plain pickup), fires a synthetic allowed
`InventoryClickEvent` so other plugins still observe a normal pickup, deducts the repair
cost from the player's level (skipped for creative/spectator), consumes the input slots,
and plays the anvil-use sound. It also reproduces vanilla's progressive anvil damage —
each pickup has a 12% chance to advance the anvil one stage toward breaking, through a
Paper `AnvilDamagedEvent` you can cancel to roll it back.

## Sounds, randomness and placeholders

Three small one- or two-method helpers.

**`SoundUtils.getSound(bukkitSound, ...)`** converts a legacy `org.bukkit.Sound` into an
Adventure `net.kyori.adventure.sound.Sound`, for APIs like Adventure's
`Audience.playSound` that expect the latter. Overloads let you set source, volume and
pitch; all default to `Sound.Source.MASTER`, volume `1f`, pitch `1f`.

**`RandomUtils.weightedRandom(map)`** picks a random entry from a `Map<K, ? extends
Number>` where each value is that entry's selection weight:

```java
import me.wyne.wutils.common.random.RandomUtils;

Map<String, Integer> loot = Map.of("common", 70, "rare", 25, "legendary", 5);
Map.Entry<String, Integer> picked = RandomUtils.weightedRandom(loot);
```

**Returns `null`** if the map is empty or every weight is zero/negative — always
null-check the result rather than assuming an entry comes back.

**`PAPIUtils.getPlaceholder(identifier, params)`** just joins the two arguments as
`identifier + "_" + params` — the body of a PlaceholderAPI placeholder, without the
surrounding `%...%`. It has no PlaceholderAPI dependency itself, but only makes sense
alongside actually registering a PAPI expansion, which needs PlaceholderAPI on your
classpath — see [WUtils Common](common.md#third-party-dependencies-you-must-supply).

## Strings

**`StringUtils.toRoman(number)`** writes an `int` as Roman numerals — `4` → `"IV"`,
`1994` → `"MCMXCIV"` — for effect and enchantment levels in item lore and messages:

```java
import me.wyne.wutils.common.strings.StringUtils;

String level = StringUtils.toRoman(3); // "III"
```

**Returns an empty string** for zero or negative numbers; above 3999 it keeps repeating
`M`. Kotlin code calls `3.toRoman()` from `wutils-common-kotlin`.

## Particles

If you're building a particle system driven by config strings, `me.wyne.wutils.common.particle`
is the conversion layer between a config value and the actual data object a Bukkit
particle needs — a `Material` for block-crack particles, `Particle.DustOptions` for
colored dust, and so on. It's the mechanism [WUtils Animation](../animation/animation.md)'s
particle runnables are built on; you'd normally only reach for it directly if you're
building your own particle-from-config feature.

Look up a parser by the data class you need, then parse:

```java
import me.wyne.wutils.common.particle.DataParserProvider;
import me.wyne.wutils.common.particle.StringDataParser;
import org.bukkit.Particle;

StringDataParser<?> parser = DataParserProvider.getDataParser(Particle.DustOptions.class);
Object data = parser.getData("#FF0000:2.0"); // color:size, colon- or space-separated
```

**Malformed input fails differently depending on which data type you're parsing** — this
is the sharp edge to know before you skip validating config input:

| Data type | On bad input |
|---|---|
| `Material` | returns `null` |
| `BlockData`, `ItemStack` | throws (`NullPointerException`/`IllegalArgumentException`) |
| `BlockFace`, `PotionType` | throws `IllegalArgumentException` |
| `Integer` | throws `NumberFormatException` |
| `Color` | accepts a named constant or hex (`#FF0000`); throws `NumberFormatException` on bad hex |
| `Particle.DustOptions` | accepts `<color>:<size>`; malformed parts throw, an *empty* string parses to black at size 1.0 |
| (no data / `Void`) | always returns `null` — this is the "correct" no-op case, not a failure |

Only the `Material` parser reports a bad value by returning `null` — everything else
either throws or (for `DustOptions`) tolerates an empty string as a default. If you're
validating config at load time, wrap the parse in a `try`/`catch` rather than
null-checking alone, except specifically for `Material`.

`DataParserProvider.getDataParser` matches on the *exact* data class only and returns
`null` for anything unregistered — check for `null` before calling `getData` on the
result.

## See also

- [Ranges, Durations and Values](values.md) — parsing the durations/ranges/comparators
  these helpers often work alongside.
- [Scheduling and Async Work](async.md) — running the async chunk lookups above off the
  main thread in the first place.
- [WUtils Animation](../animation/animation.md) — the main consumer of the particle
  parsers.
- contributor wiki pages with more depth:
  [Items](/contributing/common/items), [Inventories](/contributing/common/inventories),
  [Players](/contributing/common/players), [Blocks](/contributing/common/blocks),
  [Worlds and Biomes](/contributing/common/worlds),
  [Locations and Vectors](/contributing/common/locations), [Anvil](/contributing/common/anvil),
  [Sounds, Randomness and Placeholders](/contributing/common/sounds),
  [Particle Data Parsers](/contributing/common/particles).
