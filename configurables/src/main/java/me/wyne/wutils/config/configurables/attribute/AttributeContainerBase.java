package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.config.ConfigEntry;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AttributeContainerBase implements AttributeContainer {

    private final AttributeMap attributeMap;
    private final Map<String, Attribute<?>> attributes;

    public AttributeContainerBase() {
        this.attributeMap = new AttributeMap(new LinkedHashMap<>());
        this.attributes = new LinkedHashMap<>();
    }

    public AttributeContainerBase(AttributeMap attributeMap) {
        this.attributeMap = new AttributeMap(attributeMap.getKeyMap());
        this.attributes = new LinkedHashMap<>();
    }

    public AttributeContainerBase(Map<String, Attribute<?>> attributes) {
        this.attributeMap = new AttributeMap(new LinkedHashMap<>());
        this.attributes = new LinkedHashMap<>(attributes);
    }

    public AttributeContainerBase(AttributeMap attributeMap, Map<String, Attribute<?>> attributes) {
        this.attributeMap = new AttributeMap(attributeMap.getKeyMap());
        this.attributes = new LinkedHashMap<>(attributes);
    }

    public AttributeContainerBase(AttributeMap attributeMap, ConfigurationSection config) {
        this.attributeMap = new AttributeMap(attributeMap.getKeyMap());
        this.attributes = new LinkedHashMap<>();
        fromConfig(config);
    }

    public AttributeContainerBase(AttributeContainer container) {
        this.attributeMap = new AttributeMap(container.getAttributeMap().getKeyMap());
        this.attributes = new LinkedHashMap<>(container.getAttributes());
    }

    @Override
    public <T> @Nullable T get(Class<T> clazz) {
        return get(clazz, null);
    }

    @Override
    public @Nullable <T> T get(String key) {
        return get(key, null);
    }

    @Override
    public <T> T get(Class<T> clazz, T def) {
        return attributes.values().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst().orElse(def);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, T def) {
        T value = (T) attributes.get(key);
        if (value == null)
            return def;
        return value;
    }

    @Override
    public <T> Set<T> getSet(Class<T> clazz) {
        return attributes.values().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public @Nullable <T, V> Attribute<V> getAttribute(Class<T> clazz) {
        return getAttribute(clazz, null);
    }

    @Override
    public @Nullable <V> Attribute<V> getAttribute(String key) {
        return getAttribute(key, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, V> Attribute<V> getAttribute(Class<T> clazz, Attribute<V> def) {
        return attributes.values().stream()
                .filter(clazz::isInstance)
                .map(Attribute.class::cast)
                .findFirst().orElse(def);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Attribute<V> getAttribute(String key, Attribute<V> def) {
        var attribute = (Attribute<V>) attributes.get(key);
        if (attribute == null)
            return def;
        return attribute;
    }

    @Override
    public <V> Set<Attribute<V>> getAttributes(Class<Attribute<V>> clazz) {
        return attributes.values().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public <T, V> @Nullable V getValue(Class<T> clazz) {
        return getValue(clazz, null);
    }

    @Override
    public @Nullable <V> V getValue(String key) {
        return getValue(key, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, V> V getValue(Class<T> clazz, V def) {
        return (V) attributes.values().stream()
                .filter(clazz::isInstance)
                .map(Attribute.class::cast)
                .findFirst().map(Attribute::getValue).orElse(def);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> V getValue(String key, V def) {
        var attribute = (Attribute<V>) attributes.get(key);
        if (attribute == null)
            return def;
        return attribute.getValue();
    }

    @Override
    public <V> Set<V> getValues(Class<V> clazz) {
        return attributes.values().stream()
                .filter(attribute -> clazz.isInstance(attribute.getValue()))
                .map(attribute -> clazz.cast(attribute.getValue()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Map<String, Attribute<?>> getAttributes() {
        return attributes;
    }

    @Override
    public AttributeMap getAttributeMap() {
        return attributeMap;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        getConfigurableAttributes()
                .forEach(attribute -> builder.append(attribute.toConfig(depth, configEntry)));
        if (builder.charAt(builder.length() - 1) == '\n')
            builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        attributes.clear();
        if (configObject == null)
            return;
        attributes.putAll(attributeMap.createAllMap((ConfigurationSection) configObject));
    }

    @SuppressWarnings("rawtypes")
    private Set<ConfigurableAttribute> getConfigurableAttributes() {
        return getSet(ConfigurableAttribute.class);
    }

}
