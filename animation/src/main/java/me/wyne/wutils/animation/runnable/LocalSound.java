package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;

public record LocalSound(Location location, Sound sound, float volume, float pitch) implements AnimationRunnable {

    public LocalSound(Location location, Sound sound) {
        this(location, sound, 1.0f, 1.0f);
    }

    @Override
    public void run() {
        World world = location.getWorld();
        if (world == null) return;

        world.playSound(location, sound, volume, pitch);
    }

}
