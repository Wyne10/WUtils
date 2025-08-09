package me.wyne.wutils.common.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class IUtils {

    public static boolean addItem(Inventory inventory, ItemStack... items) {
        return inventory.addItem(items).isEmpty();
    }

    public static boolean addItem(Player player, ItemStack... items) {
        return addItem(player.getInventory(), items);
    }

    public static void addOrDrop(Player player, ItemStack... items) {
        var exceed = player.getInventory().addItem(items);
        exceed.values().forEach(item -> player.getLocation().getWorld().dropItem(player.getLocation(), item));
    }

}
