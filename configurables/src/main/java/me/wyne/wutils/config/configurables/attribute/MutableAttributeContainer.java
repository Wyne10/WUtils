package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * An {@link AttributeContainer} whose {@code with} / {@code ignore} operations mutate this instance
 * in place and return {@code this}, rather than copying. Nothing shipped in this module uses it;
 * every shipped configurable uses {@link ImmutableAttributeContainer} instead.
 */
public class MutableAttributeContainer extends AttributeContainerBase {

    public MutableAttributeContainer() {
        super();
    }

    public MutableAttributeContainer(@NotNull AttributeMap attributeMap) {
        super(attributeMap);
    }

    public MutableAttributeContainer(@NotNull Map<@NotNull String, @NotNull Attribute<?>> attributes) {
        super(attributes);
    }

    public MutableAttributeContainer(@NotNull AttributeMap attributeMap, @NotNull Map<@NotNull String, @NotNull Attribute<?>> attributes) {
        super(attributeMap, attributes);
    }

    public MutableAttributeContainer(@NotNull AttributeMap attributeMap, @NotNull ConfigurationSection config) {
        super(attributeMap, config);
    }

    public MutableAttributeContainer(@NotNull AttributeContainer container) {
        super(container);
    }

    @Override
    public @NotNull AttributeContainer ignore(@NotNull String... ignore) {
        for (String ignoreKey : ignore)
            getAttributes().remove(ignoreKey);
        return this;
    }

    @Override
    public @NotNull AttributeContainer with(@NotNull String key, @NotNull AttributeFactory<?> factory) {
        getAttributeMap().put(key, factory);
        return this;
    }

    @Override
    public @NotNull AttributeContainer with(@NotNull Map<@NotNull String, @NotNull AttributeFactory<?>> keyMap) {
        getAttributeMap().putAll(keyMap);
        return this;
    }

    @Override
    public @NotNull AttributeContainer with(@NotNull Attribute<?> attribute) {
        getAttributes().put(attribute.getKey(), attribute);
        return this;
    }

    @Override
    public @NotNull AttributeContainer with(@NotNull AttributeContainer container) {
        getAttributeMap().putAll(container.getAttributeMap().getKeyMap());
        getAttributes().putAll(container.getAttributes());
        return this;
    }

    @Override
    public @NotNull AttributeContainer copy(@NotNull AttributeContainer container) {
        return new MutableAttributeContainer(container);
    }

    @Override
    public @NotNull AttributeContainer copy() {
        return new MutableAttributeContainer(this);
    }

    public static @NotNull AttributeContainerBuilder builder() {
        return new AttributeContainerBuilder();
    }

}
