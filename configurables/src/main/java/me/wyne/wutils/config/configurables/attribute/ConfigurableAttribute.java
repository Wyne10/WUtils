package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigBuilder;

public abstract class ConfigurableAttribute<V> extends AttributeBase<V> implements CompositeConfigSerializable {

    public ConfigurableAttribute(String key, V value) {
        super(key, value);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue()).buildNoSpace();
    }

}
