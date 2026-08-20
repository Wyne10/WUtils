package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class AttributeUtils {

    /**
     * Runs a single {@code factory} over every direct child key of {@code config}, with no registry
     * involved — the simple case of "a section of N things that are all the same kind", as opposed to
     * the key-resolution scheme in {@link AttributeMap}.
     *
     * <p>Uses a raw {@link AttributeFactory}, so {@code T} is unchecked: a mismatched factory fails
     * with a {@link ClassCastException} at the call site rather than at parse time.</p>
     */
    @SuppressWarnings("unchecked")
    public static <T> @NotNull Set<@NotNull T> createAll(@NotNull ConfigurationSection config, @NotNull AttributeFactory factory) {
        return config.getKeys(false).stream()
                .map(key -> (T) factory.create(key, config))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
