package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.*;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class MutableItemConfigurable extends ItemConfigurableBase {

    public MutableItemConfigurable() {
        super(new MutableAttributeContainer(ITEM_ATTRIBUTE_MAP, new LinkedHashMap<>()));
    }

    public MutableItemConfigurable(ConfigurationSection section) {
        this();
        fromConfig(section);
    }

    public MutableItemConfigurable(AttributeContainer attributeContainer) {
        super(attributeContainer);
    }

    public MutableItemConfigurable(AttributeContainer attributeContainer, ConfigurationSection section) {
        super(attributeContainer, section);
    }

    public MutableItemConfigurable ignore(String... ignore) {
        getAttributeContainer().ignore(ignore);
        return this;
    }

    public MutableItemConfigurable ignore(ItemAttribute... ignore) {
        getAttributeContainer().ignore(Arrays.stream(ignore).map(ItemAttribute::getKey).toArray(String[]::new));
        return this;
    }

    public MutableItemConfigurable with(String key, AttributeFactory factory) {
        getAttributeContainer().with(key, factory);
        return this;
    }

    public MutableItemConfigurable with(ItemAttribute key, AttributeFactory factory) {
        getAttributeContainer().with(key.getKey(), factory);
        return this;
    }

    public MutableItemConfigurable with(Map<String, AttributeFactory> keyMap) {
        getAttributeContainer().with(keyMap);
        return this;
    }

    public MutableItemConfigurable with(Attribute<?> attribute) {
        getAttributeContainer().with(attribute);
        return this;
    }

    public MutableItemConfigurable with(AttributeContainer container) {
        getAttributeContainer().with(container);
        return this;
    }

    public MutableItemConfigurable with(ItemConfigurableBase itemConfigurable) {
        getAttributeContainer().with(itemConfigurable.getAttributeContainer());
        return this;
    }

    public MutableItemConfigurable copy(AttributeContainer container) {
        getAttributeContainer().copy(container);
        return this;
    }

    public MutableItemConfigurable copy(ItemConfigurableBase itemConfigurable) {
        getAttributeContainer().copy(itemConfigurable.getAttributeContainer());
        return this;
    }

    public MutableItemConfigurable copy() {
        return new MutableItemConfigurable(getAttributeContainer().copy());
    }
    
    public static ItemConfigurableBuilder builder() {
        return new ItemConfigurableBuilder();
    }

}
