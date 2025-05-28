package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class AttributeContainer implements CompositeConfigurable {

    private final AttributeMap attributeMap;
    private final Map<String, Attribute<?>> attributes;

    public AttributeContainer() {
        attributeMap = new AttributeMap(new LinkedHashMap<>());
        attributes = new LinkedHashMap<>();
    }

    public AttributeContainer(AttributeMap attributeMap) {
        this.attributeMap = attributeMap;
        attributes = new LinkedHashMap<>();
    }

    public AttributeContainer(Map<String, Attribute<?>> attributes) {
        attributeMap = new AttributeMap(new LinkedHashMap<>());
        this.attributes = attributes;
    }

    public AttributeContainer(AttributeMap attributeMap, Map<String, Attribute<?>> attributes) {
        this.attributeMap = attributeMap;
        this.attributes = attributes;
    }

    public AttributeContainer ignore(String... ignore) {
        for (String ignoreKey : ignore)
            attributes.remove(ignoreKey);
        return this;
    }

    public AttributeContainer with(String key, AttributeFactory factory) {
        attributeMap.put(key, factory);
        return this;
    }

    public AttributeContainer with(Attribute<?> attribute) {
        attributes.put(attribute.getKey(), attribute);
        return this;
    }

    @Nullable
    public <T> T get(String key) {
        return (T) attributes.get(key);
    }

    public <T> T get(String key, T def) {
        T value = get(key);
        if (value == null)
            return def;
        return value;
    }

    public <T> Set<T> getSet(Class<T> clazz) {
        return attributes.values().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Nullable
    public <V> Attribute<V> getAttribute(String key) {
        return (Attribute<V>) attributes.get(key);
    }

    public <V> Attribute<V> getAttribute(String key, Attribute<V> def) {
        var attribute = (Attribute<V>) attributes.get(key);
        if (attribute == null)
            return def;
        return attribute;
    }

    public <V> Set<Attribute<V>> getAttributes(Class<Attribute<V>> clazz) {
        return attributes.values().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Nullable
    public <V> V getValue(String key) {
        return ((Attribute<V>) attributes.get(key)).getValue();
    }

    public <V> V getValue(String key, V def) {
        var attribute = (Attribute<V>) attributes.get(key);
        if (attribute == null)
            return def;
        return attribute.getValue();
    }

    public <V> Set<V> getValues(Class<V> clazz) {
        return attributes.values().stream()
                .filter(attribute -> clazz.isInstance(attribute.getValue()))
                .map(attribute -> clazz.cast(attribute.getValue()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Map<String, Attribute<?>> getAttributes() {
        return attributes;
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
        attributes.putAll(attributeMap.createAllMap((ConfigurationSection) configObject));
    }

    private Map<String, ConfigurableAttribute<?>> getConfigurableAttributes() {
        Map<String, ConfigurableAttribute<?>> configurableAttributes = new LinkedHashMap<>();
        attributes.keySet().stream()
                .filter(key -> attributes.get(key) instanceof ConfigurableAttribute<?>)
                .forEach(key-> configurableAttributes.put(key, (ConfigurableAttribute<?>) attributes.get(key)));
        return configurableAttributes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder {

        private final AttributeMap attributeMap;
        private final Map<String, Attribute<?>> attributes;

        public Builder() {
            attributeMap = new AttributeMap(new LinkedHashMap<>());
            attributes = new LinkedHashMap<>();
        }

        public Builder(AttributeContainer attributeContainer) {
            this.attributeMap = new AttributeMap(attributeContainer.attributeMap.keyMap());
            this.attributes = new LinkedHashMap<>(attributeContainer.attributes);
        }

        public Builder ignore(String... ignore) {
            for (String ignoreKey : ignore)
                attributes.remove(ignoreKey);
            return this;
        }

        public Builder with(String key, AttributeFactory factory) {
            attributeMap.put(key, factory);
            return this;
        }

        public Builder with(Attribute<?> attribute) {
            attributes.put(attribute.getKey(), attribute);
            return this;
        }

        public AttributeContainer build() {
            return new AttributeContainer(attributeMap, attributes);
        }

    }

}
