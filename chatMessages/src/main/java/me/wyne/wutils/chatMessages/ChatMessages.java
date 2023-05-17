package me.wyne.wutils.chatMessages;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public final class ChatMessages {

    public static void sendMessage(@NotNull final Player player, @NotNull final Component message)
    {
        if (message == Component.empty())
            return;
        player.sendMessage(message);
    }

    public static void sendMessageGroup(@NotNull final Set<String> permissions, @NotNull final Component message)
    {
        if (message == Component.empty())
            return;
        for (Player player : Bukkit.getOnlinePlayers())
        {
            for (String permission : permissions)
            {
                if (player.hasPermission(permission))
                {
                    player.sendMessage(message);
                    break;
                }
            }
        }
    }

    public void sendMessageGroupAsync(@NotNull final Set<String> permissions, @NotNull final Component message, @NotNull final Executor executor)
    {
        executor.execute(() -> sendMessageGroup(permissions, message));
    }

    public Future<?> sendMessageGroupAsync(@NotNull final Set<String> permissions, @NotNull final Component message, @NotNull final ExecutorService executor)
    {
        return executor.submit(() -> sendMessageGroup(permissions, message));
    }

}
