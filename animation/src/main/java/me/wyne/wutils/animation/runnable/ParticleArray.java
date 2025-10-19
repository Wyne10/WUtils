package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.data.AnimationParticle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Set;

public record ParticleArray(World world, Set<Vector> points, AnimationParticle particle) implements AnimationRunnable {

    @Override
    public void run() {
        for (Vector point : points) {
            particle.spawnParticle(world, point, false);
        }
    }

}
