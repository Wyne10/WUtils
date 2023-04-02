package me.wyne.wutils.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TabComplete {

    @NotNull
    public static List<String> collectionTabComplete(@NotNull final Collection<String> collection, @NotNull final String arg)
    {
        List<String> result = new ArrayList<>();

        for (String element : collection)
        {
            if (arg.isBlank())
                result.add(element);
            else
            {
                if (element.toLowerCase().startsWith(arg.toLowerCase()))
                    result.add(element);
            }
        }

        return result;
    }

    @NotNull
    public static List<String> offlinePlayersTabComplete(@NotNull final String arg)
    {
        List<String> result = new ArrayList<>();

        for (OfflinePlayer player : Bukkit.getOfflinePlayers())
        {
            if (player.getName() == null)
                continue;

            if (arg.isBlank())
                result.add(player.getName());
            else
            {
                if (player.getName().toLowerCase().startsWith(arg.toLowerCase()))
                    result.add(player.getName());
            }
        }

        return result;
    }

    @NotNull
    public static List<String> onlinePlayersTabComplete(@NotNull final String arg)
    {
        List<String> result = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (arg.isBlank())
                result.add(player.getName());
            else
            {
                if (player.getName().toLowerCase().startsWith(arg.toLowerCase()))
                    result.add(player.getName());
            }
        }

        return result;
    }

}
