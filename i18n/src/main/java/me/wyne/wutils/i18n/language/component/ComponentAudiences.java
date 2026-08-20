package me.wyne.wutils.i18n.language.component;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * Resolves Bukkit senders and groups of senders into Adventure {@link Audience}s. See
 * {@link PaperComponentAudiences} and {@link BukkitComponentAudiences} for the two implementations
 * this module ships.
 */
public interface ComponentAudiences {
    @NotNull Audience player(@NotNull Player player);

    @NotNull Audience sender(@NotNull CommandSender sender);

    @NotNull Audience player(@NotNull UUID playerId);

    @NotNull Audience all();

    @NotNull Audience filter(@NotNull Predicate<CommandSender> filter);

    @NotNull Audience console();

    @NotNull Audience permission(@NotNull Key permission);

    @NotNull Audience permission(@NotNull String permission);

    @NotNull Audience players();

    @NotNull Audience server(@NotNull String serverName);

    @NotNull Audience world(@NotNull Key worldKey);
}
