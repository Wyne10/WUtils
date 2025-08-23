package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.*;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImmutableItemConfigurable extends ItemConfigurableBase {

    public ImmutableItemConfigurable() {
        super(new ImmutableAttributeContainer(ITEM_ATTRIBUTE_MAP, new LinkedHashMap<>()));
    }

    public ImmutableItemConfigurable(ConfigurationSection section) {
        this();
        fromConfig(section);
    }

    public ImmutableItemConfigurable(AttributeContainer attributeContainer) {
        super(attributeContainer);
    }

    public ImmutableItemConfigurable(AttributeContainer attributeContainer, ConfigurationSection section) {
        super(attributeContainer, section);
    }

    public ImmutableItemConfigurable ignore(String... ignore) {
        return new ImmutableItemConfigurable(getAttributeContainer().ignore(ignore));
    }

    public ImmutableItemConfigurable ignore(ItemAttribute... ignore) {
        return new ImmutableItemConfigurable(getAttributeContainer().ignore(Arrays.stream(ignore).map(ItemAttribute::getKey).toArray(String[]::new)));
    }

    public ImmutableItemConfigurable with(String key, AttributeFactory factory) {
        return new ImmutableItemConfigurable(getAttributeContainer().with(key, factory));
    }

    public ImmutableItemConfigurable with(ItemAttribute key, AttributeFactory factory) {
        return new ImmutableItemConfigurable(getAttributeContainer().with(key.getKey(), factory));
    }

    public ImmutableItemConfigurable with(Map<String, AttributeFactory> keyMap) {
        return new ImmutableItemConfigurable(getAttributeContainer().with(keyMap));
    }

    public ImmutableItemConfigurable with(Attribute<?> attribute) {
        return new ImmutableItemConfigurable(getAttributeContainer().with(attribute));
    }

    public ImmutableItemConfigurable with(AttributeContainer container) {
        return new ImmutableItemConfigurable(getAttributeContainer().with(container));
    }

    public ImmutableItemConfigurable with(ItemConfigurableBase itemConfigurable) {
        return new ImmutableItemConfigurable(getAttributeContainer().with(itemConfigurable.getAttributeContainer()));
    }

    public ImmutableItemConfigurable copy(AttributeContainer container) {
        return new ImmutableItemConfigurable(getAttributeContainer().copy(container));
    }

    public ImmutableItemConfigurable copy(ItemConfigurableBase itemConfigurable) {
        return new ImmutableItemConfigurable(getAttributeContainer().copy(itemConfigurable.getAttributeContainer()));
    }

    public ImmutableItemConfigurable copy() {
        return new ImmutableItemConfigurable(getAttributeContainer().copy());
    }
    
    public static ItemConfigurableBuilder builder() {
        return new ItemConfigurableBuilder();
    }

}
