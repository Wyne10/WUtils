package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * A queryable bag of resolved {@link Attribute}s, built from an {@link AttributeMap} either by
 * reading a {@link ConfigurationSection} via {@link #fromConfig} or by accumulating attributes
 * directly through an {@link AttributeContainerBuilder}.
 *
 * <p>Every lookup comes in a by-key and a by-class flavour, at three depths: {@link #get} /
 * {@link #getAttribute} return the raw attribute (cast, or as {@code Attribute<V>}), and
 * {@link #getValue} unwraps to the attribute's value. The by-class lookups (and their {@code Set}
 * variants {@link #getSet}, {@link #getAttributes(Class)}, {@link #getValues}) are {@code instanceof}
 * scans over the stored attributes, returned in registration order.</p>
 *
 * <p>{@link #ignore} and the {@code with} overloads mutate differently depending on the concrete
 * implementation: {@link ImmutableAttributeContainer} copies and returns a new container, leaving the
 * receiver untouched, while {@link MutableAttributeContainer} mutates in place and returns
 * {@code this}. Both satisfy this interface's return type, so callers must chain the result rather
 * than assume the receiver changed.</p>
 */
public interface AttributeContainer extends CompositeConfigSerializable, ConfigDeserializable {

    /**
     * Returns the section the container was last read from via {@link #fromConfig}, or {@code null}
     * if the container was built by hand and never read from config. Populated as the
     * {@code "root"} attribute — see {@link #fromConfig}.
     */
    @Nullable ConfigurationSection getRoot();

    /**
     * Returns a copy of this container with the given keys removed from its attributes, or (for a
     * mutable container) this container with the keys removed in place.
     */
    @NotNull AttributeContainer ignore(@NotNull String... ignore);

    /**
     * Returns a copy of this container with {@code factory} registered under {@code key} in its
     * attribute map, or (for a mutable container) this container with the factory registered in
     * place. Registering a factory does not itself add an attribute; it only affects subsequent
     * {@link #fromConfig} reads.
     */
    @NotNull AttributeContainer with(@NotNull String key, @NotNull AttributeFactory<?> factory);

    @NotNull AttributeContainer with(@NotNull Map<@NotNull String, @NotNull AttributeFactory<?>> keyMap);

    /**
     * Returns a copy of this container with {@code attribute} added (or replacing any existing
     * attribute under the same key), or (for a mutable container) this container with the attribute
     * added in place.
     */
    @NotNull AttributeContainer with(@NotNull Attribute<?> attribute);

    /**
     * Returns a copy of this container with {@code container}'s attribute map and attributes merged
     * in, or (for a mutable container) this container with the merge applied in place.
     */
    @NotNull AttributeContainer with(@NotNull AttributeContainer container);

    /**
     * Returns a new container of this implementation's kind, holding a shallow copy of
     * {@code container}'s attribute map and attributes.
     */
    @NotNull AttributeContainer copy(@NotNull AttributeContainer container);

    /**
     * Returns a new container of this implementation's kind, holding a shallow copy of this
     * container's attribute map and attributes.
     */
    @NotNull AttributeContainer copy();

    boolean contains(@NotNull Class<?> clazz);

    boolean contains(@NotNull String key);

    /**
     * Returns the first stored attribute assignable to {@code clazz}, cast to {@code T}, or
     * {@code null} if none matches.
     */
    @Nullable <T> T get(@NotNull Class<T> clazz);

    /**
     * Returns the attribute stored under {@code key}, cast to {@code T}, or {@code null} if no
     * attribute is stored under that key.
     */
    @Nullable <T> T get(@NotNull String key);

    @Nullable <T> T get(@NotNull Class<T> clazz, @Nullable T def);

    @Nullable <T> T get(@NotNull String key, @Nullable T def);

    @NotNull <T> Set<@NotNull T> getSet(@NotNull Class<T> clazz);

    @Nullable <T, V> Attribute<V> getAttribute(@NotNull Class<T> clazz);

    @Nullable <V> Attribute<V> getAttribute(@NotNull String key);

    @Nullable <T, V> Attribute<V> getAttribute(@NotNull Class<T> clazz, @Nullable Attribute<V> def);

    @Nullable <V> Attribute<V> getAttribute(@NotNull String key, @Nullable Attribute<V> def);

    @NotNull <V> Set<@NotNull Attribute<V>> getAttributes(@NotNull Class<Attribute<V>> clazz);

    @Nullable <T, V> V getValue(@NotNull Class<T> clazz);

    @Nullable <V> V getValue(@NotNull String key);

    /**
     * Returns the value of the first stored attribute assignable to {@code clazz}, or {@code def} if
     * no such attribute is stored. Note that a matching attribute holding a {@code null} value
     * yields {@code null}, not {@code def} — the default only covers a missing attribute, not one
     * whose value is itself null.
     */
    @Nullable <T, V> V getValue(@NotNull Class<T> clazz, @Nullable V def);

    /**
     * Returns the value of the attribute stored under {@code key}, or {@code def} if no attribute is
     * stored under that key. Note that an attribute present under {@code key} but holding a
     * {@code null} value yields {@code null}, not {@code def}.
     */
    @Nullable <V> V getValue(@NotNull String key, @Nullable V def);

    @NotNull <V> Set<@NotNull V> getValues(@NotNull Class<V> clazz);

    @NotNull AttributeMap getAttributeMap();

    @NotNull Map<@NotNull String, @NotNull Attribute<?>> getAttributes();

    default @NotNull AttributeContainerBuilder toBuilder() {
        return new AttributeContainerBuilder(this);
    }
}
