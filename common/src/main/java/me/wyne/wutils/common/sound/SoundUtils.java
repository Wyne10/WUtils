package me.wyne.wutils.common.sound;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

public final class SoundUtils {

    public static Sound getSound(@NotNull org.bukkit.Sound sound) {
        return Sound.sound(Key.key(sound.getKey().toString()), Sound.Source.MASTER, 1f, 1f);
    }

    public static Sound getSound(@NotNull org.bukkit.Sound sound, @NotNull Sound.Source source) {
        return Sound.sound(Key.key(sound.getKey().toString()), source, 1f, 1f);
    }

    public static Sound getSound(@NotNull org.bukkit.Sound sound, float volume) {
        return Sound.sound(Key.key(sound.getKey().toString()), Sound.Source.MASTER, volume, 1f);
    }

    public static Sound getSound(@NotNull org.bukkit.Sound sound, float volume, float pitch) {
        return Sound.sound(Key.key(sound.getKey().toString()), Sound.Source.MASTER, volume, pitch);
    }

    public static Sound getSound(@NotNull org.bukkit.Sound sound, @NotNull Sound.Source source, float volume, float pitch) {
        return Sound.sound(Key.key(sound.getKey().toString()), source, volume, pitch);
    }

}
