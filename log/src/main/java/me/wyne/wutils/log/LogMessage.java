package me.wyne.wutils.log;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Contract;

import java.util.function.Function;
import java.util.logging.Level;

public class LogMessage {

    private Level level = Level.INFO;
    private final String message;

    public LogMessage(String message)
    {
        this.message = message;
    }
    public LogMessage(Level level, String message)
    {
        this.level = level;
        this.message = message;
    }
    LogMessage(Builder builder)
    {
        this.level = builder.level;
        this.message = builder.message;
    }

    public Level getLevel() {
        return level;
    }
    public String getMessage() {
        return message;
    }

    @Contract("-> new")
    public static Builder builder()
    {
        return new Builder();
    }

    @Contract("_, _ -> new")
    public static Builder builder(Level level, String message)
    {
        return new Builder(level, message);
    }

    @Contract("_ -> new")
    public static Builder builder(String message)
    {
        return new Builder(message);
    }

    @Contract("_ -> new")
    public static Builder builder(LogMessage logMessage)
    {
        return new Builder(logMessage);
    }

    @Contract("-> new")
    public Builder toBuilder()
    {
        return new Builder(this);
    }

    public static final class Builder
    {
        private Level level = Level.INFO;
        private String message;

        Builder() {}
        Builder(String message)
        {
            this.message = message;
        }
        Builder(LogMessage logMessage)
        {
            this.level = logMessage.level;
            this.message = logMessage.message;
        }
        Builder(Level level, String message)
        {
            this.level = level;
            this.message = message;
        }

        @Contract("_ -> this")
        public Builder setLevel(Level level)
        {
            this.level = level;
            return this;
        }

        @Contract("_ -> this")
        public Builder setMessage(String message)
        {
            this.message = message;
            return this;
        }

        @Contract("-> this")
        public Builder stripTags()
        {
            message = MiniMessage.miniMessage().stripTags(message);
            return this;
        }

        @Contract("_ -> this")
        public Builder setPlaceholders(OfflinePlayer player)
        {
            message = PlaceholderAPI.setPlaceholders(player, message);
            return this;
        }

        @Contract("_, _ -> this")
        public Builder replaceAll(String replaceRegex, String replacement)
        {
            message = message.replaceAll(replaceRegex, replacement);
            return this;
        }

        @Contract("_, _ -> this")
        public Builder replace(String replace, String replacement)
        {
            message = message.replace(replace, replacement);
            return this;
        }

        @Contract("_ -> this")
        public Builder modifyMessage(Function<String, String> modification)
        {
            message = modification.apply(message);
            return this;
        }

        @Contract("-> new")
        public LogMessage build()
        {
            return new LogMessage(this);
        }
    }
}
