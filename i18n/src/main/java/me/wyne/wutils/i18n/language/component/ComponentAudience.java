package me.wyne.wutils.i18n.language.component;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Predicate;

public interface ComponentAudience {
    default void sendMessage(Audience audience, Component component) {
        audience.sendMessage(component);
    }

    void sendMessage(Player player, Component component);

    void sendMessage(CommandSender sender, Component component);

    void sendMessage(UUID playerId, Component component);

    void sendMessageAll(Component component);

    void sendMessage(Predicate<CommandSender> filter, Component component);

    void sendMessageConsole(Component component);

    void sendMessage(Key permission, Component component);

    void sendMessage(String permission, Component component);

    void sendMessagePlayers(Component component);

    void sendMessageServer(String serverName, Component component);

    void sendMessageWorld(Key worldKey, Component component);

    void sendActionBar(Player player, Component component);

    void sendActionBar(Player player, ComponentLike component);
}
