package me.wyne.wutils.config.configFieldTypes;

import me.wyne.wutils.chatMessages.ChatMessage;
import me.wyne.wutils.config.ConfigParameter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class ChatMessageBuilderFieldType implements ConfigParameter {

    /**
     * @return String as {@link ChatMessage} builder
     */
    @Override
    public Object getValue(@NotNull FileConfiguration config, @NotNull String path) {
        return ChatMessage.builder(config.getString(path));
    }

}
