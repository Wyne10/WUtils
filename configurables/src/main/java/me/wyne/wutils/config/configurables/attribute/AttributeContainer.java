package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.config.configurable.CompositeConfigurable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public interface AttributeContainer extends CompositeConfigurable {
    AttributeContainer ignore(String... ignore);

    AttributeContainer with(String key, AttributeFactory<?> factory);

    AttributeContainer with(Map<String, AttributeFactory<?>> keyMap);

    AttributeContainer with(Attribute<?> attribute);

    AttributeContainer with(AttributeContainer container);

    AttributeContainer copy(AttributeContainer container);

    AttributeContainer copy();

    @Nullable <T> T get(Class<T> clazz);

    @Nullable <T> T get(String key);

    <T> T get(Class<T> clazz, T def);

    <T> T get(String key, T def);

    <T> Set<T> getSet(Class<T> clazz);

    @Nullable <T, V> Attribute<V> getAttribute(Class<T> clazz);

    @Nullable <V> Attribute<V> getAttribute(String key);

    <T, V> Attribute<V> getAttribute(Class<T> clazz, Attribute<V> def);

    <V> Attribute<V> getAttribute(String key, Attribute<V> def);

    <V> Set<Attribute<V>> getAttributes(Class<Attribute<V>> clazz);

    @Nullable <T, V> V getValue(Class<T> clazz);

    @Nullable <V> V getValue(String key);

    <T, V> V getValue(Class<T> clazz, V def);

    <V> V getValue(String key, V def);

    <V> Set<V> getValues(Class<V> clazz);

    AttributeMap getAttributeMap();

    Map<String, Attribute<?>> getAttributes();

    default AttributeContainerBuilder toBuilder() {
        return new AttributeContainerBuilder(this);
    }
}
