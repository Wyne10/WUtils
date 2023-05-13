package me.wyne.wutils.log;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class LogMessage {

    private Level level = Level.INFO;
    private final String message;

    public LogMessage(@NotNull final String message)
    {
        this.message = message;
    }
    public LogMessage(@NotNull final Level level, @NotNull final String message)
    {
        this.level = level;
        this.message = message;
    }
    LogMessage(@NotNull final Builder builder)
    {
        this.level = builder.level;
        this.message = builder.message;
    }

    @NotNull
    public Level getLevel() {
        return level;
    }
    @NotNull
    public String getMessage() {
        return message;
    }

    @Contract("-> new")
    public static Builder builder()
    {
        return new Builder();
    }

    @Contract("_, _ -> new")
    public static Builder builder(@NotNull final Level level, @NotNull final String message)
    {
        return new Builder(level, message);
    }

    @Contract("_ -> new")
    public static Builder builder(@NotNull final String message)
    {
        return new Builder(message);
    }

    @Contract("_ -> new")
    public static Builder builder(@NotNull final LogMessage logMessage)
    {
        return new Builder(logMessage);
    }

    @Contract("-> new")
    @NotNull
    public Builder toBuilder()
    {
        return new Builder(this);
    }

    public static final class Builder
    {
        private Level level = Level.INFO;
        private String message;

        Builder() {}
        Builder(@NotNull final String message)
        {
            this.message = message;
        }
        Builder(@NotNull final LogMessage logMessage)
        {
            this.level = logMessage.level;
            this.message = logMessage.message;
        }
        Builder(@NotNull final Level level, @NotNull final String message)
        {
            this.level = level;
            this.message = message;
        }

        @Contract("_ -> this")
        public Builder setLevel(@NotNull final Level level)
        {
            this.level = level;
            return this;
        }

        @Contract("_ -> this")
        public Builder setMessage(@NotNull final String message)
        {
            this.message = message;
            return this;
        }

        /**
         * Strip {@link MiniMessage} tags.
         * Requires {@link net.kyori.adventure.text.minimessage} dependency.
         */
        @Contract("-> this")
        public Builder stripTags()
        {
            try {
                Class.forName("net.kyori.adventure.text.minimessage.MiniMessage", false, getClass().getClassLoader());
            } catch (ClassNotFoundException e) {
                if (!Log.error("Trying to use stripTags in LogMessage but net.kyori.adventure.text.minimessage is not included"))
                    Bukkit.getLogger().severe("Trying to use stripTags in LogMessage but net.kyori.adventure.text.minimessage is not included");
                if (!Log.error(e.getMessage()))
                    Bukkit.getLogger().severe(e.getMessage());
                return this;
            }
            message = MiniMessage.miniMessage().stripTags(message);
            return this;
        }

        /**
         * Set {@link PlaceholderAPI} placeholders.
         * Requires {@link me.clip.placeholderapi} dependency.
         */
        @Contract("_ -> this")
        public Builder setPlaceholders(@NotNull final OfflinePlayer player)
        {
            try {
                Class.forName("me.clip.placeholderapi.PlaceholderAPI", false, getClass().getClassLoader());
            } catch (ClassNotFoundException e) {
                if (!Log.error("Trying to use setPlaceholders in LogMessage but me.clip.placeholderapi is not included"))
                    Bukkit.getLogger().severe("Trying to use setPlaceholders in LogMessage but me.clip.placeholderapi is not included");
                if (!Log.error(e.getMessage()))
                    Bukkit.getLogger().severe(e.getMessage());
                return this;
            }
            message = PlaceholderAPI.setPlaceholders(player, message);
            return this;
        }

        /**
         * Replace {@link #message} text by regex.
         */
        @Contract("_, _ -> this")
        public Builder replaceAll(@NotNull final String replaceRegex, @NotNull final String replacement)
        {
            message = message.replaceAll(replaceRegex, replacement);
            return this;
        }

        /**
         * Replace {@link #message} literal text.
         */
        @Contract("_, _ -> this")
        public Builder replace(@NotNull final String replace, @NotNull final String replacement)
        {
            message = message.replace(replace, replacement);
            return this;
        }

        @Contract("-> new")
        public LogMessage build()
        {
            return new LogMessage(this);
        }
    }
}
