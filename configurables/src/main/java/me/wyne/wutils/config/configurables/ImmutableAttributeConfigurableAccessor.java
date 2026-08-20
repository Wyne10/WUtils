package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.Attribute;
import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * An {@link AttributeConfigurableAccessor} that builds a new {@code T} per operation, by reflectively
 * calling a {@code T(AttributeContainer)} constructor on the wrapped configurable's own class.
 *
 * <p>Every shipped configurable declares such a constructor. A custom {@link AttributeConfigurable}
 * subclass that does not throws {@link RuntimeException} from this accessor's own constructor,
 * before any operation is called.</p>
 *
 * @param <T> the configurable type being operated on
 */
@SuppressWarnings("unchecked")
public class ImmutableAttributeConfigurableAccessor<T extends AttributeConfigurable> implements AttributeConfigurableAccessor<T> {

    private final T attributeConfigurable;
    private final Constructor<? extends AttributeConfigurable> constructor;

    public ImmutableAttributeConfigurableAccessor(@NotNull T attributeConfigurable) {
        this.attributeConfigurable = attributeConfigurable;
        try {
            this.constructor = attributeConfigurable.getClass().getConstructor(AttributeContainer.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Configurable must contain constructor with AttributeContainer parameter in order to use immutable attribute configurable accessor", e);
        }
    }

    @Override
    public @NotNull T ignore(@NotNull String... ignore) {
        return create(attributeConfigurable.getAttributeContainer().ignore(ignore));
    }

    @Override
    public @NotNull T with(@NotNull String key, @NotNull AttributeFactory<?> factory) {
        return create(attributeConfigurable.getAttributeContainer().with(key, factory));
    }

    @Override
    public @NotNull T with(@NotNull Map<@NotNull String, @NotNull AttributeFactory<?>> keyMap) {
        return create(attributeConfigurable.getAttributeContainer().with(keyMap));
    }

    @Override
    public @NotNull T with(@NotNull Attribute<?> attribute) {
        return create(attributeConfigurable.getAttributeContainer().with(attribute));
    }

    @Override
    public @NotNull T with(@NotNull AttributeContainer container) {
        return create(attributeConfigurable.getAttributeContainer().with(container));
    }

    @Override
    public @NotNull T copy(@NotNull AttributeContainer container) {
        return create(attributeConfigurable.getAttributeContainer().copy(container));
    }

    @Override
    public @NotNull T copy() {
        return create(attributeConfigurable.getAttributeContainer().copy());
    }

    private @NotNull T create(@NotNull AttributeContainer container) {
        try {
            return (T) constructor.newInstance(container);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Configurable must contain constructor with AttributeContainer parameter in order to use immutable attribute configurable accessor", e);
        }
    }

}
