package me.wyne.wutils.i18n.language.component;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * {@link ComponentAudiences} backed by the adventure-platform-bukkit {@link BukkitAudiences} bridge.
 * Requires the {@code adventure-platform-bukkit} dependency on the consumer's classpath — this module
 * only declares it {@code compileOnly}. Prefer this over {@link PaperComponentAudiences} when the
 * server does not natively implement Adventure (pre-Paper Spigot, or older Paper builds).
 */
public record BukkitComponentAudiences(@NotNull BukkitAudiences audiences) implements ComponentAudiences {

    @Override
    public @NotNull Audience player(@NotNull Player player) {
        return audiences.player(player);
    }

    @Override
    public @NotNull Audience sender(@NotNull CommandSender sender) {
        return audiences.sender(sender);
    }

    @Override
    public @NotNull Audience player(@NotNull UUID playerId) {
        return audiences.player(playerId);
    }

    @Override
    public @NotNull Audience all() {
        return audiences.all();
    }

    @Override
    public @NotNull Audience filter(@NotNull Predicate<CommandSender> filter) {
        return audiences.filter(filter);
    }

    @Override
    public @NotNull Audience console() {
        return audiences.console();
    }

    @Override
    public @NotNull Audience permission(@NotNull Key permission) {
        return audiences.permission(permission);
    }

    @Override
    public @NotNull Audience permission(@NotNull String permission) {
        return audiences.permission(permission);
    }

    @Override
    public @NotNull Audience players() {
        return audiences.players();
    }

    @Override
    public @NotNull Audience server(@NotNull String serverName) {
        return audiences.server(serverName);
    }

    @Override
    public @NotNull Audience world(@NotNull Key worldKey) {
        return audiences.world(worldKey);
    }

}
