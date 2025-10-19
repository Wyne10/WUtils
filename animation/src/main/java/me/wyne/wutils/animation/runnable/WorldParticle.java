package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.data.AnimationParticle;
import org.bukkit.Location;

public record WorldParticle(Location location, AnimationParticle particle) implements AnimationRunnable {

    @Override
    public void run() {
        particle.spawnParticle(location, false);
    }

}
