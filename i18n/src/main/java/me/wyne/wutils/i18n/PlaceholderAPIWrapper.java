package me.wyne.wutils.i18n;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PlaceholderAPIWrapper {

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

    public static String setPlaceholders(@Nullable Player player, String string)
    {
        if (!IS_PLACEHOLDER_API_PRESENT)
            return string;
        return PlaceholderAPI.setPlaceholders(player, string);
    }

    public static String setPlaceholders(@Nullable OfflinePlayer player, String string)
    {
        if (!IS_PLACEHOLDER_API_PRESENT)
            return string;
        return PlaceholderAPI.setPlaceholders(player, string);
    }

}
