package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.data.AnimationParticle;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Spawns {@code particle} at {@code location}. Does nothing if {@code location}'s world is
 * not currently loaded.
 */
public record WorldParticle(@NotNull Location location, @NotNull AnimationParticle particle) implements AnimationRunnable {

    @Override
    public void run() {
        particle.spawnParticle(location, false);
    }

}
