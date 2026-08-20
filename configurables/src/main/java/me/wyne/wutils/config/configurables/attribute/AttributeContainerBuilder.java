package me.wyne.wutils.config.configurables.attribute;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fluent front end for accumulating an attribute map and a set of attributes before producing either
 * an {@link ImmutableAttributeContainer} or a {@link MutableAttributeContainer} from the same
 * accumulated state.
 */
public class AttributeContainerBuilder {

    private AttributeMap attributeMap;
    private Map<String, Attribute<?>> attributes;

    public AttributeContainerBuilder() {
        attributeMap = new AttributeMap(new LinkedHashMap<>());
        attributes = new LinkedHashMap<>();
    }

    public AttributeContainerBuilder(@NotNull AttributeContainer attributeContainer) {
        this.attributeMap = new AttributeMap(attributeContainer.getAttributeMap().getKeyMap());
        this.attributes = new LinkedHashMap<>(attributeContainer.getAttributes());
    }

    public @NotNull AttributeContainerBuilder ignore(@NotNull String... ignore) {
        for (String ignoreKey : ignore)
            attributes.remove(ignoreKey);
        return this;
    }

    public @NotNull AttributeContainerBuilder with(@NotNull String key, @NotNull AttributeFactory<?> factory) {
        attributeMap.put(key, factory);
        return this;
    }

    public @NotNull AttributeContainerBuilder with(@NotNull Map<@NotNull String, @NotNull AttributeFactory<?>> keyMap) {
        attributeMap.putAll(keyMap);
        return this;
    }

    public @NotNull AttributeContainerBuilder with(@NotNull AttributeMap attributeMap) {
        this.attributeMap.putAll(attributeMap.getKeyMap());
        return this;
    }

    public @NotNull AttributeContainerBuilder with(@NotNull Attribute<?> attribute) {
        attributes.put(attribute.getKey(), attribute);
        return this;
    }

    public @NotNull AttributeContainerBuilder with(@NotNull AttributeContainer container) {
        attributeMap.putAll(container.getAttributeMap().getKeyMap());
        attributes.putAll(container.getAttributes());
        return this;
    }

    public @NotNull AttributeContainerBuilder copy(@NotNull AttributeContainer container) {
        attributeMap = new AttributeMap(container.getAttributeMap().getKeyMap());
        attributes = new LinkedHashMap<>(container.getAttributes());
        return this;
    }

    public @NotNull MutableAttributeContainer build() {
        return new MutableAttributeContainer(attributeMap, attributes);
    }

    public @NotNull ImmutableAttributeContainer buildImmutable() {
        return new ImmutableAttributeContainer(attributeMap, attributes);
    }

}
