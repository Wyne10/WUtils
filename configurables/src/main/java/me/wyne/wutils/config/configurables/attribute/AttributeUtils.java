package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class AttributeUtils {

    public static Set<Attribute<?>> createAll(ConfigurationSection config, AttributeFactory factory) {
        return config.getKeys(false).stream()
                .map(key -> factory.create(key, config))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
