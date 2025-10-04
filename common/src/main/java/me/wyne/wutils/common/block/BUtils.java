package me.wyne.wutils.common.block;

import me.wyne.wutils.common.item.ItUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public final class BUtils {

    public static void breakActuallyNaturally(Block block, ItemStack tool, Player player) {
        var state = block.getState();
        var drops = block.getDrops(tool, player);
        var naturalBreakEvent = new NaturalBlockBreakEvent(block, player);
        naturalBreakEvent.setDropItems(!drops.isEmpty());
        if (!tool.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH) &&
                (block.getType() == Material.SPAWNER || !drops.isEmpty()))
            setExpDrop(naturalBreakEvent);
        naturalBreakEvent.callEvent();
        if (naturalBreakEvent.isCancelled()) return;
        block.setType(Material.AIR);
        if (naturalBreakEvent.damageTool())
            ItUtils.damageNaturally(tool, player);
        if (naturalBreakEvent.getExpToDrop() > 0) {
            var exp = (ExperienceOrb) block.getWorld().spawnEntity(block.getLocation().toCenterLocation(), EntityType.EXPERIENCE_ORB);
            exp.setExperience(naturalBreakEvent.getExpToDrop());
        }
        if (naturalBreakEvent.isDropItems() && player.getGameMode() != GameMode.CREATIVE) {
            var originalItems = drops.stream().map(item -> block.getWorld().dropItemNaturally(block.getLocation(), item)).toList();
            var items = new ArrayList<>(originalItems);
            var dropItemEvent = new BlockDropItemEvent(block, state, player, items);
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
    }

    public static void setExpDrop(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.ADVENTURE) return;
        if (!NaturalBlockBreakEvent.EXP_DROPS.containsKey(event.getBlock().getType())) return;
        event.setExpToDrop(NaturalBlockBreakEvent.EXP_DROPS.get(event.getBlock().getType()).getRandom());
    }

}
