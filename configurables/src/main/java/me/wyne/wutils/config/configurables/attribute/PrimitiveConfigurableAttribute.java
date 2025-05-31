package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;

public class PrimitiveConfigurableAttribute<V> extends ConfigurableAttribute<V> {

    public PrimitiveConfigurableAttribute(String key, V value) {
        super(key, value);
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public PrimitiveConfigurableAttribute<?> create(String key, ConfigurationSection config) {
            return new PrimitiveConfigurableAttribute<>(key, config.get(key));
        }
    }

}
