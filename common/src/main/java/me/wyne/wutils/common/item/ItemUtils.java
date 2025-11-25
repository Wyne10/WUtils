package me.wyne.wutils.common.item;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class ItemUtils {

    public static boolean isNullOrAir(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    public static boolean isNotNullOrAir(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }

    public static void damageNaturally(ItemStack item, Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (item.getType().getMaxDurability() <= 0) return;
        if (item.getItemMeta().isUnbreakable()) return;
        if (item.getItemMeta().hasEnchant(Enchantment.DURABILITY)) {
            if (ThreadLocalRandom.current().nextDouble() >= (1.0 / item.getItemMeta().getEnchantLevel(Enchantment.DURABILITY) + 1))
                return;
        }
        var damageable = (Damageable) item.getItemMeta();
        damageable.setDamage(damageable.getDamage() + 1);
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
