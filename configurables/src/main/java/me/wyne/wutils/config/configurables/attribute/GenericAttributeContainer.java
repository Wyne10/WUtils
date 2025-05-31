package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.common.MapUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class GenericAttributeContainer<T extends Attribute<?>> implements CompositeConfigurable {

    private final AttributeMap attributeMap;
    private final Map<String, T> attributes;

    public GenericAttributeContainer() {
        this.attributeMap = new AttributeMap(new LinkedHashMap<>());
        this.attributes = new LinkedHashMap<>();
    }

    public GenericAttributeContainer(AttributeMap attributeMap) {
        this.attributeMap = attributeMap;
        this.attributes = new LinkedHashMap<>();
    }

    public GenericAttributeContainer(Map<String, T> attributes) {
        this.attributeMap = new AttributeMap(new LinkedHashMap<>());
        this.attributes = attributes;
    }

    public GenericAttributeContainer(AttributeMap attributeMap, Map<String, T> attributes) {
        this.attributeMap = attributeMap;
        this.attributes = attributes;
    }

    public GenericAttributeContainer(GenericAttributeContainer<T> container) {
        this.attributeMap = new AttributeMap(container.attributeMap.getKeyMap());
        this.attributes = new LinkedHashMap<>(container.attributes);
    }

    public GenericAttributeContainer<T> ignore(String... ignore) {
        for (String ignoreKey : ignore)
            attributes.remove(ignoreKey);
        return this;
    }

    public GenericAttributeContainer<T> with(String key, AttributeFactory factory) {
        attributeMap.put(key, factory);
        return this;
    }

    public GenericAttributeContainer<T> with(Map<String, AttributeFactory> keyMap) {
        attributeMap.putAll(keyMap);
        return this;
    }

    public GenericAttributeContainer<T> with(T attribute) {
        attributes.put(attribute.getKey(), attribute);
        return this;
    }

    public GenericAttributeContainer<T> copy(GenericAttributeContainer<T> container) {
        attributes.putAll(container.attributes);
        return this;
    }

    public GenericAttributeContainer<T> copy() {
        return new GenericAttributeContainer<T>(this);
    }

    @Nullable
    public T get(String key) {
        return attributes.get(key);
    }

    public T get(String key, T def) {
        T value = get(key);
        if (value == null)
            return def;
        return value;
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

    public Map<String, T> getAttributes() {
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
        attributes.putAll(MapUtils.map(attributeMap.createAllMap((ConfigurationSection) configObject), entry -> MapUtils.entry(entry.getKey(), (T) entry.getValue()), LinkedHashMap::new));
    }

    private Map<String, ConfigurableAttribute<?>> getConfigurableAttributes() {
        Map<String, ConfigurableAttribute<?>> configurableAttributes = new LinkedHashMap<>();
        attributes.keySet().stream()
                .filter(key -> attributes.get(key) instanceof ConfigurableAttribute<?>)
                .forEach(key -> configurableAttributes.put(key, (ConfigurableAttribute<?>) attributes.get(key)));
        return configurableAttributes;
    }

    public static <T extends Attribute<?>> Builder<T> builder() {
        return new Builder<>();
    }

    public Builder<T> toBuilder() {
        return new Builder<>(this);
    }

    public static final class Builder<T extends Attribute<?>> {

        private final AttributeMap attributeMap;
        private final Map<String, T> attributes;

        public Builder() {
            attributeMap = new AttributeMap(new LinkedHashMap<>());
            attributes = new LinkedHashMap<>();
        }

        public Builder(GenericAttributeContainer<T> attributeContainer) {
            this.attributeMap = new AttributeMap(attributeContainer.attributeMap.getKeyMap());
            this.attributes = new LinkedHashMap<>(attributeContainer.attributes);
        }

        public Builder<T> ignore(String... ignore) {
            for (String ignoreKey : ignore)
                attributes.remove(ignoreKey);
            return this;
        }

        public Builder<T> with(String key, AttributeFactory factory) {
            attributeMap.put(key, factory);
            return this;
        }

        public Builder<T> with(Map<String, AttributeFactory> keyMap) {
            attributeMap.putAll(keyMap);
            return this;
        }

        public Builder<T> with(T attribute) {
            attributes.put(attribute.getKey(), attribute);
            return this;
        }

        public Builder<T> copy(GenericAttributeContainer<T> container) {
            attributes.putAll(container.attributes);
            return this;
        }

        public GenericAttributeContainer<T> build() {
            return new GenericAttributeContainer<>(attributeMap, attributes);
        }

    }

}
