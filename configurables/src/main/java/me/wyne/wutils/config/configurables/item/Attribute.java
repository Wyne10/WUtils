package me.wyne.wutils.config.configurables.item;

public interface Attribute<V> {
    String getKey();

    V getValue();
}
