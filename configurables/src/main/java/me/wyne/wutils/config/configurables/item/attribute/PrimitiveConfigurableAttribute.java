package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.AttributeBase;
import me.wyne.wutils.config.configurables.item.ConfigurableAttribute;

public class PrimitiveAttribute<V> extends AttributeBase<V> implements ConfigurableAttribute<V> {

    public PrimitiveAttribute(String key, V value) {
        super(key, value);
    }

}
