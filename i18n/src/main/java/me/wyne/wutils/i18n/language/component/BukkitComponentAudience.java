package me.wyne.wutils.i18n.language.component;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Predicate;

public class BukkitComponentAudience implements ComponentAudience {

    private final BukkitAudiences audiences;

    public BukkitComponentAudience(BukkitAudiences audiences) {
        this.audiences = audiences;
    }

    @Override
    public void sendMessage(Player player, Component component) {
        audiences.player(player).sendMessage(component);
    }

    @Override
    public void sendMessage(CommandSender sender, Component component) {
        audiences.sender(sender).sendMessage(component);
    }

    @Override
    public void sendMessage(UUID playerId, Component component) {
        audiences.player(playerId).sendMessage(component);
    }

    @Override
    public void sendMessageAll(Component component) {
        audiences.all().sendMessage(component);
    }

    @Override
    public void sendMessage(Predicate<CommandSender> filter, Component component) {
        audiences.filter(filter).sendMessage(component);
    }

    @Override
    public void sendMessageConsole(Component component) {
        audiences.console().sendMessage(component);
    }

    @Override
    public void sendMessage(Key permission, Component component) {
        audiences.permission(permission).sendMessage(component);
    }

    @Override
    public void sendMessage(String permission, Component component) {
        audiences.permission(permission).sendMessage(component);
    }

    @Override
    public void sendMessagePlayers(Component component) {
        audiences.players().sendMessage(component);
    }

    @Override
    public void sendMessageServer(String serverName, Component component) {
        audiences.server(serverName).sendMessage(component);
    }

    @Override
    public void sendMessageWorld(Key worldKey, Component component) {
        audiences.world(worldKey).sendMessage(component);
    }

    @Override
    public void sendActionBar(Player player, Component component) {
        audiences.player(player).sendActionBar(component);
    }

    @Override
    public void sendActionBar(Player player, ComponentLike component) {
        audiences.player(player).sendActionBar(component);
    }

}
