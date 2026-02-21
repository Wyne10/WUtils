package me.wyne.wutils.common.command;

import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

public final class CommandUtils {

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

    @Nullable
    public static OfflinePlayer getOfflinePlayer(CommandArguments args, String nodeName) {
        var playerName = args.getOptionalByClass(nodeName, String.class);
        if (playerName.isEmpty()) return null;
        var playerUuid = Bukkit.getPlayerUniqueId(playerName.get());
        if (playerUuid == null) return null;
        var player = Bukkit.getOfflinePlayer(playerUuid);
        if (!player.hasPlayedBefore()) return null;
        return player;
    }

}
