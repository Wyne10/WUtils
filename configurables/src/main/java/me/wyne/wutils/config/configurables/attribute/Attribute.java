package me.wyne.wutils.config.configurables.attribute;

public interface Attribute<V> {
    String getKey();

    V getValue();
}
