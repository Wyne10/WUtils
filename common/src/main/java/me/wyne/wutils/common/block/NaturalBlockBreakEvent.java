package me.wyne.wutils.common.block;

import me.wyne.wutils.common.range.ClosedIntRange;
import me.wyne.wutils.common.range.Range;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class NaturalBlockBreakEvent extends BlockBreakEvent {

    public static final Map<Material, Range<Integer>> EXP_DROPS = Map.of(
            Material.NETHER_GOLD_ORE, new ClosedIntRange(0, 1),
            Material.COAL_ORE, new ClosedIntRange(0, 2),
            Material.REDSTONE_ORE, new ClosedIntRange(1, 5),
            Material.LAPIS_ORE, new ClosedIntRange(2, 5),
            Material.NETHER_QUARTZ_ORE, new ClosedIntRange(2, 5),
            Material.DIAMOND_ORE, new ClosedIntRange(3, 7),
            Material.EMERALD_ORE, new ClosedIntRange(3, 7),
            Material.SPAWNER, new ClosedIntRange(15, 43)
    );

    private boolean damageTool = true;

    public NaturalBlockBreakEvent(@NotNull Block theBlock, @NotNull Player player) {
        super(theBlock, player);
    }

    public boolean damageTool() {
        return damageTool;
    }

    public void setDamageTool(boolean damageTool) {
        this.damageTool = damageTool;
    }

}
