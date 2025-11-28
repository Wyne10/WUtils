package me.wyne.wutils.common.block;

import me.wyne.wutils.common.item.ItemUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class BlockUtils {

    private static final ItemStack EMPTY_TOOL = new ItemStack(Material.AIR);

    public static void breakActuallyNaturally(Block block, @Nullable ItemStack tool, Player player) {
        if (tool == null)
            tool = EMPTY_TOOL;
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
            ItemUtils.damageNaturally(tool, player);
        if (naturalBreakEvent.getExpToDrop() > 0) {
            var exp = (ExperienceOrb) block.getWorld().spawnEntity(block.getLocation().toCenterLocation(), EntityType.EXPERIENCE_ORB);
            exp.setExperience(naturalBreakEvent.getExpToDrop());
        }
        if (naturalBreakEvent.isDropItems() && player.getGameMode() != GameMode.CREATIVE) {
            ItemUtils.dropActuallyNaturally(drops, naturalBreakEvent);
        }
    }

    public static void setExpDrop(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.ADVENTURE) return;
        if (!NaturalBlockBreakEvent.EXP_DROPS.containsKey(event.getBlock().getType())) return;
        event.setExpToDrop(NaturalBlockBreakEvent.EXP_DROPS.get(event.getBlock().getType()).getRandom());
    }

    public static float getYaw(BlockFace face) {
        return switch (face) {
            case SOUTH -> 0.0f;
            case WEST -> 90.0f;
            case NORTH -> 180.0f;
            case EAST -> 270.0f;
            default -> 0.0f;
        };
    }

}
