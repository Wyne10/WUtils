package me.wyne.wutils.config.configurables.attribute.common;

import me.wyne.wutils.config.configurables.attribute.CompositeAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * A {@link CompositeAttribute} of {@link ColorAttribute}s — one YAML key holding several named
 * colors as child sections.
 */
public class ColorsAttribute extends CompositeAttribute<ColorAttribute> {

    public ColorsAttribute(@NotNull String key, @NotNull Set<@NotNull ColorAttribute> colors) {
        super(key, colors);
    }

    public ColorsAttribute(@NotNull String key, @NotNull ConfigurationSection config) {
        super(key, config, new ColorAttribute.Factory());
    }

}

