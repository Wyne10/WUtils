package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Plays a sound at {@code location}, audible only to nearby players. Does nothing if
 * {@code location}'s world is not currently loaded.
 */
public record LocalSound(@NotNull Location location, @NotNull Sound sound, float volume, float pitch) implements AnimationRunnable {

    public LocalSound(@NotNull Location location, @NotNull Sound sound) {
        this(location, sound, 1.0f, 1.0f);
    }

    /**
     * Resolves the equivalent {@link Sound Bukkit sound} for an Adventure {@code sound} by key.
     *
     * @throws java.util.NoSuchElementException if no Bukkit sound shares its key
     */
    public LocalSound(@NotNull Location location, @NotNull net.kyori.adventure.sound.Sound sound) {
        this(
                location,
                Arrays.stream(org.bukkit.Sound.values())
                        .filter(bsound -> sound.name().value().equals(bsound.getKey().value()))
                        .findAny().orElseThrow(),
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
