package me.wyne.wutils.common.command;

import dev.jorel.commandapi.arguments.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;

public final class CUtils {

    public static Argument<Player> onlinePlayer(String nodeName) {
        return new EntitySelectorArgument.OnePlayer(nodeName)
                .replaceSuggestions(ArgumentSuggestions.stringCollection(info ->
                        Bukkit.getOnlinePlayers().stream().map(Player::getName).toList()));
    }

    public static Argument<String> offlinePlayer(String nodeName) {
        return new StringArgument(nodeName)
                .replaceSuggestions(ArgumentSuggestions.stringCollection(info ->
                        Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList()));
    }

}
