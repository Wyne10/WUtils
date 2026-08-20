package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GenericFactory} that dispatches on the shape of the YAML value at {@code key}: a nested
 * section goes to {@link #fromSection}, a string goes to {@link #fromString}, and anything else
 * (including a key that is absent) falls back to treating the <em>enclosing</em> section itself as
 * the value's body via {@link #fromSection}.
 *
 * <p>That fallback branch is what makes {@code attributeType} aliasing work — see
 * {@link AttributeMap} — because an aliased key is not itself a key inside its own section, so
 * dispatch falls through to it. A plain {@link GenericFactory} has no such fallback, but reaches the
 * same shape by resolving its body with {@code ConfigUtils.getConfigurationSection(config, key)} —
 * the idiom the {@code structure} module uses throughout — and can be aliased without either, if the
 * alias body repeats the alias name. What this interface adds is accepting both shapes at once.</p>
 *
 * @param <T> the type this factory builds
 */
public interface CompositeGenericFactory<T> extends GenericFactory<T> {
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
