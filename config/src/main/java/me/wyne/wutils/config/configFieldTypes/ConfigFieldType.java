package me.wyne.wutils.config.configFieldTypes;

import me.wyne.wutils.config.ConfigParameter;
import org.jetbrains.annotations.NotNull;

public enum ConfigFieldType {
    MiniMessageComponent(new MiniMessageComponentFieldType()),
    MiniMessageChatMessage(new MiniMessageChatMessageFieldType()),
    ChatMessageBuilder(new ChatMessageBuilderFieldType());

    private final ConfigParameter configParameter;

    ConfigFieldType(@NotNull final ConfigParameter configParameter)
    {
        this.configParameter = configParameter;
    }

    public ConfigParameter getConfigParameter() {
        return configParameter;
    }
}
