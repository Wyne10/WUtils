package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.Attribute;
import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ImmutableAttributeContainer;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImmutableGuiConfigurable extends GuiConfigurableBase {

    public ImmutableGuiConfigurable() {
        super(new ImmutableAttributeContainer(GUI_ITEM_ATTRIBUTE_MAP, new LinkedHashMap<>()));
    }

    public ImmutableGuiConfigurable(ConfigurationSection section) {
        this();
        fromConfig(section);
    }

    public ImmutableGuiConfigurable(AttributeContainer attributeContainer) {
        super(attributeContainer);
    }

    public ImmutableGuiConfigurable(AttributeContainer attributeContainer, ConfigurationSection section) {
        super(attributeContainer, section);
    }

    public ImmutableGuiConfigurable ignore(String... ignore) {
        return new ImmutableGuiConfigurable(getAttributeContainer().ignore(ignore));
    }

    public ImmutableGuiConfigurable ignore(ItemAttribute... ignore) {
        return new ImmutableGuiConfigurable(getAttributeContainer().ignore(Arrays.stream(ignore).map(ItemAttribute::getKey).toArray(String[]::new)));
    }

    public ImmutableGuiConfigurable ignore(GuiItemAttribute... ignore) {
        return new ImmutableGuiConfigurable(getAttributeContainer().ignore(Arrays.stream(ignore).map(GuiItemAttribute::getKey).toArray(String[]::new)));
    }

    public ImmutableGuiConfigurable with(String key, AttributeFactory factory) {
        return new ImmutableGuiConfigurable(getAttributeContainer().with(key, factory));
    }

    public ImmutableGuiConfigurable with(ItemAttribute key, AttributeFactory factory) {
        return new ImmutableGuiConfigurable(getAttributeContainer().with(key.getKey(), factory));
    }

    public ImmutableGuiConfigurable with(GuiItemAttribute key, AttributeFactory factory) {
        return new ImmutableGuiConfigurable(getAttributeContainer().with(key.getKey(), factory));
    }

    public ImmutableGuiConfigurable with(Map<String, AttributeFactory> keyMap) {
        return new ImmutableGuiConfigurable(getAttributeContainer().with(keyMap));
    }

    public ImmutableGuiConfigurable with(Attribute<?> attribute) {
        return new ImmutableGuiConfigurable(getAttributeContainer().with(attribute));
    }

    public ImmutableGuiConfigurable with(AttributeContainer container) {
        return new ImmutableGuiConfigurable(getAttributeContainer().with(container));
    }

    public ImmutableGuiConfigurable with(ItemConfigurableBase itemConfigurable) {
        return new ImmutableGuiConfigurable(getAttributeContainer().with(itemConfigurable.getAttributeContainer()));
    }

    public ImmutableGuiConfigurable copy(AttributeContainer container) {
        return new ImmutableGuiConfigurable(getAttributeContainer().copy(container));
    }

    public ImmutableGuiConfigurable copy(ItemConfigurableBase itemConfigurable) {
        return new ImmutableGuiConfigurable(getAttributeContainer().copy(itemConfigurable.getAttributeContainer()));
    }

    public ImmutableGuiConfigurable copy() {
        return new ImmutableGuiConfigurable(getAttributeContainer().copy());
    }
    
    public static GuiConfigurableBuilder guiBuilder() {
        return new GuiConfigurableBuilder();
    }

}
