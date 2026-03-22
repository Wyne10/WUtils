package me.wyne.wutils.common.item;

import com.destroystokyo.paper.MaterialTags;
import org.bukkit.GameMode;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ItemUtils {

    public static final Set<Material> AXES = Collections.unmodifiableSet(MaterialTags.AXES.getValues());
    public static final Set<Material> PICKAXES = Collections.unmodifiableSet(MaterialTags.PICKAXES.getValues());
    public static final Set<Material> SHOVELS = Collections.unmodifiableSet(MaterialTags.SHOVELS.getValues());
    public static final Set<Material> HOES = Collections.unmodifiableSet(MaterialTags.HOES.getValues());
    public static final Set<Material> SWORDS = Collections.unmodifiableSet(MaterialTags.SWORDS.getValues());
    public static final Set<Material> TOOLS = Stream
            .of(AXES, PICKAXES, SHOVELS, HOES, SWORDS)
            .flatMap(Set::stream)
            .collect(Collectors.toUnmodifiableSet());

    public static final Set<Material> ARMOR = Stream
            .of(MaterialTags.HELMETS.getValues(), MaterialTags.CHESTPLATES.getValues(),
                    MaterialTags.LEGGINGS.getValues(), MaterialTags.BOOTS.getValues())
            .flatMap(Set::stream)
            .collect(Collectors.toUnmodifiableSet());

    public static boolean isNullOrAir(@Nullable ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    public static boolean isNotNullOrAir(@Nullable ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }

    public static void damageNaturally(@Nullable ItemStack item, Player player) {
        damageNaturally(item, player, 1);
    }

    public static void damageNaturally(@Nullable ItemStack item, Player player, int damage) {
        if (item == null) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (item.getType().getMaxDurability() <= 0) return;
        if (item.getItemMeta().isUnbreakable()) return;
        if (item.getItemMeta().hasEnchant(Enchantment.DURABILITY)) {
            if (ThreadLocalRandom.current().nextDouble() >= (1.0 / item.getItemMeta().getEnchantLevel(Enchantment.DURABILITY) + 1))
                return;
        }
        var damageable = (Damageable) item.getItemMeta();
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

    public static void dropActuallyNaturally(Collection<ItemStack> drops, BlockBreakEvent event) {
        var originalItems = drops.stream().map(item -> event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item)).toList();
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

    public static ItemStack saveBlockState(ItemStack item, BlockState blockState) {
        if (!(item.getItemMeta() instanceof BlockStateMeta blockStateMeta)) return item;
        blockStateMeta.setBlockState(blockState);
        item.setItemMeta(blockStateMeta);
        return item;
    }

    public static ItemStack saveBlockState(ItemStack item, BlockState blockState, TileStateLoader loader) {
        saveBlockState(item, blockState);
        return loader.save(item, blockState);
    }

    public static ItemStack saveBlockStateExtended(ItemStack item, BlockState blockState) {
        var loader = TILE_STATE_LOADERS.get(blockState.getType());
        if (loader == null) return saveBlockState(item, blockState);
        return saveBlockState(item, blockState, loader);
    }

    public static BlockState loadBlockState(ItemStack item, BlockState blockState) {
        if (!(item.getItemMeta() instanceof BlockStateMeta)) return blockState;
        var loader = TILE_STATE_LOADERS.get(blockState.getType());
        if (loader == null) return blockState;
        return loadBlockState(item, blockState, loader);
    }

    public static BlockState loadBlockState(ItemStack item, BlockState blockState, TileStateLoader loader) {
        return loader.load(item, blockState);
    }

    public static final Map<Material, TileStateLoader> TILE_STATE_LOADERS = Map.of(
            Material.SPAWNER, new SpawnerLoader()
    );

}
