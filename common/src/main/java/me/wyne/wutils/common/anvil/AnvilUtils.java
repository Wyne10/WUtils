package me.wyne.wutils.common.anvil;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static me.wyne.wutils.common.item.ItUtils.isNotNullOrAir;
import static me.wyne.wutils.common.item.ItUtils.isNullOrAir;

public final class AnvilUtils {

    public final static double ANVIL_DAMAGE_CHANCE = 0.12;
    public final static Set<GameMode> ANVIL_DAMAGE_IMMUNITY = Set.of(GameMode.CREATIVE, GameMode.SPECTATOR);
    public final static Set<Material> ANVIL_TYPES = Set.of(Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL);
    public final static List<Material> ANVIL_DAMAGE_QUEUE = List.of(Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL, Material.AIR);

    public static boolean isClickValid(InventoryClickEvent e) {
        if (e.getInventory().getType() != InventoryType.ANVIL) return false;
        var anvil = (AnvilInventory) e.getInventory();
        if (e.getSlotType() != InventoryType.SlotType.RESULT) return false;
        if (e.getAction() != InventoryAction.NOTHING) return false;
        if (isNullOrAir(anvil.getResult())) return false;
        if (!isNullOrAir(e.getCursor()) && !e.isShiftClick()) return false;
        var player = (Player) e.getWhoClicked();
        if (anvil.getRepairCost() > player.getLevel()) return false;
        return true;
    }

    public static void getResult(InventoryClickEvent e) {
        if (!isClickValid(e)) return;
        var anvil = (AnvilInventory) e.getInventory();
        var player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        if (e.isShiftClick()) {
            if (player.getInventory().firstEmpty() == -1) return;
            player.getInventory().addItem(anvil.getResult());
        } else {
            player.setItemOnCursor(anvil.getResult());
        }
        anvil.getResult().setAmount(0);
        player.setLevel(player.getLevel() - anvil.getRepairCost());
        if (isNotNullOrAir(anvil.getFirstItem()) && isNotNullOrAir(anvil.getSecondItem())) {
            var amount = Math.min(anvil.getFirstItem().getAmount(), anvil.getSecondItem().getAmount());
            anvil.getFirstItem().setAmount(anvil.getFirstItem().getAmount() - amount);
            anvil.getSecondItem().setAmount(anvil.getSecondItem().getAmount() - amount);
        }

        player.getLocation().getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1f);
        if (ANVIL_DAMAGE_IMMUNITY.contains(player.getGameMode())) return;
        if (ThreadLocalRandom.current().nextDouble() >= ANVIL_DAMAGE_CHANCE) return;
        if (anvil.getLocation() == null) return;
        var block = anvil.getLocation().getBlock();
        if (!ANVIL_TYPES.contains(block.getType())) return;
        var currentDamageIndex = ANVIL_DAMAGE_QUEUE.indexOf(block.getType());
        var nextDamageIndex = currentDamageIndex + 1;
        block.setType(ANVIL_DAMAGE_QUEUE.get(nextDamageIndex));
        if (block.getType() == Material.AIR)
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1f, 1f);
    }

}
