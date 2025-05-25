package me.wyne.wutils.config.configurables.item;

import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record AttributeMap(Map<String, AttributeFactory> keyMap) {

    public void put(String key, AttributeFactory factory) {
        keyMap.put(key, factory);
    }

    public Set<Attribute<?>> createAll(ConfigurationSection config) {
        return keyMap.keySet().stream()
                .filter(config::contains)
                .map(key -> keyMap.get(key).create(key, config))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Map<String, Attribute<?>> createAllMap(ConfigurationSection config) {
        Map<String, Attribute<?>> result = new LinkedHashMap<>();
        keyMap.keySet().stream()
                .filter(config::contains)
                .forEach(key -> result.put(key, keyMap.get(key).create(key, config)));
        return result;
    }

}
