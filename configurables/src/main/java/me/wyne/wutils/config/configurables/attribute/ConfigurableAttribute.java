package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link Attribute} that can render itself back into generated YAML as {@code key: value}.
 *
 * <p>Only {@code ConfigurableAttribute}s are collected by {@link AttributeContainerBase#toConfig};
 * a plain {@link AttributeBase} is invisible to config generation by design. The default
 * {@link #toConfig(int, ConfigEntry)} renders through {@link ConfigBuilder#append(int, String, Object)},
 * which is enough for primitive values — attributes whose value is a record or a Bukkit object
 * (see {@link me.wyne.wutils.config.configurables.attribute.common.SoundAttribute}) override it.</p>
 *
 * @param <V> the value type
 */
public abstract class ConfigurableAttribute<V> extends AttributeBase<V> implements CompositeConfigSerializable {

    public ConfigurableAttribute(@NotNull String key, @NotNull V value) {
        super(key, value);
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue()).buildNoSpace();
    }

}
