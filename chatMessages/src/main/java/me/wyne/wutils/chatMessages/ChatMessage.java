package me.wyne.wutils.chatMessages;

import me.clip.placeholderapi.PlaceholderAPI;
import me.wyne.wutils.config.ConfigParameter;
import me.wyne.wutils.log.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * THIS MODULE IS STILL INCOMPLETE, WORK IN PROGRESS
 */
public class ChatMessage {

    private Set<String> permissions = new HashSet<>();
    private final Component message;

    public ChatMessage(@NotNull final String message, String... permissions)
    {
        this.message = Component.text(message);
        this.permissions = permissions != null ? Set.of(permissions) : new HashSet<>();
    }
    public ChatMessage(@NotNull final Component message, String... permissions)
    {
        this.message = message;
        this.permissions = permissions != null ? Set.of(permissions) : new HashSet<>();
    }
    public ChatMessage(@NotNull final String message, @NotNull final Set<String> permissions)
    {
        this.message = Component.text(message);
        this.permissions = permissions;
    }
    public ChatMessage(@NotNull final Component message, @NotNull final Set<String> permissions)
    {
        this.message = message;
        this.permissions = permissions;
    }
    ChatMessage(@NotNull final Builder builder)
    {
        this.message = MiniMessage.miniMessage().deserialize(builder.message);
        this.permissions = builder.permissions;
    }

    @NotNull
    public Component getMessage() {
        return message;
    }

    @NotNull
    public Set<String> getPermissions() { return permissions; }

    public void sendMessage(@NotNull final Player player)
    {
        if (message == Component.empty())
            return;
        player.sendMessage(message);
    }

    public void sendMessageGroup()
    {
        sendMessageGroup(permissions);
    }

    public void sendMessageGroup(@NotNull final Set<String> permissions)
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

    public void sendMessageGroupAsync(@NotNull final Executor executor)
    {
        executor.execute(() -> sendMessageGroup());
    }

    public Future<?> sendMessageGroupAsync(@NotNull final ExecutorService executor)
    {
        return executor.submit(() -> sendMessageGroup());
    }

    public void sendMessageGroupAsync(@NotNull final Set<String> permissions, @NotNull final Executor executor)
    {
        executor.execute(() -> sendMessageGroup(permissions));
    }

    public Future<?> sendMessageGroupAsync(@NotNull final Set<String> permissions, @NotNull final ExecutorService executor)
    {
        return executor.submit(() -> sendMessageGroup(permissions));
    }

    @Contract("-> new")
    public static Builder builder() { return new Builder(); }

    @Contract("_ -> new")
    public static Builder builder(@NotNull final String message)
    {
        return new Builder(message);
    }

    @Contract("_ -> new")
    public static Builder builder(@NotNull final Component message)
    {
        return new Builder(message);
    }

    @Contract("_, _ -> new")
    public static Builder builder(@NotNull final String message, String... permissions)
    {
        return new Builder(message, permissions);
    }

    @Contract("_, _ -> new")
    public static Builder builder(@NotNull final String message, @NotNull Set<String> permissions)
    {
        return new Builder(message, permissions);
    }

    @Contract("_, _ -> new")
    public static Builder builder(@NotNull final Component message, String... permissions)
    {
        return new Builder(message, permissions);
    }

    @Contract("_, _ -> new")
    public static Builder builder(@NotNull final Component message, @NotNull Set<String> permissions)
    {
        return new Builder(message, permissions);
    }

    @Contract("-> new")
    @NotNull
    public ChatMessage.Builder toBuilder()
    {
        return new ChatMessage.Builder(this);
    }

    public static final class Builder implements ConfigParameter
    {
        private Set<String> permissions = new HashSet<>();
        private String message;

        Builder() {}
        Builder(@NotNull final String message)
        {
            this.message = message;
        }
        Builder(@NotNull final Component message)
        {
            this.message = MiniMessage.miniMessage().serialize(message);
        }
        Builder(@NotNull final String message, String... permissions)
        {
            this.message = message;
            this.permissions = permissions != null ? Set.of(permissions) : new HashSet<>();
        }
        Builder(@NotNull final String message, @NotNull Set<String> permissions)
        {
            this.message = message;
            this.permissions = permissions;
        }
        Builder(@NotNull final Component message, String... permissions)
        {
            this.message = MiniMessage.miniMessage().serialize(message);;
            this.permissions = permissions != null ? Set.of(permissions) : new HashSet<>();
        }
        Builder(@NotNull final Component message, @NotNull Set<String> permissions)
        {
            this.message = MiniMessage.miniMessage().serialize(message);;
            this.permissions = permissions;
        }
        Builder(@NotNull final ChatMessage chatMessage)
        {
            this.message = MiniMessage.miniMessage().serialize(chatMessage.message);
            this.permissions = chatMessage.permissions;
        }
        Builder(@NotNull final Builder builder)
        {
            this.message = builder.message;
            this.permissions = builder.permissions;
        }

        @Override
        public Object getValue(@NotNull FileConfiguration config, @NotNull String path) {
            this.message = config.getString(path);
            return null;
        }

        @Contract("_ -> this")
        public Builder setMessage(@NotNull final String message)
        {
            this.message = message;
            return this;
        }

        @Contract("_ -> this")
        public Builder setMessage(@NotNull final Component message)
        {
            this.message = MiniMessage.miniMessage().serialize(message);
            return this;
        }

        @Contract("_ -> this")
        public Builder setPermissions(@NotNull final Set<String> permissions)
        {
            this.permissions = permissions;
            return this;
        }

        /**
         * Strip {@link MiniMessage} tags.
         * <br>Requires {@link net.kyori.adventure.text.minimessage} dependency.
         */
        @Contract("-> this")
        public Builder stripTags()
        {
            try {
                message = MiniMessage.miniMessage().stripTags(message);
            } catch (NoClassDefFoundError e) {
                if (!Log.error("Trying to use stripTags in ChatMessage but net.kyori.adventure.text.minimessage is not included"))
                    Bukkit.getLogger().severe("Trying to use stripTags in ChatMessage but net.kyori.adventure.text.minimessage is not included");
                if (!Log.error(e.getMessage()))
                    Bukkit.getLogger().severe(e.getMessage());
                return this;
            }
            return this;
        }

        /**
         * Set {@link PlaceholderAPI} placeholders.
         * <br>Requires {@link me.clip.placeholderapi} dependency.
         */
        @Contract("_ -> this")
        public Builder setPlaceholders(@NotNull final OfflinePlayer player)
        {
            try {
                message = PlaceholderAPI.setPlaceholders(player, message);
            } catch (NoClassDefFoundError e) {
                if (!Log.error("Trying to use setPlaceholders in ChatMessage but me.clip.placeholderapi is not included"))
                    Bukkit.getLogger().severe("Trying to use setPlaceholders in ChatMessage but me.clip.placeholderapi is not included");
                if (!Log.error(e.getMessage()))
                    Bukkit.getLogger().severe(e.getMessage());
                return this;
            }
            return this;
        }

        @Contract("_ -> this")
        public Builder modifyMessage(@NotNull final Function<Component, Component> modification)
        {
            message = MiniMessage.miniMessage().serialize(modification.apply(MiniMessage.miniMessage().deserialize(message)));
            return this;
        }

        @Contract("_ -> this")
        public Builder modifyPermissions(@NotNull final Function<Set<String>, Set<String>> modification)
        {
            permissions = modification.apply(permissions);
            return this;
        }

        @Contract("-> new")
        public Builder clone()
        {
            return new Builder(this);
        }

        @Contract("-> new")
        public ChatMessage build()
        {
            return new ChatMessage(this);
        }
    }

}
