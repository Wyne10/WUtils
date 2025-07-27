package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.config.ConfigEntry;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ImmutableAttributeContainer implements AttributeContainer {

    private final AttributeMap attributeMap;
    private final Map<String, Attribute<?>> attributes;

    public ImmutableAttributeContainer() {
        this.attributeMap = new AttributeMap(new LinkedHashMap<>());
        this.attributes = new LinkedHashMap<>();
    }

    public ImmutableAttributeContainer(AttributeMap attributeMap) {
        this.attributeMap = new AttributeMap(attributeMap.getKeyMap());
        this.attributes = new LinkedHashMap<>();
    }

    public ImmutableAttributeContainer(Map<String, Attribute<?>> attributes) {
        this.attributeMap = new AttributeMap(new LinkedHashMap<>());
        this.attributes = new LinkedHashMap<>(attributes);
    }

    public ImmutableAttributeContainer(AttributeMap attributeMap, Map<String, Attribute<?>> attributes) {
        this.attributeMap = new AttributeMap(attributeMap.getKeyMap());
        this.attributes = new LinkedHashMap<>(attributes);
    }

    public ImmutableAttributeContainer(AttributeMap attributeMap, ConfigurationSection config) {
        this.attributeMap = new AttributeMap(attributeMap.getKeyMap());
        this.attributes = new LinkedHashMap<>();
        fromConfig(config);
    }

    public ImmutableAttributeContainer(AttributeContainer container) {
        this.attributeMap = new AttributeMap(container.getAttributeMap().getKeyMap());
        this.attributes = new LinkedHashMap<>(container.getAttributes());
    }

    @Override
    public AttributeContainer ignore(String... ignore) {
        ImmutableAttributeContainer container = new ImmutableAttributeContainer(this);
        for (String ignoreKey : ignore)
            container.attributes.remove(ignoreKey);
        return container;
    }

    @Override
    public AttributeContainer with(String key, AttributeFactory factory) {
        ImmutableAttributeContainer container = new ImmutableAttributeContainer(this);
        container.attributeMap.put(key, factory);
        return container;
    }

    @Override
    public AttributeContainer with(Map<String, AttributeFactory> keyMap) {
        ImmutableAttributeContainer container = new ImmutableAttributeContainer(this);
        container.attributeMap.putAll(keyMap);
        return container;
    }

    @Override
    public AttributeContainer with(Attribute<?> attribute) {
        ImmutableAttributeContainer container = new ImmutableAttributeContainer(this);
        container.attributes.put(attribute.getKey(), attribute);
        return container;
    }

    @Override
    public AttributeContainer with(AttributeContainer container) {
        ImmutableAttributeContainer newContainer = new ImmutableAttributeContainer(this);
        newContainer.attributeMap.putAll(container.getAttributeMap().getKeyMap());
        newContainer.attributes.putAll(container.getAttributes());
        return newContainer;
    }

    @Override
    public AttributeContainer copy(AttributeContainer container) {
        return new ImmutableAttributeContainer(container);
    }

    @Override
    public AttributeContainer copy() {
        return new ImmutableAttributeContainer(this);
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

    @Override
    public <T, V> Attribute<V> getAttribute(Class<T> clazz, Attribute<V> def) {
        return attributes.values().stream()
                .filter(clazz::isInstance)
                .map(Attribute.class::cast)
                .findFirst().orElse(def);
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
    public <T, V> @Nullable V getValue(Class<T> clazz) {
        return getValue(clazz, null);
    }

    @Override
    public @Nullable <V> V getValue(String key) {
        return getValue(key, null);
    }

    @Override
    public <T, V> V getValue(Class<T> clazz, V def) {
        return (V) attributes.values().stream()
                .filter(clazz::isInstance)
                .map(Attribute.class::cast)
                .findFirst().map(Attribute::getValue).orElse(def);
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
