package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.config.configurables.attribute.CompositeAttribute;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;

public class FireworkEffectsAttribute extends CompositeAttribute<FireworkEffectAttribute> {

    public FireworkEffectsAttribute(String key, Set<FireworkEffectAttribute> colors) {
        super(key, colors);
    }

    public FireworkEffectsAttribute(String key, ConfigurationSection config) {
        super(key, config, new FireworkEffectAttribute.Factory());
    }

}

