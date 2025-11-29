package me.wyne.wutils.config.configurables.attribute.common;

import me.wyne.wutils.config.configurables.attribute.AttributeBase;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import org.bukkit.configuration.ConfigurationSection;

public class PrimitiveAttribute<V> extends AttributeBase<V> {

    public PrimitiveAttribute(String key, V value) {
        super(key, value);
    }

    public static final class Factory implements AttributeFactory<PrimitiveAttribute<?>> {
        @Override
        public PrimitiveAttribute<?> create(String key, ConfigurationSection config) {
            return new PrimitiveAttribute<>(key, config.get(key));
        }
    }

}
