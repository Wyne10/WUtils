package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link AttributeFactory} that dispatches on the shape of the YAML value at {@code key}: a
 * nested section goes to {@link #fromSection}, a string goes to {@link #fromString}, and anything
 * else falls back to treating the <em>enclosing</em> section as the attribute's own body via
 * {@link #fromSection}.
 *
 * <p>That fallback branch lets an {@code attributeType} alias body name its own fields rather than
 * repeating the alias name — see {@link AttributeMap} — because an aliased key is not itself a key
 * inside its own section, so dispatch falls through to it. A plain {@link AttributeFactory} gets the
 * same by resolving its body with {@code ConfigUtils.getConfigurationSection(config, key)}. What
 * only this interface adds is accepting <em>both</em> shapes: {@link #fromString} for a bare scalar
 * at {@code key}, {@link #fromSection} for a named body.</p>
 *
 * @param <T> the attribute type this factory builds
 */
public interface CompositeAttributeFactory<T extends Attribute<?>> extends AttributeFactory<T> {
    @Override
    default @NotNull T create(@NotNull String key, @NotNull ConfigurationSection config) {
        if (config.isConfigurationSection(key))
            return fromSection(key, config.getConfigurationSection(key));
        else if (config.isString(key))
            return fromString(key, config.getString(key), config);
        else
            return fromSection(key, config);
    }

    @NotNull T fromSection(@NotNull String key, @NotNull ConfigurationSection section);

    @NotNull T fromString(@NotNull String key, @NotNull String string, @NotNull ConfigurationSection config);
}
