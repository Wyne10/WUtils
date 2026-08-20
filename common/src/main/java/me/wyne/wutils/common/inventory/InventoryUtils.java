package me.wyne.wutils.common.inventory;

import me.wyne.wutils.common.item.ItemUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Helpers for adding, dropping and inspecting {@link ItemStack}s against inventories, players,
 * and locations. Item-mutating operations run on the calling thread and must be called from the
 * main server thread, as with any other Bukkit inventory API.
 */
public final class InventoryUtils {

    /** @return {@code true} if every item fit; leftovers are not tracked, use {@link Inventory#addItem} directly if needed */
    public static boolean addItem(@NotNull Inventory inventory, @NotNull ItemStack... items) {
        return inventory.addItem(items).isEmpty();
    }

    /** @see #addItem(Inventory, ItemStack...) */
    public static boolean addItem(@NotNull Inventory inventory, @NotNull Collection<@NotNull ItemStack> items) {
        return inventory.addItem(items.toArray(ItemStack[]::new)).isEmpty();
    }

    /** @see #addItem(Inventory, ItemStack...) */
    public static boolean addItem(@NotNull Player player, @NotNull ItemStack... items) {
        return addItem(player.getInventory(), items);
    }

    /** @see #addItem(Inventory, ItemStack...) */
    public static boolean addItem(@NotNull Player player, @NotNull Collection<@NotNull ItemStack> items) {
        return addItem(player.getInventory(), items);
    }

    /** Adds {@code items} to {@code player}'s inventory, dropping at their feet whatever does not fit. */
    public static void addOrDrop(@NotNull Player player, @NotNull ItemStack... items) {
        addOrDrop(player, false, items);
    }

    /** @see #addOrDrop(Player, ItemStack...) */
    public static void addOrDrop(@NotNull Player player, @NotNull Collection<@NotNull ItemStack> items) {
        addOrDrop(player, false, items);
    }

    /**
     * Adds {@code items} to {@code player}'s inventory, dropping at their feet whatever does not
     * fit, marking dropped items as owned by {@code player} (subject to pickup delay) when
     * {@code setOwner} is {@code true}.
     */
    public static void addOrDrop(@NotNull Player player, boolean setOwner, @NotNull ItemStack... items) {
        addOrDrop(player, setOwner, Arrays.asList(items));
    }

    /** @see #addOrDrop(Player, boolean, ItemStack...) */
    public static void addOrDrop(@NotNull Player player, boolean setOwner, @NotNull Collection<@NotNull ItemStack> items) {
        var exceed = player.getInventory().addItem(items.toArray(ItemStack[]::new));
        drop(player, setOwner, exceed.values());
    }

    /**
     * Drops {@code items} at {@code player}'s location with no pickup delay. Null and air
     * elements are silently skipped, so callers may pass sparse arrays through unfiltered.
     */
    public static void drop(@NotNull Player player, @Nullable ItemStack... items) {
        drop(player, false, items);
    }

    /** @see #drop(Player, ItemStack...) */
    public static void drop(@NotNull Player player, @NotNull Collection<@Nullable ItemStack> items) {
        drop(player, false, items);
    }

    /**
     * Drops {@code items} at {@code player}'s location with no pickup delay, marking the drops as
     * owned by {@code player} when {@code setOwner} is {@code true}. Null and air elements are
     * silently skipped.
     */
    public static void drop(@NotNull Player player, boolean setOwner, @Nullable ItemStack... items) {
        drop(player, setOwner, Arrays.asList(items));
    }

    /** @see #drop(Player, boolean, ItemStack...) */
    public static void drop(@NotNull Player player, boolean setOwner, @NotNull Collection<@Nullable ItemStack> items) {
        items.stream()
                .filter(ItemUtils::isNotNullOrAir)
                .forEach(item -> {
                    var drop = player.getLocation().getWorld().dropItem(player.getLocation(), item);
                    if (setOwner)
                        drop.setOwner(player.getUniqueId());
                    drop.setPickupDelay(0);
                });
    }

    /**
     * Drops {@code items} at {@code location} with no pickup delay and no owner. Null and air
     * elements are silently skipped.
     */
    public static void drop(@NotNull Location location, @NotNull Collection<@Nullable ItemStack> items) {
        items.stream()
                .filter(ItemUtils::isNotNullOrAir)
                .forEach(item -> {
                    var drop = location.getWorld().dropItem(location, item);
                    drop.setPickupDelay(0);
                });
    }

    /**
     * Collects the item(s) an {@link InventoryClickEvent} would move: the clicked slot's current
     * item, plus the hotbar slot for a {@link ClickType#NUMBER_KEY} click or the off-hand item
     * for a {@link ClickType#SWAP_OFFHAND} click. Null/air items are omitted.
     */
    public static @NotNull List<@NotNull ItemStack> getAffectedItems(@NotNull InventoryClickEvent event) {
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
