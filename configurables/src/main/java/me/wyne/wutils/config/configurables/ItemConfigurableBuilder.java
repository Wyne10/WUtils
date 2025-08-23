package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.*;
import me.wyne.wutils.config.configurables.item.ItemAttribute;

import java.util.LinkedHashMap;
import java.util.Map;

import static me.wyne.wutils.config.configurables.ItemConfigurableBase.ITEM_ATTRIBUTE_MAP;

public class ItemConfigurableBuilder {

    private final AttributeContainerBuilder attributeContainerBuilder;

    public ItemConfigurableBuilder() {
        this.attributeContainerBuilder = new ImmutableAttributeContainer(ITEM_ATTRIBUTE_MAP, new LinkedHashMap<>()).toBuilder();
    }

    public ItemConfigurableBuilder(AttributeContainer attributeContainer) {
        this.attributeContainerBuilder = attributeContainer.toBuilder();
    }

    public ItemConfigurableBuilder(ItemConfigurableBase itemConfigurable) {
        this.attributeContainerBuilder = itemConfigurable.getAttributeContainer().toBuilder();
    }

    public ItemConfigurableBuilder ignore(String... ignore) {
        attributeContainerBuilder.ignore(ignore);
        return this;
    }

    public ItemConfigurableBuilder ignore(ItemAttribute... ignore) {
        for (ItemAttribute ignoreAttribute : ignore)
            attributeContainerBuilder.ignore(ignoreAttribute.getKey());
        return this;
    }

    public ItemConfigurableBuilder with(String key, AttributeFactory factory) {
        attributeContainerBuilder.with(key, factory);
        return this;
    }

    public ItemConfigurableBuilder with(ItemAttribute key, AttributeFactory factory) {
        attributeContainerBuilder.with(key.getKey(), factory);
        return this;
    }

    public ItemConfigurableBuilder with(Map<String, AttributeFactory> keyMap) {
        attributeContainerBuilder.with(keyMap);
        return this;
    }

    public ItemConfigurableBuilder with(Attribute<?> attribute) {
        attributeContainerBuilder.with(attribute);
        return this;
    }

    public ItemConfigurableBuilder with(AttributeContainer container) {
        attributeContainerBuilder.with(container);
        return this;
    }

    public ItemConfigurableBuilder with(ItemConfigurableBase itemConfigurable) {
        attributeContainerBuilder.with(itemConfigurable.getAttributeContainer());
        return this;
    }

    public ItemConfigurableBuilder copy(AttributeContainer container) {
        attributeContainerBuilder.copy(container);
        return this;
    }

    public ItemConfigurableBuilder copy(ItemConfigurableBase itemConfigurable) {
        attributeContainerBuilder.copy(itemConfigurable.getAttributeContainer());
        return this;
    }

    public MutableItemConfigurable build() {
        return new MutableItemConfigurable(attributeContainerBuilder.build());
    }

    public ImmutableItemConfigurable buildImmutable() {
        return new ImmutableItemConfigurable(attributeContainerBuilder.buildImmutable());
    }

}
