package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class MutableAttributeContainer implements AttributeContainer {

    private final AttributeMap attributeMap;
    private final Map<String, Attribute<?>> attributes;

    public MutableAttributeContainer() {
        this.attributeMap = new AttributeMap(new LinkedHashMap<>());
        this.attributes = new LinkedHashMap<>();
    }

    public MutableAttributeContainer(AttributeMap attributeMap) {
        this.attributeMap = new AttributeMap(attributeMap.getKeyMap());
        this.attributes = new LinkedHashMap<>();
    }

    public MutableAttributeContainer(Map<String, Attribute<?>> attributes) {
        this.attributeMap = new AttributeMap(new LinkedHashMap<>());
        this.attributes = new LinkedHashMap<>(attributes);
    }

    public MutableAttributeContainer(AttributeMap attributeMap, Map<String, Attribute<?>> attributes) {
        this.attributeMap = new AttributeMap(attributeMap.getKeyMap());
        this.attributes = new LinkedHashMap<>(attributes);
    }

    public MutableAttributeContainer(AttributeMap attributeMap, ConfigurationSection config) {
        this.attributeMap = new AttributeMap(attributeMap.getKeyMap());
        this.attributes = new LinkedHashMap<>();
        fromConfig(config);
    }

    public MutableAttributeContainer(AttributeContainer container) {
        this.attributeMap = new AttributeMap(container.getAttributeMap().getKeyMap());
        this.attributes = new LinkedHashMap<>(container.getAttributes());
    }

    @Override
    public AttributeContainer ignore(String... ignore) {
        for (String ignoreKey : ignore)
            attributes.remove(ignoreKey);
        return this;
    }

    @Override
    public AttributeContainer with(String key, AttributeFactory factory) {
        attributeMap.put(key, factory);
        return this;
    }

    @Override
    public AttributeContainer with(Map<String, AttributeFactory> keyMap) {
        attributeMap.putAll(keyMap);
        return this;
    }

    @Override
    public AttributeContainer with(Attribute<?> attribute) {
        attributes.put(attribute.getKey(), attribute);
        return this;
    }

    @Override
    public AttributeContainer with(AttributeContainer container) {
        attributeMap.putAll(container.getAttributeMap().getKeyMap());
        attributes.putAll(container.getAttributes());
        return this;
    }

    @Override
    public AttributeContainer copy(AttributeContainer container) {
        return new MutableAttributeContainer(container);
    }

    @Override
    public AttributeContainer copy() {
        return new MutableAttributeContainer(this);
    }

    @Override
    @Nullable
    public <T> T get(String key) {
        return (T) attributes.get(key);
    }

    @Override
    public <T> T get(String key, T def) {
        T value = get(key);
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
    @Nullable
    public <V> Attribute<V> getAttribute(String key) {
        return (Attribute<V>) attributes.get(key);
    }

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
    @Nullable
    public <V> V getValue(String key) {
        return ((Attribute<V>) attributes.get(key)).getValue();
    }

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
        getConfigurableAttributes().values()
                .forEach(attribute -> builder.append(attribute.toConfig(depth, configEntry)));
        return builder.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        attributes.clear();
        if (configObject == null)
            return;
        attributes.putAll(attributeMap.createAllMap((ConfigurationSection) configObject));
    }

    private Map<String, ConfigurableAttribute<?>> getConfigurableAttributes() {
        Map<String, ConfigurableAttribute<?>> configurableAttributes = new LinkedHashMap<>();
        attributes.keySet().stream()
                .filter(key -> attributes.get(key) instanceof ConfigurableAttribute<?>)
                .forEach(key -> configurableAttributes.put(key, (ConfigurableAttribute<?>) attributes.get(key)));
        return configurableAttributes;
    }

    public static AttributeContainerBuilder builder() {
        return new AttributeContainerBuilder();
    }

}
