package me.wyne.wutils.i18n.language;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PlaceholderAPIWrapper {

    private final static boolean isPlaceholderApiPresent;

    static {
        boolean isPlaceholderApiPresent1;
        try {
            Class.forName("PlaceholderAPI");
            isPlaceholderApiPresent1 = true;
        } catch (ClassNotFoundException e) {
            isPlaceholderApiPresent1 = false;
        }
        isPlaceholderApiPresent = isPlaceholderApiPresent1;
    }

    public static String setPlaceholders(@Nullable Player player, String string)
    {
        if (!isPlaceholderApiPresent)
            return string;
        return PlaceholderAPI.setPlaceholders(player, string);
    }

    public static String setPlaceholders(@Nullable OfflinePlayer player, String string)
    {
        if (!isPlaceholderApiPresent)
            return string;
        return PlaceholderAPI.setPlaceholders(player, string);
    }

}
