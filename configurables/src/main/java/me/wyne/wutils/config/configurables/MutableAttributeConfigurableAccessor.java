package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.Attribute;
import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;

import java.util.Map;

public class MutableAttributeConfigurableAccessor<T extends AttributeConfigurable> implements AttributeConfigurableAccessor<T> {

    private final T attributeConfigurable;

    public MutableAttributeConfigurableAccessor(T attributeConfigurable) {
        this.attributeConfigurable = attributeConfigurable;
    }

    @Override
    public T ignore(String... ignore) {
        attributeConfigurable.getAttributeContainer().ignore(ignore);
        return attributeConfigurable;
    }

    @Override
    public T with(String key, AttributeFactory factory) {
        attributeConfigurable.getAttributeContainer().with(key, factory);
        return attributeConfigurable;
    }

    @Override
    public T with(Map<String, AttributeFactory> keyMap) {
        attributeConfigurable.getAttributeContainer().with(keyMap);
        return attributeConfigurable;
    }

    @Override
    public T with(Attribute<?> attribute) {
        attributeConfigurable.getAttributeContainer().with(attribute);
        return attributeConfigurable;
    }

    @Override
    public T with(AttributeContainer container) {
        attributeConfigurable.getAttributeContainer().with(container);
        return attributeConfigurable;
    }

    @Override
    public T copy(AttributeContainer container) {
        attributeConfigurable.getAttributeContainer().copy(container);
        return attributeConfigurable;
    }

    @Override
    public T copy() {
        attributeConfigurable.getAttributeContainer().copy();
        return attributeConfigurable;
    }

}
