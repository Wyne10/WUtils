package me.wyne.wutils.config.configFieldTypes;

import me.wyne.wutils.chatMessages.ChatMessage;
import me.wyne.wutils.config.ConfigParameter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class MiniMessageChatMessageFieldType implements ConfigParameter {

    /**
     * @return {@link MiniMessage} deserialized string as {@link ChatMessage}
     */
    @Override
    public Object getValue(@NotNull FileConfiguration config, @NotNull String path) {
        return new ChatMessage(MiniMessage.miniMessage().deserialize(config.getString(path)));
    }

}
