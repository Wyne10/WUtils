package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * An {@link AttributeContainer} whose {@code with} / {@code ignore} operations copy: each returns a
 * new container reflecting the change, leaving the receiver untouched. Used by every shipped
 * configurable, since they share a static {@link AttributeMap} between instances and cannot risk one
 * instance's mutation leaking into another's.
 *
 * <p>"Immutable" describes only this mutator behaviour, not the object itself: the copy constructors
 * take a shallow {@code new LinkedHashMap<>(...)} of the source's attributes, and
 * {@link #getAttributes()} returns that live map — so {@code container.getAttributes().put(...)}
 * mutates it directly, bypassing the copy-on-write contract.</p>
 */
public class ImmutableAttributeContainer extends AttributeContainerBase {

    public ImmutableAttributeContainer() {
        super();
    }

    public ImmutableAttributeContainer(@NotNull AttributeMap attributeMap) {
        super(attributeMap);
    }

    public ImmutableAttributeContainer(@NotNull Map<@NotNull String, @NotNull Attribute<?>> attributes) {
        super(attributes);
    }

    public ImmutableAttributeContainer(@NotNull AttributeMap attributeMap, @NotNull Map<@NotNull String, @NotNull Attribute<?>> attributes) {
        super(attributeMap, attributes);
    }

    public ImmutableAttributeContainer(@NotNull AttributeMap attributeMap, @NotNull ConfigurationSection config) {
        super(attributeMap, config);
    }

    public ImmutableAttributeContainer(@NotNull AttributeContainer container) {
        super(container);
    }

    @Override
    public @NotNull AttributeContainer ignore(@NotNull String... ignore) {
        ImmutableAttributeContainer container = new ImmutableAttributeContainer(this);
        for (String ignoreKey : ignore)
            container.getAttributes().remove(ignoreKey);
        return container;
    }

    @Override
    public @NotNull AttributeContainer with(@NotNull String key, @NotNull AttributeFactory<?> factory) {
        ImmutableAttributeContainer container = new ImmutableAttributeContainer(this);
        container.getAttributeMap().put(key, factory);
        return container;
    }

    @Override
    public @NotNull AttributeContainer with(@NotNull Map<@NotNull String, @NotNull AttributeFactory<?>> keyMap) {
        ImmutableAttributeContainer container = new ImmutableAttributeContainer(this);
        container.getAttributeMap().putAll(keyMap);
        return container;
    }

    @Override
    public @NotNull AttributeContainer with(@NotNull Attribute<?> attribute) {
        ImmutableAttributeContainer container = new ImmutableAttributeContainer(this);
        container.getAttributes().put(attribute.getKey(), attribute);
        return container;
    }

    @Override
    public @NotNull AttributeContainer with(@NotNull AttributeContainer container) {
        ImmutableAttributeContainer newContainer = new ImmutableAttributeContainer(this);
        newContainer.getAttributeMap().putAll(container.getAttributeMap().getKeyMap());
        newContainer.getAttributes().putAll(container.getAttributes());
        return newContainer;
    }

    @Override
    public @NotNull AttributeContainer copy(@NotNull AttributeContainer container) {
        return new ImmutableAttributeContainer(container);
    }

    @Override
    public @NotNull AttributeContainer copy() {
        return new ImmutableAttributeContainer(this);
    }

    public static @NotNull AttributeContainerBuilder builder() {
        return new AttributeContainerBuilder();
    }

}
