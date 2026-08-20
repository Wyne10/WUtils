package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.Attribute;
import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ImmutableAttributeContainer;
import me.wyne.wutils.config.configurables.attribute.MutableAttributeContainer;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An {@link AttributeConfigurableAccessor} that mutates the wrapped configurable's
 * {@link AttributeContainer} in place and returns the same configurable instance.
 *
 * <p>{@link #copy(AttributeContainer)} replaces the wrapped configurable's contents with the given
 * container's, in place. {@link #copy()} is identity — copying a configurable into itself changes
 * nothing. For an independent duplicate, use {@link ImmutableAttributeConfigurableAccessor}, whose
 * {@code copy} builds a new configurable.</p>
 *
 * <p><b>{@link #with} and {@link #ignore} are no-ops over an {@link ImmutableAttributeContainer}.</b>
 * They delegate to the container's own {@code with}/{@code ignore}, which on the immutable
 * implementation return a modified <em>copy</em> and leave the receiver untouched — and this class
 * discards that return value. Every shipped configurable
 * ({@code ItemConfigurable}, {@code GuiConfigurable}, {@code InteractionConfigurable},
 * {@code AnimationStepConfigurable}, {@code InvUiItemConfigurable}) is built on an
 * {@link ImmutableAttributeContainer}, so those calls silently do nothing; they work only when the
 * configurable was constructed over a {@link MutableAttributeContainer}. {@link #copy(AttributeContainer)}
 * is unaffected because it writes through the live maps rather than delegating.</p>
 *
 * @param <T> the configurable type being operated on
 */
public class MutableAttributeConfigurableAccessor<T extends AttributeConfigurable> implements AttributeConfigurableAccessor<T> {

    private final T attributeConfigurable;

    public MutableAttributeConfigurableAccessor(@NotNull T attributeConfigurable) {
        this.attributeConfigurable = attributeConfigurable;
    }

    @Override
    public @NotNull T ignore(@NotNull String... ignore) {
        attributeConfigurable.getAttributeContainer().ignore(ignore);
        return attributeConfigurable;
    }

    @Override
    public @NotNull T with(@NotNull String key, @NotNull AttributeFactory<?> factory) {
        attributeConfigurable.getAttributeContainer().with(key, factory);
        return attributeConfigurable;
    }

    @Override
    public @NotNull T with(@NotNull Map<@NotNull String, @NotNull AttributeFactory<?>> keyMap) {
        attributeConfigurable.getAttributeContainer().with(keyMap);
        return attributeConfigurable;
    }

    @Override
    public @NotNull T with(@NotNull Attribute<?> attribute) {
        attributeConfigurable.getAttributeContainer().with(attribute);
        return attributeConfigurable;
    }

    @Override
    public @NotNull T with(@NotNull AttributeContainer container) {
        attributeConfigurable.getAttributeContainer().with(container);
        return attributeConfigurable;
    }

    /**
     * Replaces this configurable's attributes and factory registry with {@code container}'s, in
     * place, discarding whatever it held before. Unlike {@link #with(AttributeContainer)} this does
     * not merge.
     *
     * <p>Attributes are shared, not cloned — {@link me.wyne.wutils.config.configurables.attribute.AttributeBase}
     * holds its key and value in final fields, so the two containers cannot drift apart through
     * them.</p>
     */
    @Override
    public @NotNull T copy(@NotNull AttributeContainer container) {
        // Snapshot before clearing: container may be this configurable's own, and the getters below
        // expose the live maps, so clearing first would wipe the source we are about to read.
        var attributes = new LinkedHashMap<>(container.getAttributes());
        var factories = new LinkedHashMap<>(container.getAttributeMap().getKeyMap());

        var target = attributeConfigurable.getAttributeContainer();
        target.getAttributes().clear();
        target.getAttributes().putAll(attributes);
        target.getAttributeMap().getKeyMap().clear();
        target.getAttributeMap().putAll(factories);
        return attributeConfigurable;
    }

    /**
     * Returns the wrapped configurable unchanged. Copying a configurable into itself is identity;
     * this accessor mutates in place, so there is no second instance to produce. Use
     * {@link ImmutableAttributeConfigurableAccessor#copy()} for an independent duplicate.
     */
    @Override
    public @NotNull T copy() {
        return attributeConfigurable;
    }

}
