package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;

public class PrimitiveAttribute<V> extends AttributeBase<V> {

    public PrimitiveAttribute(String key, V value) {
        super(key, value);
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public PrimitiveAttribute<?> create(String key, ConfigurationSection config) {
            return new PrimitiveAttribute<>(key, config.get(key));
        }
    }

}
