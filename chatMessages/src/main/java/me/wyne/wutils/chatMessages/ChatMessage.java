package me.wyne.wutils.chatMessages;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

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
        this.message = builder.message;
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
                if (!player.hasPermission(permission))
                    break;
            }
            player.sendMessage(message);
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
    public static Builder Builder() { return new Builder(); }

    @Contract("_ -> new")
    public static Builder Builder(@NotNull final String message)
    {
        return new Builder(message);
    }

    @Contract("_ -> new")
    public static Builder Builder(@NotNull final Component message)
    {
        return new Builder(message);
    }

    @Contract("_, _ -> new")
    public static Builder Builder(@NotNull final String message, String... permissions)
    {
        return new Builder(message, permissions);
    }

    @Contract("_, _ -> new")
    public static Builder Builder(@NotNull final String message, @NotNull Set<String> permissions)
    {
        return new Builder(message, permissions);
    }

    @Contract("_, _ -> new")
    public static Builder Builder(@NotNull final Component message, String... permissions)
    {
        return new Builder(message, permissions);
    }

    @Contract("_, _ -> new")
    public static Builder Builder(@NotNull final Component message, @NotNull Set<String> permissions)
    {
        return new Builder(message, permissions);
    }

    public static final class Builder
    {
        private Set<String> permissions = new HashSet<>();
        private Component message;

        Builder() {}
        Builder(@NotNull final String message)
        {
            this.message = Component.text(message);
        }
        Builder(@NotNull final Component message)
        {
            this.message = message;
        }
        Builder(@NotNull final String message, String... permissions)
        {
            this.message = Component.text(message);
            this.permissions = permissions != null ? Set.of(permissions) : new HashSet<>();
        }
        Builder(@NotNull final String message, @NotNull Set<String> permissions)
        {
            this.message = Component.text(message);
            this.permissions = permissions;
        }
        Builder(@NotNull final Component message, String... permissions)
        {
            this.message = message;
            this.permissions = permissions != null ? Set.of(permissions) : new HashSet<>();
        }
        Builder(@NotNull final Component message, @NotNull Set<String> permissions)
        {
            this.message = message;
            this.permissions = permissions;
        }

        @Contract("_ -> this")
        public Builder setMessage(@NotNull final String message)
        {
            this.message = Component.text(message);
            return this;
        }

        @Contract("_ -> this")
        public Builder setMessage(@NotNull final Component message)
        {
            this.message = message;
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
                message = Component.text(MiniMessage.miniMessage().stripTags(MiniMessage.miniMessage().serialize(message)));
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
         * Apply {@link MiniMessage} tags.
         * <br>Requires {@link net.kyori.adventure.text.minimessage} dependency.
         */
        @Contract("-> this")
        public Builder applyTags()
        {
            try {
                message = MiniMessage.miniMessage().deserialize(MiniMessage.miniMessage().serialize(message));
            } catch (NoClassDefFoundError e) {
                if (!Log.error("Trying to use applyTags in ChatMessage but net.kyori.adventure.text.minimessage is not included"))
                    Bukkit.getLogger().severe("Trying to use applyTags in ChatMessage but net.kyori.adventure.text.minimessage is not included");
                if (!Log.error(e.getMessage()))
                    Bukkit.getLogger().severe(e.getMessage());
                return this;
            }
            return this;
        }

        /**
         * Set {@link PlaceholderAPI} placeholders.
         * <br>Requires {@link net.kyori.adventure.text.minimessage} dependency.
         * <br>Requires {@link me.clip.placeholderapi} dependency.
         */
        @Contract("_ -> this")
        public Builder setPlaceholders(@NotNull final OfflinePlayer player)
        {
            try {
                message = MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, MiniMessage.miniMessage().serialize(message)));
            } catch (NoClassDefFoundError e) {
                if (!Log.error("Trying to use setPlaceholders in ChatMessage but me.clip.placeholderapi or net.kyori.adventure.text.minimessage is not included"))
                    Bukkit.getLogger().severe("Trying to use setPlaceholders in ChatMessage but me.clip.placeholderapi or net.kyori.adventure.text.minimessage is not included");
                if (!Log.error(e.getMessage()))
                    Bukkit.getLogger().severe(e.getMessage());
                return this;
            }
            return this;
        }

        @Contract("_ -> this")
        public Builder modifyMessage(@NotNull final Function<Component, Component> modification)
        {
            message = modification.apply(message);
            return this;
        }

        @Contract("_ -> this")
        public Builder modifyPermissions(@NotNull final Function<Set<String>, Set<String>> modification)
        {
            permissions = modification.apply(permissions);
            return this;
        }

        @Contract("-> new")
        public ChatMessage build()
        {
            return new ChatMessage(this);
        }
    }

}
