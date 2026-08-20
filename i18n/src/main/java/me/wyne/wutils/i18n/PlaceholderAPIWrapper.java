package me.wyne.wutils.i18n;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Applies PlaceholderAPI placeholders when the optional {@code compileOnly} PlaceholderAPI dependency is
 * present on the runtime classpath, and passes the string through unchanged otherwise.
 */
public final class PlaceholderAPIWrapper {

    /** Whether PlaceholderAPI was detected on the classpath at class-load time. */
    public final static boolean IS_PLACEHOLDER_API_PRESENT;

    static {
        boolean isPlaceholderApiPresent;
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            isPlaceholderApiPresent = true;
        } catch (ClassNotFoundException e) {
            isPlaceholderApiPresent = false;
        }
        IS_PLACEHOLDER_API_PRESENT = isPlaceholderApiPresent;
    }

    public static @NotNull String setPlaceholders(@Nullable Player player, @NotNull String string) {
        if (!IS_PLACEHOLDER_API_PRESENT)
            return string;
        return PlaceholderAPI.setPlaceholders(player, string);
    }

    public static @NotNull String setPlaceholders(@Nullable OfflinePlayer player, @NotNull String string) {
        if (!IS_PLACEHOLDER_API_PRESENT)
            return string;
        return PlaceholderAPI.setPlaceholders(player, string);
    }

}
