package me.wyne.wutils.config.configurables.attribute;

/**
 * A {@link GenericFactory} narrowed to build {@link Attribute}s, registered against a key in an
 * {@link AttributeMap}.
 *
 * @param <T> the attribute type this factory builds
 */
@FunctionalInterface
public interface AttributeFactory<T extends Attribute<?>> extends GenericFactory<T> {}
