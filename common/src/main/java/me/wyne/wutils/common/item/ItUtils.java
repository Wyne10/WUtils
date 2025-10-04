package me.wyne.wutils.common.item;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.ThreadLocalRandom;

public final class ItUtils {

    public static void damageNaturally(ItemStack item, Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (!(item.getItemMeta() instanceof Damageable damageable)) return;
        if (item.getItemMeta().isUnbreakable()) return;
        if (item.getItemMeta().hasEnchant(Enchantment.DURABILITY)) {
            if (ThreadLocalRandom.current().nextDouble() >= (1.0 / item.getItemMeta().getEnchantLevel(Enchantment.DURABILITY) + 1))
                return;
        }
        damageable.setDamage(damageable.getDamage() + 1);
        if (item.getType().getMaxDurability() <= damageable.getDamage()) {
            item.setAmount(item.getAmount() - 1);
            new PlayerItemBreakEvent(player, item).callEvent();
            damageable.setDamage(0);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
        }
        item.setItemMeta((ItemMeta) damageable);
    }

    public static ItemStack saveBlockState(ItemStack item, BlockState blockState) {
        if (!(item.getItemMeta() instanceof BlockStateMeta blockStateMeta)) return item;
        blockStateMeta.setBlockState(blockState);
        item.setItemMeta(blockStateMeta);
        return item;
    }

}
