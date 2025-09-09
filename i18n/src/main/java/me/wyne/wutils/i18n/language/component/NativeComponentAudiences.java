package me.wyne.wutils.i18n.language.component;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Predicate;

public class NativeComponentAudiences implements ComponentAudiences {

    @Override
    public Audience player(Player player) {
        return player;
    }

    @Override
    public Audience sender(CommandSender sender) {
        return sender;
    }

    @Override
    public Audience player(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        return player;
    }

    @Override
    public Audience all() {
        return Bukkit.getServer();
    }

    @Override
    public Audience filter(Predicate<CommandSender> filter) {
        return Audience.audience(Bukkit.getOnlinePlayers().stream()
                .filter(filter)
                .toList());
    }

    @Override
    public Audience console() {
        return Bukkit.getConsoleSender();
    }

    @Override
    public Audience permission(Key permission) {
        return permission(permission.namespace() + '.' + permission.value());
    }

    @Override
    public Audience permission(String permission) {
        return filter(sender -> sender.hasPermission(permission));
    }

    @Override
    public Audience players() {
        return Audience.audience(Bukkit.getOnlinePlayers());
    }

    @Override
    public Audience server(String serverName) {
        return Bukkit.getServer();
    }

    @Override
    public Audience world(Key worldKey) {
        return Bukkit.getWorld(worldKey.value());
    }

}
