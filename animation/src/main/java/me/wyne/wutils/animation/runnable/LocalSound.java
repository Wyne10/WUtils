package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;

import java.util.Arrays;

public record LocalSound(Location location, Sound sound, float volume, float pitch) implements AnimationRunnable {

    public LocalSound(Location location, Sound sound) {
        this(location, sound, 1.0f, 1.0f);
    }

    public LocalSound(Location location, net.kyori.adventure.sound.Sound sound) {
        this(
                location,
                Arrays.stream(org.bukkit.Sound.values())
                        .filter(bsound -> sound.name().value().equals(bsound.getKey().value()))
                        .findAny().orElse(null),
                sound.volume(),
                sound.pitch()
        );
    }

    @Override
    public void run() {
        World world = location.getWorld();
        if (world == null) return;

        world.playSound(location, sound, volume, pitch);
    }

}
