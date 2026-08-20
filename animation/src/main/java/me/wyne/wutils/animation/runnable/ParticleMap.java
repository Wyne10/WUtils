package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.data.AnimationParticle;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Spawns a distinct {@link AnimationParticle} at each point in {@code particles} within
 * {@code world}, unlike {@link ParticleArray} which spawns one shared particle at many points.
 */
public record ParticleMap(@NotNull World world, @NotNull Map<@NotNull Vector, @NotNull AnimationParticle> particles) implements AnimationRunnable {

    @Override
    public void run() {
        for (Vector point : particles.keySet()) {
            particles.get(point).spawnParticle(world, point, false);
        }
    }

}
