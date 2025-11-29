package me.wyne.wutils.config.configurables.attribute.common;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.CompositeAttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;

public class ColorAttribute extends ConfigurableAttribute<Color> {

    public ColorAttribute(String key, Color value) {
        super(key, value);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().asRGB()).buildNoSpace();
    }

    public static final class Factory implements CompositeAttributeFactory<ColorAttribute> {
        @Override
        public ColorAttribute fromSection(String key, ConfigurationSection section) {
            return new ColorAttribute(
                    key,
                    Color.fromRGB(
                            section.getInt("red", 0),
                            section.getInt("green", 0),
                            section.getInt("blue", 0)
                    )
            );
        }

        @Override
        public ColorAttribute fromString(String key, String string, ConfigurationSection config) {
            return new ColorAttribute(
                    key,
                    Color.fromRGB(Integer.parseUnsignedInt(string.replace("#", ""), 16))
            );
        }
    }

}
