package me.wyne.wutils.common.inventory;

import me.wyne.wutils.common.item.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class InventoryUtils {

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

    public static List<ItemStack> getAffectedItems(InventoryClickEvent event) {
        var result = new ArrayList<ItemStack>();
        addNonEmpty(result, event.getCurrentItem());
        if (event.getClick() == ClickType.NUMBER_KEY) {
            addNonEmpty(result, event.getWhoClicked().getInventory().getItem(event.getHotbarButton()));
        } else if (event.getClick() == ClickType.SWAP_OFFHAND) {
            addNonEmpty(result, event.getWhoClicked().getInventory().getItemInOffHand());
        }
        return List.copyOf(result);
    }

    private static void addNonEmpty(List<ItemStack> list, ItemStack item) {
        if (ItemUtils.isNotNullOrAir(item))
            list.add(item);
    }


}
