package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.Attribute;
import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;

import java.util.Map;

@SuppressWarnings("unchecked")
public class ImmutableAttributeConfigurableAccessor<T extends AttributeConfigurable> implements AttributeConfigurableAccessor<T> {

    private final T attributeConfigurable;

    public ImmutableAttributeConfigurableAccessor(T attributeConfigurable) {
        this.attributeConfigurable = attributeConfigurable;
    }

    @Override
    public T ignore(String... ignore) {
        return (T) new AttributeConfigurable(attributeConfigurable.getAttributeContainer().ignore(ignore));
    }

    @Override
    public T with(String key, AttributeFactory factory) {
        return (T) new AttributeConfigurable(attributeConfigurable.getAttributeContainer().with(key, factory));
    }

    @Override
    public T with(Map<String, AttributeFactory> keyMap) {
        return (T) new AttributeConfigurable(attributeConfigurable.getAttributeContainer().with(keyMap));
    }

    @Override
    public T with(Attribute<?> attribute) {
        return (T) new AttributeConfigurable(attributeConfigurable.getAttributeContainer().with(attribute));
    }

    @Override
    public T with(AttributeContainer container) {
        return (T) new AttributeConfigurable(attributeConfigurable.getAttributeContainer().with(container));
    }

    @Override
    public T copy(AttributeContainer container) {
        return (T) new AttributeConfigurable(attributeConfigurable.getAttributeContainer().copy(container));
    }

    @Override
    public T copy() {
        return (T) new AttributeConfigurable(attributeConfigurable.getAttributeContainer().copy());
    }

}
