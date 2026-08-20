package me.wyne.wutils.common.command;

import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * CommandAPI player arguments with target selectors stripped out.
 *
 * <p>CommandAPI's player arguments are built on entity-selector syntax, so their tab completion
 * offers {@code @a}, {@code @s}, {@code @p}, {@code @r} and {@code @e} alongside real player
 * names. Both helpers here replace the suggestions with plain player names;
 * {@link #offlinePlayer(String)} additionally drops the entity-selector argument type entirely
 * in favour of a plain string.</p>
 *
 * <p>Requires the optional CommandAPI {@code compileOnly} dependency.</p>
 */
public final class CommandUtils {

    /** A CommandAPI argument that resolves to an online {@link Player}, suggesting online player names. */
    public static @NotNull Argument<@NotNull Player> onlinePlayer(@NotNull String nodeName) {
        return new EntitySelectorArgument.OnePlayer(nodeName)
                .replaceSuggestions(ArgumentSuggestions.stringCollection(info ->
                        Bukkit.getOnlinePlayers().stream().map(Player::getName).toList()));
    }

    /**
     * A CommandAPI string argument suggesting known offline player names. Unlike
     * {@link #onlinePlayer(String)}, this does not validate the input against an actual player
     * server-side — resolve it with {@link #getOfflinePlayer(CommandArguments, String)}.
     */
    public static @NotNull Argument<@NotNull String> offlinePlayer(@NotNull String nodeName) {
        return new StringArgument(nodeName)
                .replaceSuggestions(ArgumentSuggestions.stringCollection(info ->
                        Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList()));
    }

    /**
     * Resolves the {@link #offlinePlayer(String)} argument named {@code nodeName} to an
     * {@link OfflinePlayer}.
     *
     * @return {@code null} when the argument is absent, the name does not resolve to a known
     *         UUID, or the player has never played on this server
     */
    public static @Nullable OfflinePlayer getOfflinePlayer(@NotNull CommandArguments args, @NotNull String nodeName) {
        var playerName = args.getOptionalByClass(nodeName, String.class);
        if (playerName.isEmpty()) return null;
        var playerUuid = Bukkit.getPlayerUniqueId(playerName.get());
        if (playerUuid == null) return null;
        var player = Bukkit.getOfflinePlayer(playerUuid);
        if (player.isOnline()) return player;
        if (!player.hasPlayedBefore()) return null;
        return player;
    }

}
