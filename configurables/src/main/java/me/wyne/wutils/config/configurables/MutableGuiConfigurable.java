package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.Attribute;
import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.MutableAttributeContainer;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static me.wyne.wutils.config.configurables.GuiConfigurableBase.GUI_ITEM_ATTRIBUTE_MAP;

public class MutableGuiConfigurable extends ItemConfigurableBase {

    public MutableGuiConfigurable() {
        super(new MutableAttributeContainer(GUI_ITEM_ATTRIBUTE_MAP, new LinkedHashMap<>()));
    }

    public MutableGuiConfigurable(ConfigurationSection section) {
        this();
        fromConfig(section);
    }

    public MutableGuiConfigurable(AttributeContainer attributeContainer) {
        super(attributeContainer);
    }

    public MutableGuiConfigurable(AttributeContainer attributeContainer, ConfigurationSection section) {
        super(attributeContainer, section);
    }

    public MutableGuiConfigurable ignore(String... ignore) {
        getAttributeContainer().ignore(ignore);
        return this;
    }

    public MutableGuiConfigurable ignore(ItemAttribute... ignore) {
        getAttributeContainer().ignore(Arrays.stream(ignore).map(ItemAttribute::getKey).toArray(String[]::new));
        return this;
    }

    public MutableGuiConfigurable ignore(GuiItemAttribute... ignore) {
        getAttributeContainer().ignore(Arrays.stream(ignore).map(GuiItemAttribute::getKey).toArray(String[]::new));
        return this;
    }

    public MutableGuiConfigurable with(String key, AttributeFactory factory) {
        getAttributeContainer().with(key, factory);
        return this;
    }

    public MutableGuiConfigurable with(ItemAttribute key, AttributeFactory factory) {
        getAttributeContainer().with(key.getKey(), factory);
        return this;
    }

    public MutableGuiConfigurable with(GuiItemAttribute key, AttributeFactory factory) {
        getAttributeContainer().with(key.getKey(), factory);
        return this;
    }

    public MutableGuiConfigurable with(Map<String, AttributeFactory> keyMap) {
        getAttributeContainer().with(keyMap);
        return this;
    }

    public MutableGuiConfigurable with(Attribute<?> attribute) {
        getAttributeContainer().with(attribute);
        return this;
    }

    public MutableGuiConfigurable with(AttributeContainer container) {
        getAttributeContainer().with(container);
        return this;
    }

    public MutableGuiConfigurable with(ItemConfigurableBase itemConfigurable) {
        getAttributeContainer().with(itemConfigurable.getAttributeContainer());
        return this;
    }

    public MutableGuiConfigurable copy(AttributeContainer container) {
        getAttributeContainer().copy(container);
        return this;
    }

    public MutableGuiConfigurable copy(ItemConfigurableBase itemConfigurable) {
        getAttributeContainer().copy(itemConfigurable.getAttributeContainer());
        return this;
    }

    public MutableGuiConfigurable copy() {
        return new MutableGuiConfigurable(getAttributeContainer().copy());
    }

    public static GuiConfigurableBuilder guiBuilder() {
        return new GuiConfigurableBuilder();
    }

}
