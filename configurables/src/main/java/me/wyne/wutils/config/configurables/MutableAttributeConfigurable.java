package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.Attribute;
import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class MutableAttributeConfigurable extends AttributeConfigurableBase {

    public MutableAttributeConfigurable(AttributeContainer attributeContainer) {
        super(attributeContainer);
    }

    public MutableAttributeConfigurable(AttributeContainer attributeContainer, ConfigurationSection section) {
        super(attributeContainer, section);
    }

    public MutableAttributeConfigurable ignore(String... ignore) {
        getAttributeContainer().ignore(ignore);
        return this;
    }

    public MutableAttributeConfigurable with(String key, AttributeFactory factory) {
        getAttributeContainer().with(key, factory);
        return this;
    }

    public MutableAttributeConfigurable with(Map<String, AttributeFactory> keyMap) {
        getAttributeContainer().with(keyMap);
        return this;
    }

    public MutableAttributeConfigurable with(Attribute<?> attribute) {
        getAttributeContainer().with(attribute);
        return this;
    }

    public MutableAttributeConfigurable with(AttributeContainer container) {
        getAttributeContainer().with(container);
        return this;
    }

    public MutableAttributeConfigurable with(MutableAttributeConfigurable attributeConfigurable) {
        getAttributeContainer().with(attributeConfigurable.getAttributeContainer());
        return this;
    }

    public MutableAttributeConfigurable copy(AttributeContainer container) {
        getAttributeContainer().copy(container);
        return this;
    }

    public MutableAttributeConfigurable copy(MutableAttributeConfigurable attributeConfigurable) {
        getAttributeContainer().copy(attributeConfigurable.getAttributeContainer());
        return this;
    }

    public MutableAttributeConfigurable copy() {
        return new MutableAttributeConfigurable(getAttributeContainer().copy());
    }

}
