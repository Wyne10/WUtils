package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.config.configurables.attribute.CompositeAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * The nested {@code effects} collection under a {@code firework} section — one
 * {@link FireworkEffectAttribute} per child key.
 */
public class FireworkEffectsAttribute extends CompositeAttribute<FireworkEffectAttribute> {

    public FireworkEffectsAttribute(@NotNull String key, @NotNull Set<@NotNull FireworkEffectAttribute> colors) {
        super(key, colors);
    }

    public FireworkEffectsAttribute(@NotNull String key, @NotNull ConfigurationSection config) {
        super(key, config, new FireworkEffectAttribute.Factory());
    }

}

