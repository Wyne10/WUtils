package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.Attribute;
import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Applies an {@link AttributeContainer} mutator to an {@link AttributeConfigurable} and hands back
 * the result correctly typed as {@code T}, rather than as the container's own static type — which is
 * what {@link AttributeConfigurable#with} et al. cannot do on their own, since the configurable wraps
 * a container instead of being one.
 *
 * <p>{@link ImmutableAttributeConfigurableAccessor} builds a new {@code T} per operation;
 * {@link MutableAttributeConfigurableAccessor} mutates the wrapped configurable's container and
 * returns the same instance. See {@link AttributeConfigurable#getAccessor} for how one is obtained.</p>
 *
 * @param <T> the configurable type being operated on
 */
public interface AttributeConfigurableAccessor<T extends AttributeConfigurable> {
    @NotNull T ignore(@NotNull String... ignore);

    @NotNull T with(@NotNull String key, @NotNull AttributeFactory<?> factory);

    @NotNull T with(@NotNull Map<@NotNull String, @NotNull AttributeFactory<?>> keyMap);

    @NotNull T with(@NotNull Attribute<?> attribute);

    @NotNull T with(@NotNull AttributeContainer container);

    @NotNull T copy(@NotNull AttributeContainer container);

    @NotNull T copy();
}
