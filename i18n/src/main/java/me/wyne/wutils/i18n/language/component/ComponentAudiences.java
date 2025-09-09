package me.wyne.wutils.i18n.language.component;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Predicate;

public interface ComponentAudiences {
    Audience player(Player player);

    Audience sender(CommandSender sender);

    Audience player(UUID playerId);

    Audience all();

    Audience filter(Predicate<CommandSender> filter);

    Audience console();

    Audience permission(Key permission);

    Audience permission(String permission);

    Audience players();

    Audience server(String serverName);

    Audience world(Key worldKey);
}
