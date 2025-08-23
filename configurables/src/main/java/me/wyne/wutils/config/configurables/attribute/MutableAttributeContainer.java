package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class MutableAttributeContainer extends AttributeContainerBase {

    public MutableAttributeContainer() {
        super();
    }

    public MutableAttributeContainer(AttributeMap attributeMap) {
        super(attributeMap);
    }

    public MutableAttributeContainer(Map<String, Attribute<?>> attributes) {
        super(attributes);
    }

    public MutableAttributeContainer(AttributeMap attributeMap, Map<String, Attribute<?>> attributes) {
        super(attributeMap, attributes);
    }

    public MutableAttributeContainer(AttributeMap attributeMap, ConfigurationSection config) {
        super(attributeMap, config);
    }

    public MutableAttributeContainer(AttributeContainer container) {
        super(container);
    }

    @Override
    public AttributeContainer ignore(String... ignore) {
        for (String ignoreKey : ignore)
            getAttributes().remove(ignoreKey);
        return this;
    }

    @Override
    public AttributeContainer with(String key, AttributeFactory factory) {
        getAttributeMap().put(key, factory);
        return this;
    }

    @Override
    public AttributeContainer with(Map<String, AttributeFactory> keyMap) {
        getAttributeMap().putAll(keyMap);
        return this;
    }

    @Override
    public AttributeContainer with(Attribute<?> attribute) {
        getAttributes().put(attribute.getKey(), attribute);
        return this;
    }

    @Override
    public AttributeContainer with(AttributeContainer container) {
        getAttributeMap().putAll(container.getAttributeMap().getKeyMap());
        getAttributes().putAll(container.getAttributes());
        return this;
    }

    @Override
    public AttributeContainer copy(AttributeContainer container) {
        return new MutableAttributeContainer(container);
    }

    @Override
    public AttributeContainer copy() {
        return new MutableAttributeContainer(this);
    }

    public static AttributeContainerBuilder builder() {
        return new AttributeContainerBuilder();
    }

}
