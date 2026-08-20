package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.data.AnimationParticle;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Spawns the same {@code particle} at every point in {@code points} within {@code world}.
 */
public record ParticleArray(@NotNull World world, @NotNull Set<@NotNull Vector> points, @NotNull AnimationParticle particle) implements AnimationRunnable {

    @Override
    public void run() {
        for (Vector point : points) {
            particle.spawnParticle(world, point, false);
        }
    }

}
