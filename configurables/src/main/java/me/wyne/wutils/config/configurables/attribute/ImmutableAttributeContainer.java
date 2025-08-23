package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class ImmutableAttributeContainer extends AttributeContainerBase {

    public ImmutableAttributeContainer() {
        super();
    }

    public ImmutableAttributeContainer(AttributeMap attributeMap) {
        super(attributeMap);
    }

    public ImmutableAttributeContainer(Map<String, Attribute<?>> attributes) {
        super(attributes);
    }

    public ImmutableAttributeContainer(AttributeMap attributeMap, Map<String, Attribute<?>> attributes) {
        super(attributeMap, attributes);
    }

    public ImmutableAttributeContainer(AttributeMap attributeMap, ConfigurationSection config) {
        super(attributeMap, config);
    }

    public ImmutableAttributeContainer(AttributeContainer container) {
        super(container);
    }

    @Override
    public AttributeContainer ignore(String... ignore) {
        ImmutableAttributeContainer container = new ImmutableAttributeContainer(this);
        for (String ignoreKey : ignore)
            container.getAttributes().remove(ignoreKey);
        return container;
    }

    @Override
    public AttributeContainer with(String key, AttributeFactory factory) {
        ImmutableAttributeContainer container = new ImmutableAttributeContainer(this);
        container.getAttributeMap().put(key, factory);
        return container;
    }

    @Override
    public AttributeContainer with(Map<String, AttributeFactory> keyMap) {
        ImmutableAttributeContainer container = new ImmutableAttributeContainer(this);
        container.getAttributeMap().putAll(keyMap);
        return container;
    }

    @Override
    public AttributeContainer with(Attribute<?> attribute) {
        ImmutableAttributeContainer container = new ImmutableAttributeContainer(this);
        container.getAttributes().put(attribute.getKey(), attribute);
        return container;
    }

    @Override
    public AttributeContainer with(AttributeContainer container) {
        ImmutableAttributeContainer newContainer = new ImmutableAttributeContainer(this);
        newContainer.getAttributeMap().putAll(container.getAttributeMap().getKeyMap());
        newContainer.getAttributes().putAll(container.getAttributes());
        return newContainer;
    }

    @Override
    public AttributeContainer copy(AttributeContainer container) {
        return new ImmutableAttributeContainer(container);
    }

    @Override
    public AttributeContainer copy() {
        return new ImmutableAttributeContainer(this);
    }

    public static AttributeContainerBuilder builder() {
        return new AttributeContainerBuilder();
    }

}
