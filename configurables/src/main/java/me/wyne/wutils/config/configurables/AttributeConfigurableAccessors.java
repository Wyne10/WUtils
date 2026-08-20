package me.wyne.wutils.config.configurables;

/**
 * Selects which kind of {@link AttributeConfigurableAccessor} {@link AttributeConfigurable#getAccessor}
 * returns: a copying accessor backed by {@link ImmutableAttributeConfigurableAccessor}, or an
 * in-place one backed by {@link MutableAttributeConfigurableAccessor}.
 */
public enum AttributeConfigurableAccessors {
    IMMUTABLE,
    MUTABLE
}
