package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class AttributeUtils {

    @SuppressWarnings("unchecked")
    public static <T> Set<T> createAll(ConfigurationSection config, AttributeFactory factory) {
        return config.getKeys(false).stream()
                .map(key -> (T) factory.create(key, config))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
