package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.data.AnimationParticle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Map;

public record ParticleMap(World world, Map<Vector, AnimationParticle> particles) implements AnimationRunnable {

    @Override
    public void run() {
        for (Vector point : particles.keySet()) {
            particles.get(point).spawnParticle(world, point, false);
        }
    }

}
