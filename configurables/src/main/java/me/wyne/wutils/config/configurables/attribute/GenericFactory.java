package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Builds a {@code T} from a config key and the section it lives in.
 *
 * <p>Note what is received: the key, and the <em>enclosing</em> section — not the value at the key.
 * That is what lets an implementation look at {@code config.getString(key)},
 * {@code config.getConfigurationSection(key)}, or ignore the key entirely and read sibling keys
 * instead.</p>
 *
 * @param <T> the type this factory builds
 */
@FunctionalInterface
public interface GenericFactory<T> {
    @NotNull T create(@NotNull String key, @NotNull ConfigurationSection config);
}
