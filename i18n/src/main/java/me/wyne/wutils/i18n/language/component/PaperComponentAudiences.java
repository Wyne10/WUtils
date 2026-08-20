package me.wyne.wutils.i18n.language.component;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * {@link ComponentAudiences} using Bukkit types directly as Adventure {@link Audience}s (a {@link Player}
 * and the console sender natively implement {@code Audience} on Paper). Requires a server that natively
 * implements Adventure — Paper or a Paper fork — with no additional dependency. This is the default
 * {@link ComponentAudiences} used by the {@code I18n} builders; use {@link BukkitComponentAudiences}
 * instead on servers without native Adventure support.
 */
public class PaperComponentAudiences implements ComponentAudiences {

    @Override
    public @NotNull Audience player(@NotNull Player player) {
        return player;
    }

    @Override
    public @NotNull Audience sender(@NotNull CommandSender sender) {
        return sender;
    }

    @Override
    public @NotNull Audience player(@NotNull UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        return player != null ? player : Audience.empty();
    }

    @Override
    public @NotNull Audience all() {
        return Bukkit.getServer();
    }

    @Override
    public @NotNull Audience filter(@NotNull Predicate<CommandSender> filter) {
        return Audience.audience(Bukkit.getOnlinePlayers().stream()
                .filter(filter)
                .toList());
    }

    @Override
    public @NotNull Audience console() {
        return Bukkit.getConsoleSender();
    }

    @Override
    public @NotNull Audience permission(@NotNull Key permission) {
        return permission(permission.namespace() + '.' + permission.value());
    }

    @Override
    public @NotNull Audience permission(@NotNull String permission) {
        return filter(sender -> sender.hasPermission(permission));
    }

    @Override
    public @NotNull Audience players() {
        return Audience.audience(Bukkit.getOnlinePlayers());
    }

    @Override
    public @NotNull Audience server(@NotNull String serverName) {
        return Bukkit.getServer();
    }

    @Override
    public @NotNull Audience world(@NotNull Key worldKey) {
        World world = Bukkit.getWorld(worldKey.value());
        return world != null ? world : Audience.empty();
    }

}
