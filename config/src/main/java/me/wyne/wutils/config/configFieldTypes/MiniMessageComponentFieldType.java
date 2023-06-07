package me.wyne.wutils.config.configFieldTypes;

import me.wyne.wutils.config.ConfigParameter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class MiniMessageComponentFieldType implements ConfigParameter {

    /**
     * @return MiniMessage deserialized string
     */
    @Override
    public Object getValue(@NotNull FileConfiguration config, @NotNull String path) {
        return MiniMessage.miniMessage().deserialize(config.getString(path));
    }

}
