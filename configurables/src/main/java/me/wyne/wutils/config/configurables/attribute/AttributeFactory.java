package me.wyne.wutils.config.configurables.attribute;

@FunctionalInterface
public interface AttributeFactory<T extends Attribute<?>> extends GenericFactory<T> {}
