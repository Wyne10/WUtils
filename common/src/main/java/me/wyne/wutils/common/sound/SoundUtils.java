package me.wyne.wutils.common.sound;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

/** Converts legacy {@link org.bukkit.Sound} enum constants to Adventure {@link Sound}s. */
public final class SoundUtils {

    /** @see #getSound(org.bukkit.Sound, Sound.Source, float, float) */
    public static @NotNull Sound getSound(@NotNull org.bukkit.Sound sound) {
        return Sound.sound(Key.key(sound.getKey().toString()), Sound.Source.MASTER, 1f, 1f);
    }

    /** @see #getSound(org.bukkit.Sound, Sound.Source, float, float) */
    public static @NotNull Sound getSound(@NotNull org.bukkit.Sound sound, @NotNull Sound.Source source) {
        return Sound.sound(Key.key(sound.getKey().toString()), source, 1f, 1f);
    }

    /** @see #getSound(org.bukkit.Sound, Sound.Source, float, float) */
    public static @NotNull Sound getSound(@NotNull org.bukkit.Sound sound, float volume) {
        return Sound.sound(Key.key(sound.getKey().toString()), Sound.Source.MASTER, volume, 1f);
    }

    /** @see #getSound(org.bukkit.Sound, Sound.Source, float, float) */
    public static @NotNull Sound getSound(@NotNull org.bukkit.Sound sound, float volume, float pitch) {
        return Sound.sound(Key.key(sound.getKey().toString()), Sound.Source.MASTER, volume, pitch);
    }

    /** Builds an Adventure {@link Sound} from a legacy {@code sound} key, {@code source}, {@code volume} and {@code pitch}. */
    public static @NotNull Sound getSound(@NotNull org.bukkit.Sound sound, @NotNull Sound.Source source, float volume, float pitch) {
        return Sound.sound(Key.key(sound.getKey().toString()), source, volume, pitch);
    }

}
