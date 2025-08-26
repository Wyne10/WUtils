package me.wyne.wutils.config.configurables.attribute;

import java.util.LinkedHashMap;
import java.util.Map;

public class AttributeContainerBuilder {

    private AttributeMap attributeMap;
    private Map<String, Attribute<?>> attributes;

    public AttributeContainerBuilder() {
        attributeMap = new AttributeMap(new LinkedHashMap<>());
        attributes = new LinkedHashMap<>();
    }

    public AttributeContainerBuilder(AttributeContainer attributeContainer) {
        this.attributeMap = new AttributeMap(attributeContainer.getAttributeMap().getKeyMap());
        this.attributes = new LinkedHashMap<>(attributeContainer.getAttributes());
    }

    public AttributeContainerBuilder ignore(String... ignore) {
        for (String ignoreKey : ignore)
            attributes.remove(ignoreKey);
        return this;
    }

    public AttributeContainerBuilder with(String key, AttributeFactory factory) {
        attributeMap.put(key, factory);
        return this;
    }

    public AttributeContainerBuilder with(Map<String, AttributeFactory> keyMap) {
        attributeMap.putAll(keyMap);
        return this;
    }

    public AttributeContainerBuilder with(AttributeMap attributeMap) {
        this.attributeMap.putAll(attributeMap.getKeyMap());
        return this;
    }

    public AttributeContainerBuilder with(Attribute<?> attribute) {
        attributes.put(attribute.getKey(), attribute);
        return this;
    }

    public AttributeContainerBuilder with(AttributeContainer container) {
        attributeMap.putAll(container.getAttributeMap().getKeyMap());
        attributes.putAll(container.getAttributes());
        return this;
    }

    public AttributeContainerBuilder copy(AttributeContainer container) {
        attributeMap = new AttributeMap(container.getAttributeMap().getKeyMap());
        attributes = new LinkedHashMap<>(container.getAttributes());
        return this;
    }

    public MutableAttributeContainer build() {
        return new MutableAttributeContainer(attributeMap, attributes);
    }

    public ImmutableAttributeContainer buildImmutable() {
        return new ImmutableAttributeContainer(attributeMap, attributes);
    }

}
