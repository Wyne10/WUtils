package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import me.wyne.wutils.config.configurables.attribute.Attribute;
import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.config.configurables.attribute.AttributeContainerBuilder;
import me.wyne.wutils.config.configurables.attribute.AttributeMap;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Wraps an {@link AttributeContainer} and exposes its query surface directly, so consumers do not
 * have to call {@link #getAttributeContainer()} for every lookup. Every shipped configurable
 * ({@code ItemConfigurable}, {@code InteractionConfigurable}, {@code AnimationStepConfigurable}, ...)
 * extends this class and registers its own {@link AttributeMap}.
 *
 * <p>Because a configurable wraps a container rather than being one, {@code with}/{@code ignore}
 * cannot be exposed here with the right return type — {@link #getAccessor} and its
 * {@link AttributeConfigurableAccessor} shortcuts exist for that.</p>
 */
public class AttributeConfigurable implements CompositeConfigSerializable, ConfigDeserializable {

    private final AttributeContainer attributeContainer;

    public AttributeConfigurable(@NotNull AttributeContainer attributeContainer) {
        this.attributeContainer = attributeContainer;
    }

    public AttributeConfigurable(@NotNull AttributeContainer attributeContainer, @NotNull ConfigurationSection section) {
        this(attributeContainer);
        fromConfig(section);
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return attributeContainer.toConfig(depth, configEntry);
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        attributeContainer.fromConfig(configObject);
    }

    /**
     * Returns an {@link AttributeConfigurableAccessor} of the requested kind for this configurable —
     * see {@link AttributeConfigurableAccessors} for how the two kinds differ.
     */
    @SuppressWarnings("unchecked")
    public <T extends AttributeConfigurable> @NotNull AttributeConfigurableAccessor<T> getAccessor(@NotNull AttributeConfigurableAccessors type) {
        return switch (type) {
            case IMMUTABLE -> new ImmutableAttributeConfigurableAccessor<>((T) this);
            case MUTABLE -> new MutableAttributeConfigurableAccessor<>((T) this);
        };
    }

    public <T extends AttributeConfigurable> @NotNull AttributeConfigurableAccessor<T> getImmutableAccessor() {
        return getAccessor(AttributeConfigurableAccessors.IMMUTABLE);
    }

    public <T extends AttributeConfigurable> @NotNull AttributeConfigurableAccessor<T> getMutableAccessor() {
        return getAccessor(AttributeConfigurableAccessors.MUTABLE);
    }

    public @Nullable ConfigurationSection getRoot() {
        return attributeContainer.getRoot();
    }

    public boolean contains(@NotNull Class<?> clazz) {
        return attributeContainer.contains(clazz);
    }

    public boolean contains(@NotNull String key) {
        return attributeContainer.contains(key);
    }

    @Nullable
    public <T> T get(@NotNull Class<T> clazz) {
        return attributeContainer.get(clazz);
    }

    @Nullable
    public <T> T get(@NotNull String key) {
        return attributeContainer.get(key);
    }

    @Nullable
    public <T> T get(@NotNull Class<T> clazz, @Nullable T def) {
        return attributeContainer.get(clazz, def);
    }

    @Nullable
    public <T> T get(@NotNull String key, @Nullable T def) {
        return attributeContainer.get(key, def);
    }

    public <T> @NotNull Set<@NotNull T> getSet(@NotNull Class<T> clazz) {
        return attributeContainer.getSet(clazz);
    }

    @Nullable
    public <T, V> Attribute<V> getAttribute(@NotNull Class<T> clazz) {
        return attributeContainer.getAttribute(clazz);
    }

    @Nullable
    public <V> Attribute<V> getAttribute(@NotNull String key) {
        return attributeContainer.getAttribute(key);
    }

    @Nullable
    public <T, V> Attribute<V> getAttribute(@NotNull Class<T> clazz, @Nullable Attribute<V> def) {
        return attributeContainer.getAttribute(clazz, def);
    }

    @Nullable
    public <V> Attribute<V> getAttribute(@NotNull String key, @Nullable Attribute<V> def) {
        return attributeContainer.getAttribute(key, def);
    }

    public <V> @NotNull Set<@NotNull Attribute<V>> getAttributes(@NotNull Class<Attribute<V>> clazz) {
        return attributeContainer.getAttributes(clazz);
    }

    @Nullable
    public <T, V> V getValue(@NotNull Class<T> clazz) {
        return attributeContainer.getValue(clazz);
    }

    @Nullable
    public <V> V getValue(@NotNull String key) {
        return attributeContainer.getValue(key);
    }

    /**
     * Note that a matching attribute holding a {@code null} value yields {@code null}, not
     * {@code def} — the default only covers a missing attribute, not one whose value is itself null.
     */
    @Nullable
    public <T, V> V getValue(@NotNull Class<T> clazz, @Nullable V def) {
        return attributeContainer.getValue(clazz, def);
    }

    /**
     * Note that an attribute present under {@code key} but holding a {@code null} value yields
     * {@code null}, not {@code def}.
     */
    @Nullable
    public <V> V getValue(@NotNull String key, @Nullable V def) {
        return attributeContainer.getValue(key, def);
    }

    public <V> @NotNull Set<@NotNull V> getValues(@NotNull Class<V> clazz) {
        return attributeContainer.getValues(clazz);
    }

    public @NotNull Map<@NotNull String, @NotNull Attribute<?>> getAttributes() {
        return attributeContainer.getAttributes();
    }

    public @NotNull AttributeMap getAttributeMap() {
        return attributeContainer.getAttributeMap();
    }

    public @NotNull AttributeContainer getAttributeContainer() {
        return attributeContainer;
    }

    public @NotNull AttributeContainerBuilder toBuilder() {
        return new AttributeContainerBuilder(getAttributeContainer());
    }

}
