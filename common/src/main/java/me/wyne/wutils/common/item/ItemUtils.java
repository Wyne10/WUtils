package me.wyne.wutils.common.item;

import com.destroystokyo.paper.MaterialTags;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Item-related helpers: tool/armor material sets, null/air checks, natural tool damage and
 * natural item drops, and block-state (tile entity) persistence on items.
 */
public final class ItemUtils {

    public static final @NotNull Set<@NotNull Material> AXES = Collections.unmodifiableSet(MaterialTags.AXES.getValues());
    public static final @NotNull Set<@NotNull Material> PICKAXES = Collections.unmodifiableSet(MaterialTags.PICKAXES.getValues());
    public static final @NotNull Set<@NotNull Material> SHOVELS = Collections.unmodifiableSet(MaterialTags.SHOVELS.getValues());
    public static final @NotNull Set<@NotNull Material> HOES = Collections.unmodifiableSet(MaterialTags.HOES.getValues());
    public static final @NotNull Set<@NotNull Material> SWORDS = Collections.unmodifiableSet(MaterialTags.SWORDS.getValues());
    /** Union of {@link #AXES}, {@link #PICKAXES}, {@link #SHOVELS}, {@link #HOES} and {@link #SWORDS}. */
    public static final @NotNull Set<@NotNull Material> TOOLS = Stream
            .of(AXES, PICKAXES, SHOVELS, HOES, SWORDS)
            .flatMap(Set::stream)
            .collect(Collectors.toUnmodifiableSet());

    /** Union of all helmet, chestplate, leggings and boots materials. */
    public static final @NotNull Set<@NotNull Material> ARMOR = Stream
            .of(MaterialTags.HELMETS.getValues(), MaterialTags.CHESTPLATES.getValues(),
                    MaterialTags.LEGGINGS.getValues(), MaterialTags.BOOTS.getValues())
            .flatMap(Set::stream)
            .collect(Collectors.toUnmodifiableSet());

    /** @return {@code true} if {@code item} is {@code null} or {@link Material#AIR} */
    public static boolean isNullOrAir(@Nullable ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    /** @return {@code true} if {@code item} is neither {@code null} nor {@link Material#AIR} */
    public static boolean isNotNullOrAir(@Nullable ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }

    /**
     * Applies one point of natural durability damage to {@code item}, as if used by
     * {@code player}. No-op for {@code null} items.
     *
     * @see #damageNaturally(ItemStack, Player, int)
     */
    public static void damageNaturally(@Nullable ItemStack item, @NotNull Player player) {
        damageNaturally(item, player, 1);
    }

    /**
     * Applies {@code damage} points of natural durability damage to {@code item}, firing
     * {@link PlayerItemDamageEvent} and, if the item breaks, {@link PlayerItemBreakEvent} and a
     * break sound. Honors unbreakable items, {@link Enchantment#DURABILITY}'s chance to negate
     * damage, and {@link GameMode#CREATIVE} (no-op). No-op for {@code null} items or items
     * without durability.
     */
    public static void damageNaturally(@Nullable ItemStack item, @NotNull Player player, int damage) {
        if (item == null) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;
        var meta = item.getItemMeta();
        if (item.getType().getMaxDurability() <= 0) return;
        if (meta.isUnbreakable()) return;
        if (meta.hasEnchant(Enchantment.DURABILITY)) {
            if (ThreadLocalRandom.current().nextDouble() >= (1.0 / meta.getEnchantLevel(Enchantment.DURABILITY) + 1))
                return;
        }
        var damageable = (Damageable) meta;
        var damageEvent = new PlayerItemDamageEvent(player, item, damage);
        if (!damageEvent.callEvent()) return;
        damageable.setDamage(damageable.getDamage() + damageEvent.getDamage());
        if (item.getType().getMaxDurability() <= damageable.getDamage()) {
            item.setAmount(item.getAmount() - 1);
            new PlayerItemBreakEvent(player, item).callEvent();
            damageable.setDamage(0);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
        }
        item.setItemMeta((ItemMeta) damageable);
    }

    /** @see #dropActuallyNaturally(Collection, BlockBreakEvent, Location) */
    public static void dropActuallyNaturally(@NotNull BlockBreakEvent event, @Nullable ItemStack... drops) {
        dropActuallyNaturally(event, event.getBlock().getLocation(), drops);
    }

    /** @see #dropActuallyNaturally(Collection, BlockBreakEvent, Location) */
    public static void dropActuallyNaturally(@NotNull BlockBreakEvent event, @NotNull Location location, @Nullable ItemStack... drops) {
        dropActuallyNaturally(Arrays.asList(drops), event, location);
    }

    /** @see #dropActuallyNaturally(Collection, BlockBreakEvent, Location) */
    public static void dropActuallyNaturally(@NotNull Collection<@Nullable ItemStack> drops, @NotNull BlockBreakEvent event) {
        dropActuallyNaturally(drops, event, event.getBlock().getLocation());
    }

    /**
     * Drops {@code drops} naturally at {@code location} (null/air entries skipped), firing a
     * {@link BlockDropItemEvent} that listeners can cancel or edit; entities removed from or
     * added to the event's item list are respected, and a cancelled event removes every dropped
     * entity again.
     */
    public static void dropActuallyNaturally(@NotNull Collection<@Nullable ItemStack> drops, @NotNull BlockBreakEvent event, @NotNull Location location) {
        var originalItems = drops.stream()
                .filter(ItemUtils::isNotNullOrAir)
                .map(item -> event.getBlock().getWorld().dropItemNaturally(location, item)).toList();
        var items = new ArrayList<>(originalItems);
        var dropItemEvent = new BlockDropItemEvent(event.getBlock(), event.getBlock().getState(), event.getPlayer(), items);
        dropItemEvent.callEvent();
        if (dropItemEvent.isCancelled()) {
            items.forEach(Entity::remove);
            return;
        }
        if (!items.containsAll(originalItems)) {
            originalItems.stream()
                    .filter(item -> !items.contains(item))
                    .forEach(Entity::remove);
        }
    }

    /**
     * Stores {@code blockState} on {@code item}'s {@link BlockStateMeta}, e.g. so a placed block
     * item remembers a container's contents. No-op (returns {@code item} unchanged) if the item
     * type has no block-state meta.
     */
    public static @NotNull ItemStack saveBlockState(@NotNull ItemStack item, @NotNull BlockState blockState) {
        if (!(item.getItemMeta() instanceof BlockStateMeta blockStateMeta)) return item;
        blockStateMeta.setBlockState(blockState);
        item.setItemMeta(blockStateMeta);
        return item;
    }

    /**
     * Like {@link #saveBlockState(ItemStack, BlockState)}, and additionally runs {@code loader}
     * to persist type-specific extra state (e.g. a spawner's mob type) that
     * {@link BlockStateMeta} alone does not capture.
     */
    public static @NotNull ItemStack saveBlockState(@NotNull ItemStack item, @NotNull BlockState blockState, @NotNull TileStateLoader loader) {
        saveBlockState(item, blockState);
        return loader.save(item, blockState);
    }

    /**
     * Saves {@code blockState} onto {@code item}, using the {@link TileStateLoader} registered in
     * {@link #TILE_STATE_LOADERS} for the block's type when one exists, otherwise falling back to
     * {@link #saveBlockState(ItemStack, BlockState)}.
     */
    public static @NotNull ItemStack saveBlockStateExtended(@NotNull ItemStack item, @NotNull BlockState blockState) {
        var loader = TILE_STATE_LOADERS.get(blockState.getType());
        if (loader == null) return saveBlockState(item, blockState);
        return saveBlockState(item, blockState, loader);
    }

    /**
     * Applies {@code item}'s saved {@link BlockStateMeta}, and any state restored by a registered
     * {@link #TILE_STATE_LOADERS} loader, onto {@code blockState}. Returns {@code blockState}
     * unchanged if the item has no block-state meta or no loader is registered for its type.
     */
    public static @NotNull BlockState loadBlockState(@NotNull ItemStack item, @NotNull BlockState blockState) {
        if (!(item.getItemMeta() instanceof BlockStateMeta)) return blockState;
        var loader = TILE_STATE_LOADERS.get(blockState.getType());
        if (loader == null) return blockState;
        return loadBlockState(item, blockState, loader);
    }

    /** @see #loadBlockState(ItemStack, BlockState) */
    public static @NotNull BlockState loadBlockState(@NotNull ItemStack item, @NotNull BlockState blockState, @NotNull TileStateLoader loader) {
        return loader.load(item, blockState);
    }

    /** Type-specific {@link TileStateLoader}s used by {@link #saveBlockStateExtended} and {@link #loadBlockState(ItemStack, BlockState)}. */
    public static final @NotNull Map<@NotNull Material, @NotNull TileStateLoader> TILE_STATE_LOADERS = Map.of(
            Material.SPAWNER, new SpawnerLoader()
    );

}
