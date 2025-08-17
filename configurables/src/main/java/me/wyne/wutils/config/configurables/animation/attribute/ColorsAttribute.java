package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.config.configurables.attribute.CompositeAttribute;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;

public class ColorsAttribute extends CompositeAttribute<ColorAttribute> {

    public ColorsAttribute(String key, Set<ColorAttribute> colors) {
        super(key, colors);
    }

    public ColorsAttribute(String key, ConfigurationSection config) {
        super(key, config, new ColorAttribute.Factory());
    }

}

