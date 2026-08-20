package me.wyne.wutils.config.configurables.attribute.common;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * A collection of strings read via {@link ConfigUtils#getStringList}. {@link ListFactory} preserves
 * the config's order and duplicates; {@link SetFactory} de-duplicates while keeping order.
 */
public class StringCollectionAttribute extends ConfigurableAttribute<Collection<String>> {

    public StringCollectionAttribute(@NotNull String key, @NotNull Collection<@NotNull String> value) {
        super(key, value);
    }

    public static final class ListFactory implements AttributeFactory<StringCollectionAttribute> {
        @Override
        public @NotNull StringCollectionAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new StringCollectionAttribute(key, ConfigUtils.getStringList(config, key));
        }
    }

    public static final class SetFactory implements AttributeFactory<StringCollectionAttribute> {
        @Override
        public @NotNull StringCollectionAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new StringCollectionAttribute(key, new LinkedHashSet<>(ConfigUtils.getStringList(config, key)));
        }
    }

}
