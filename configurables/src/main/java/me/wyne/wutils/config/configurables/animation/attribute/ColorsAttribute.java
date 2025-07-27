package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.config.configurables.item.CompositeAttribute;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class ColorsAttribute extends CompositeAttribute<ColorAttribute> {

    public ColorsAttribute(String key, Map<String, ColorAttribute> colors) {
        super(key, colors);
    }

    public ColorsAttribute(String key, ConfigurationSection config) {
        super(key, config, new ColorAttribute.Factory());
    }

}

