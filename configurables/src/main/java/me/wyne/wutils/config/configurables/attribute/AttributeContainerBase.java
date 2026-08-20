package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurables.attribute.common.RootAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implements the {@link AttributeContainer} query surface shared by {@link ImmutableAttributeContainer}
 * and {@link MutableAttributeContainer}; the two subclasses differ only in whether {@code with} /
 * {@code ignore} / {@code copy} mutate in place or copy.
 */
public abstract class AttributeContainerBase implements AttributeContainer {

    private final AttributeMap attributeMap;
    private final Map<String, Attribute<?>> attributes;

    public AttributeContainerBase() {
        this.attributeMap = new AttributeMap(new LinkedHashMap<>());
        this.attributes = new LinkedHashMap<>();
    }

    public AttributeContainerBase(@NotNull AttributeMap attributeMap) {
        this.attributeMap = new AttributeMap(attributeMap.getKeyMap());
        this.attributes = new LinkedHashMap<>();
    }

    public AttributeContainerBase(@NotNull Map<@NotNull String, @NotNull Attribute<?>> attributes) {
        this.attributeMap = new AttributeMap(new LinkedHashMap<>());
        this.attributes = new LinkedHashMap<>(attributes);
    }

    public AttributeContainerBase(@NotNull AttributeMap attributeMap, @NotNull Map<@NotNull String, @NotNull Attribute<?>> attributes) {
        this.attributeMap = new AttributeMap(attributeMap.getKeyMap());
        this.attributes = new LinkedHashMap<>(attributes);
    }

    public AttributeContainerBase(@NotNull AttributeMap attributeMap, @NotNull ConfigurationSection config) {
        this.attributeMap = new AttributeMap(attributeMap.getKeyMap());
        this.attributes = new LinkedHashMap<>();
        fromConfig(config);
    }

    public AttributeContainerBase(@NotNull AttributeContainer container) {
        this.attributeMap = new AttributeMap(container.getAttributeMap().getKeyMap());
        this.attributes = new LinkedHashMap<>(container.getAttributes());
    }

    @Override
    public @Nullable ConfigurationSection getRoot() {
        return getValue("root");
    }

    @Override
    public boolean contains(@NotNull Class<?> clazz) {
        return attributes.values()
                .stream()
                .anyMatch(clazz::isInstance);
    }

    @Override
    public boolean contains(@NotNull String key) {
        return attributes.containsKey(key);
    }

    @Override
    public <T> @Nullable T get(@NotNull Class<T> clazz) {
        return get(clazz, null);
    }

    @Override
    public @Nullable <T> T get(@NotNull String key) {
        return get(key, null);
    }

    @Override
    public @Nullable <T> T get(@NotNull Class<T> clazz, @Nullable T def) {
        return attributes.values().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst().orElse(def);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T> T get(@NotNull String key, @Nullable T def) {
        T value = (T) attributes.get(key);
        if (value == null)
            return def;
        return value;
    }

    @Override
    public @NotNull <T> Set<@NotNull T> getSet(@NotNull Class<T> clazz) {
        return attributes.values().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public @Nullable <T, V> Attribute<V> getAttribute(@NotNull Class<T> clazz) {
        return getAttribute(clazz, null);
    }

    @Override
    public @Nullable <V> Attribute<V> getAttribute(@NotNull String key) {
        return getAttribute(key, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T, V> Attribute<V> getAttribute(@NotNull Class<T> clazz, @Nullable Attribute<V> def) {
        return attributes.values().stream()
                .filter(clazz::isInstance)
                .map(Attribute.class::cast)
                .findFirst().orElse(def);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <V> Attribute<V> getAttribute(@NotNull String key, @Nullable Attribute<V> def) {
        var attribute = (Attribute<V>) attributes.get(key);
        if (attribute == null)
            return def;
        return attribute;
    }

    @Override
    public @NotNull <V> Set<@NotNull Attribute<V>> getAttributes(@NotNull Class<Attribute<V>> clazz) {
        return attributes.values().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public <T, V> @Nullable V getValue(@NotNull Class<T> clazz) {
        return getValue(clazz, null);
    }

    @Override
    public @Nullable <V> V getValue(@NotNull String key) {
        return getValue(key, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, V> @Nullable V getValue(@NotNull Class<T> clazz, @Nullable V def) {
        return (V) attributes.values().stream()
                .filter(clazz::isInstance)
                .map(Attribute.class::cast)
                .findFirst().map(Attribute::getValue).orElse(def);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <V> V getValue(@NotNull String key, @Nullable V def) {
        var attribute = (Attribute<V>) attributes.get(key);
        if (attribute == null)
            return def;
        return attribute.getValue();
    }

    @Override
    public @NotNull <V> Set<@NotNull V> getValues(@NotNull Class<V> clazz) {
        return attributes.values().stream()
                .filter(attribute -> clazz.isInstance(attribute.getValue()))
                .map(attribute -> clazz.cast(attribute.getValue()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull Attribute<?>> getAttributes() {
        return attributes;
    }

    @Override
    public @NotNull AttributeMap getAttributeMap() {
        return attributeMap;
    }

    /**
     * Renders every {@link ConfigurableAttribute} stored in this container as {@code key: value}
     * lines, in registration order. Attributes that only extend {@link AttributeBase} (such as
     * {@link RootAttribute}) are not {@link ConfigurableAttribute}s and are skipped, by design.
     */
    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        getConfigurableAttributes()
                .forEach(attribute -> builder.append(attribute.toConfig(depth, configEntry)));
        if (builder.charAt(builder.length() - 1) == '\n')
            builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    /**
     * Clears the container and rebuilds it from {@code configObject}: a {@code "root"}
     * {@link RootAttribute} holding the entire section is inserted first, then every attribute the
     * container's {@link AttributeMap} resolves from the section is added. A {@code null}
     * {@code configObject} is a no-op, leaving any existing attributes untouched.
     *
     * <p>Because {@code root} is always inserted first, it is always the first entry in
     * {@link #getAttributes()} after a successful read, and {@link #getRoot()} is always non-null. A
     * container built by hand (via a builder or {@code with}) has no {@code root} unless one is added
     * explicitly.</p>
     */
    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        attributes.clear();
        attributes.put("root", new RootAttribute.Factory().create("root", (ConfigurationSection) configObject));
        attributes.putAll(attributeMap.createAllMap((ConfigurationSection) configObject));
    }

    @SuppressWarnings("rawtypes")
    private Set<ConfigurableAttribute> getConfigurableAttributes() {
        return getSet(ConfigurableAttribute.class);
    }

}
