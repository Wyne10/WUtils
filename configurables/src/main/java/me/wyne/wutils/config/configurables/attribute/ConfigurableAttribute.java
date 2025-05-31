package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigurableAttribute<V> extends AttributeBase<V> implements CompositeConfigurable {

    public ConfigurableAttribute(String key, V value) {
        super(key, value);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue()).buildNoSpace();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        throw new NotImplementedException();
    }

}
