package me.wyne.wutils.i18n.language.component;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Predicate;

public class BukkitComponentAudiences implements ComponentAudiences {

    private final BukkitAudiences audiences;

    public BukkitComponentAudiences(BukkitAudiences audiences) {
        this.audiences = audiences;
    }

    @Override
    public Audience player(Player player) {
        return audiences.player(player);
    }

    @Override
    public Audience sender(CommandSender sender) {
        return audiences.sender(sender);
    }

    @Override
    public Audience player(UUID playerId) {
        return audiences.player(playerId);
    }

    @Override
    public Audience all() {
        return audiences.all();
    }

    @Override
    public Audience filter(Predicate<CommandSender> filter) {
        return audiences.filter(filter);
    }

    @Override
    public Audience console() {
        return audiences.console();
    }

    @Override
    public Audience permission(Key permission) {
        return audiences.permission(permission);
    }

    @Override
    public Audience permission(String permission) {
        return audiences.permission(permission);
    }

    @Override
    public Audience players() {
        return audiences.players();
    }

    @Override
    public Audience server(String serverName) {
        return audiences.server(serverName);
    }

    @Override
    public Audience world(Key worldKey) {
        return audiences.world(worldKey);
    }

}
