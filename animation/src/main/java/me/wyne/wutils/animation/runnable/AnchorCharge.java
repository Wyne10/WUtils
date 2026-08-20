package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.jetbrains.annotations.NotNull;

/**
 * Adds (or removes, for a negative {@code amount}) charges to the respawn anchor at
 * {@code location}, clamped to its valid charge range. Does nothing if the block there is not
 * a respawn anchor.
 */
public record AnchorCharge(@NotNull Location location, int amount) implements AnimationRunnable {

    @Override
    public void run() {
        Block block = location.getBlock();
        if (!(block.getBlockData() instanceof RespawnAnchor anchor)) return;
        int cappedAmount = anchor.getCharges() + amount;
        if (cappedAmount > anchor.getMaximumCharges()) cappedAmount = anchor.getMaximumCharges();
        if (cappedAmount < 0) cappedAmount = 0;
        anchor.setCharges(cappedAmount);
        block.setBlockData(anchor);
    }

}
