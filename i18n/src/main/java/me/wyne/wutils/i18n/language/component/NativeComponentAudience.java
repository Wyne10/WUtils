package me.wyne.wutils.i18n.language.component;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Predicate;

public class NativeComponentAudience implements ComponentAudience {

    @Override
    public void sendMessage(Player player, Component component) {
        player.sendMessage(component);
    }

    @Override
    public void sendMessage(CommandSender sender, Component component) {
        sender.sendMessage(component);
    }

    @Override
    public void sendMessage(UUID playerId, Component component) {
        Player player = Bukkit.getPlayer(playerId);
        if (player != null)
            player.sendMessage(component);
    }

    @Override
    public void sendMessageAll(Component component) {
        Bukkit.broadcast(component);
    }

    @Override
    public void sendMessage(Predicate<CommandSender> filter, Component component) {
        Bukkit.getOnlinePlayers().stream()
                .filter(filter)
                .forEach(player -> player.sendMessage(component));
    }

    @Override
    public void sendMessageConsole(Component component) {
        Bukkit.getConsoleSender().sendMessage(component);
    }

    @Override
    public void sendMessage(Key permission, Component component) {
        sendMessage(permission.namespace() + '.' + permission.value(), component);
    }

    @Override
    public void sendMessage(String permission, Component component) {
        Bukkit.broadcast(component, permission);
    }

    @Override
    public void sendMessagePlayers(Component component) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(component));
    }

    @Override
    public void sendMessageServer(String serverName, Component component) {
        if (Bukkit.getServer().getName().equals(serverName))
            Bukkit.broadcast(component);
    }

    @Override
    public void sendMessageWorld(Key worldKey, Component component) {
        World world = Bukkit.getWorld(worldKey.value());
        if (world != null)
            world.sendMessage(component);
    }

}
