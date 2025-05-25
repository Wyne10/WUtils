package me.wyne.wutils.config.configurables.attribute;

public class PrimitiveConfigurableAttribute<V> extends AttributeBase<V> implements ConfigurableAttribute<V> {

    public PrimitiveConfigurableAttribute(String key, V value) {
        super(key, value);
    }

}
