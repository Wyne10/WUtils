package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.*;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;

import java.util.LinkedHashMap;
import java.util.Map;

import static me.wyne.wutils.config.configurables.GuiConfigurableBase.GUI_ITEM_ATTRIBUTE_MAP;

public class GuiConfigurableBuilder {

    private final AttributeContainerBuilder attributeContainerBuilder;

    public GuiConfigurableBuilder() {
        this.attributeContainerBuilder = new ImmutableAttributeContainer(GUI_ITEM_ATTRIBUTE_MAP, new LinkedHashMap<>()).toBuilder();
    }

    public GuiConfigurableBuilder(AttributeContainer attributeContainer) {
        this.attributeContainerBuilder = attributeContainer.toBuilder();
    }

    public GuiConfigurableBuilder(ItemConfigurableBase itemConfigurable) {
        this.attributeContainerBuilder = itemConfigurable.getAttributeContainer().toBuilder();
    }

    public GuiConfigurableBuilder ignore(String... ignore) {
        attributeContainerBuilder.ignore(ignore);
        return this;
    }

    public GuiConfigurableBuilder ignore(ItemAttribute... ignore) {
        for (ItemAttribute ignoreAttribute : ignore)
            attributeContainerBuilder.ignore(ignoreAttribute.getKey());
        return this;
    }

    public GuiConfigurableBuilder ignore(GuiItemAttribute... ignore) {
        for (GuiItemAttribute ignoreAttribute : ignore)
            attributeContainerBuilder.ignore(ignoreAttribute.getKey());
        return this;
    }

    public GuiConfigurableBuilder with(String key, AttributeFactory factory) {
        attributeContainerBuilder.with(key, factory);
        return this;
    }

    public GuiConfigurableBuilder with(ItemAttribute key, AttributeFactory factory) {
        attributeContainerBuilder.with(key.getKey(), factory);
        return this;
    }

    public GuiConfigurableBuilder with(GuiItemAttribute key, AttributeFactory factory) {
        attributeContainerBuilder.with(key.getKey(), factory);
        return this;
    }

    public GuiConfigurableBuilder with(Map<String, AttributeFactory> keyMap) {
        attributeContainerBuilder.with(keyMap);
        return this;
    }

    public GuiConfigurableBuilder with(Attribute<?> attribute) {
        attributeContainerBuilder.with(attribute);
        return this;
    }

    public GuiConfigurableBuilder with(AttributeContainer container) {
        attributeContainerBuilder.with(container);
        return this;
    }

    public GuiConfigurableBuilder with(ItemConfigurableBase itemConfigurable) {
        attributeContainerBuilder.with(itemConfigurable.getAttributeContainer());
        return this;
    }

    public GuiConfigurableBuilder copy(AttributeContainer container) {
        attributeContainerBuilder.copy(container);
        return this;
    }

    public GuiConfigurableBuilder copy(ItemConfigurableBase itemConfigurable) {
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
