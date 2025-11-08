package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.Attribute;
import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ImmutableAttributeConfigurableAccessor<T extends AttributeConfigurable> implements AttributeConfigurableAccessor<T> {

    private final T attributeConfigurable;
    private final Constructor<? extends AttributeConfigurable> constructor;

    public ImmutableAttributeConfigurableAccessor(T attributeConfigurable) {
        this.attributeConfigurable = attributeConfigurable;
        try {
            this.constructor = attributeConfigurable.getClass().getConstructor(AttributeContainer.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Configurable must contain constructor with AttributeContainer parameter in order to use immutable attribute configurable accessor", e);
        }
    }

    @Override
    public T ignore(String... ignore) {
        return create(attributeConfigurable.getAttributeContainer().ignore(ignore));
    }

    @Override
    public T with(String key, AttributeFactory factory) {
        return create(attributeConfigurable.getAttributeContainer().with(key, factory));
    }

    @Override
    public T with(Map<String, AttributeFactory> keyMap) {
        return create(attributeConfigurable.getAttributeContainer().with(keyMap));
    }

    @Override
    public T with(Attribute<?> attribute) {
        return create(attributeConfigurable.getAttributeContainer().with(attribute));
    }

    @Override
    public T with(AttributeContainer container) {
        return create(attributeConfigurable.getAttributeContainer().with(container));
    }

    @Override
    public T copy(AttributeContainer container) {
        return create(attributeConfigurable.getAttributeContainer().copy(container));
    }

    @Override
    public T copy() {
        return create(attributeConfigurable.getAttributeContainer().copy());
    }

    private T create(AttributeContainer container) {
        try {
            return (T) constructor.newInstance(container);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Configurable must contain constructor with AttributeContainer parameter in order to use immutable attribute configurable accessor", e);
        }
    }

}
