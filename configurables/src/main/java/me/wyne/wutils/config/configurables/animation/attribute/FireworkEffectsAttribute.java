package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.config.configurables.item.CompositeAttribute;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class FireworkEffectsAttribute extends CompositeAttribute<FireworkEffectAttribute> {

    public FireworkEffectsAttribute(String key, Map<String, FireworkEffectAttribute> colors) {
        super(key, colors);
    }

    public FireworkEffectsAttribute(String key, ConfigurationSection config) {
        super(key, config, new FireworkEffectAttribute.Factory());
    }

}

