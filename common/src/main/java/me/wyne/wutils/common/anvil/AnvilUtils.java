package me.wyne.wutils.common.anvil;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static me.wyne.wutils.common.item.ItemUtils.isNotNullOrAir;
import static me.wyne.wutils.common.item.ItemUtils.isNullOrAir;

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
        var player = (Player) e.getWhoClicked();
        if (!ANVIL_DAMAGE_IMMUNITY.contains(player.getGameMode()) && anvil.getRepairCost() > player.getLevel()) return false;
        return true;
    }

    public static void getResult(InventoryClickEvent e) {
        if (!isClickValid(e)) return;
        var anvil = (AnvilInventory) e.getInventory();
        var player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        var result = anvil.getResult();
        var pickupAction = switch (e.getClick()) {
            case NUMBER_KEY, SWAP_OFFHAND -> InventoryAction.HOTBAR_SWAP;
            case DROP -> InventoryAction.DROP_ONE_SLOT;
            case CONTROL_DROP -> InventoryAction.DROP_ALL_SLOT;
            default -> e.isShiftClick() ? InventoryAction.MOVE_TO_OTHER_INVENTORY : InventoryAction.PICKUP_ALL;
        };
        switch (e.getClick()) {
            case NUMBER_KEY -> {
                if (e.getHotbarButton() < 0) return;
                if (isNotNullOrAir(player.getInventory().getItem(e.getHotbarButton()))) return;
            }
            case SWAP_OFFHAND -> {
                if (isNotNullOrAir(player.getInventory().getItemInOffHand())) return;
            }
            default -> {
                if (e.isShiftClick()) {
                    if (player.getInventory().firstEmpty() == -1) return;
                } else if (isNotNullOrAir(e.getCursor())) {
                    return;
                }
            }
        }
        var pickup = new InventoryClickEvent(e.getView(), InventoryType.SlotType.RESULT, e.getRawSlot(), e.getClick(), pickupAction, e.getHotbarButton());
        pickup.setResult(Event.Result.ALLOW);
        if (!pickup.callEvent())
            return;
        switch (e.getClick()) {
            case NUMBER_KEY -> player.getInventory().setItem(e.getHotbarButton(), result);
            case SWAP_OFFHAND -> player.getInventory().setItemInOffHand(result);
            case DROP, CONTROL_DROP -> {
                var dropped = player.getWorld().dropItem(player.getEyeLocation(), result);
                dropped.setVelocity(player.getEyeLocation().getDirection().multiply(0.3));
                dropped.setPickupDelay(40);
                dropped.setThrower(player.getUniqueId());
            }
            default -> {
                if (e.isShiftClick())
                    player.getInventory().addItem(result);
                else
                    player.setItemOnCursor(result);
            }
        }
        result.setAmount(0);
        if (!ANVIL_DAMAGE_IMMUNITY.contains(player.getGameMode()))
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
        var damageEvent = new AnvilDamagedEvent(e.getView(), block.getBlockData());
        if (!damageEvent.callEvent()) {
            block.setType(ANVIL_DAMAGE_QUEUE.get(currentDamageIndex));
            return;
        }
        block.setType(damageEvent.getDamageState().getMaterial());
        if (block.getType() == Material.AIR)
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1f, 1f);
    }

}
