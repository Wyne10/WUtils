package me.wyne.wutils.config.configurables.attribute.common;

import me.wyne.wutils.config.configurables.attribute.AttributeBase;
import me.wyne.wutils.config.configurables.attribute.AttributeContainerBase;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Holds the entire {@link ConfigurationSection} a container was read from. Inserted under the key
 * {@code "root"} by {@link AttributeContainerBase#fromConfig}, ahead of every other attribute, as an
 * escape hatch for config keys this module's registered attributes know nothing about. Extends plain
 * {@link AttributeBase}, so it never appears in generated config.
 */
public class RootAttribute extends AttributeBase<ConfigurationSection> {

    public RootAttribute(@NotNull String key, @NotNull ConfigurationSection value) {
        super(key, value);
    }

    public static final class Factory implements AttributeFactory<RootAttribute> {
        @Override
        public @NotNull RootAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new RootAttribute(key, config);
        }
    }

}
