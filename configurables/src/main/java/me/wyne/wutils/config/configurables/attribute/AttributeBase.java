package me.wyne.wutils.config.configurables.attribute;

import org.jetbrains.annotations.NotNull;

/**
 * Final-field implementation of {@link Attribute}, used directly by attributes that are read-only
 * and never rendered back into generated config — {@link me.wyne.wutils.config.configurables.attribute.common.RootAttribute},
 * {@link me.wyne.wutils.config.configurables.attribute.common.PrimitiveAttribute} and similar.
 * Attributes that also need to serialize themselves extend {@link ConfigurableAttribute} instead.
 *
 * @param <V> the value type
 */
public abstract class AttributeBase<V> implements Attribute<V> {

    private final String key;
    private final V value;

    public AttributeBase(@NotNull String key, @NotNull V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public @NotNull String getKey() {
        return key;
    }

    @Override
    public @NotNull V getValue() {
        return value;
    }

}
