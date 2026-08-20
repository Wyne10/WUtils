package me.wyne.wutils.config.configurables.attribute;

import org.jetbrains.annotations.NotNull;

/**
 * A single named piece of configuration state, resolved from a YAML key by an {@link AttributeMap}
 * and held inside an {@link AttributeContainer}.
 *
 * <p>An attribute's behaviour beyond carrying a value comes from the interfaces it additionally
 * implements — {@link ConfigurableAttribute} to render back into YAML, or a module-specific
 * behaviour interface such as {@code ItemStackAttribute} to act on something.</p>
 *
 * @param <V> the value type
 */
public interface Attribute<V> {
    /**
     * Returns the config key this attribute was resolved under.
     */
    @NotNull String getKey();

    /**
     * Returns the attribute's value, which is never {@code null}.
     *
     * <p>An attribute exists only when its config key does. A value that cannot be resolved — a
     * material name that matches nothing, an unknown enum constant — is a config error, and the
     * factory rejects it at load with the offending path rather than building an attribute around
     * {@code null}. "Not set" is expressed by the attribute being absent, not by a null value.</p>
     */
    @NotNull V getValue();
}
