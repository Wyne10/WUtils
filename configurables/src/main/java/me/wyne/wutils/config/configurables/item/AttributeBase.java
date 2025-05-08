package me.wyne.wutils.config.configurables.item;

public abstract class AttributeBase<V> implements Attribute<V> {

    private final String key;
    private final V value;

    public AttributeBase(String key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

}
