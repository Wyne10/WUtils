package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;

public class PrimitiveConfigurableAttribute<V> extends AttributeBase<V> implements ConfigurableAttribute<V> {

    public PrimitiveConfigurableAttribute(String key, V value) {
        super(key, value);
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public ConfigurableAttribute<?> create(String key, ConfigurationSection config) {
            return new PrimitiveConfigurableAttribute<>(key, config.get(key));
        }
    }

}
