package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.Attribute;
import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ImmutableAttributeContainer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class ImmutableAttributeConfigurable extends AttributeConfigurableBase {

    public ImmutableAttributeConfigurable() {
        super(new ImmutableAttributeContainer());
    }

    public ImmutableAttributeConfigurable(ConfigurationSection section) {
        this();
        fromConfig(section);
    }

    public ImmutableAttributeConfigurable(AttributeContainer attributeContainer) {
        super(attributeContainer);
    }

    public ImmutableAttributeConfigurable(AttributeContainer attributeContainer, ConfigurationSection section) {
        super(attributeContainer, section);
    }

    public ImmutableAttributeConfigurable ignore(String... ignore) {
        return new ImmutableAttributeConfigurable(getAttributeContainer().ignore(ignore));
    }

    public ImmutableAttributeConfigurable with(String key, AttributeFactory factory) {
        return new ImmutableAttributeConfigurable(getAttributeContainer().with(key, factory));
    }

    public ImmutableAttributeConfigurable with(Map<String, AttributeFactory> keyMap) {
        return new ImmutableAttributeConfigurable(getAttributeContainer().with(keyMap));
    }

    public ImmutableAttributeConfigurable with(Attribute<?> attribute) {
        return new ImmutableAttributeConfigurable(getAttributeContainer().with(attribute));
    }

    public ImmutableAttributeConfigurable with(AttributeContainer container) {
        return new ImmutableAttributeConfigurable(getAttributeContainer().with(container));
    }

    public ImmutableAttributeConfigurable with(AttributeConfigurableBase attributeConfigurable) {
        return new ImmutableAttributeConfigurable(getAttributeContainer().with(attributeConfigurable.getAttributeContainer()));
    }

    public ImmutableAttributeConfigurable copy(AttributeContainer container) {
        return new ImmutableAttributeConfigurable(getAttributeContainer().copy(container));
    }

    public ImmutableAttributeConfigurable copy(AttributeConfigurableBase attributeConfigurable) {
        return new ImmutableAttributeConfigurable(getAttributeContainer().copy(attributeConfigurable.getAttributeContainer()));
    }

    public ImmutableAttributeConfigurable copy() {
        return new ImmutableAttributeConfigurable(getAttributeContainer().copy());
    }

}
