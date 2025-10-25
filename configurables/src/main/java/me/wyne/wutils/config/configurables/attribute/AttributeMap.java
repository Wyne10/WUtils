package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;
import org.javatuples.Pair;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AttributeMap {

    private final Map<String, AttributeFactory> keyMap = new LinkedHashMap<>();

    public AttributeMap(Map<String, AttributeFactory> keyMap) {
        this.keyMap.putAll(keyMap);
    }

    public void put(String key, AttributeFactory factory) {
        keyMap.put(key, factory);
    }

    public void putAll(Map<String, AttributeFactory> keyMap) {
        this.keyMap.putAll(keyMap);
    }

    public Set<Attribute<?>> createAll(ConfigurationSection config) {
        return keyMap.keySet().stream()
                .map(key -> {
                    var attributeKey = key;
                    var section = config.getConfigurationSection(key);
                    if (section != null && section.getString("type") != null)
                        attributeKey = section.getString("type");
                    return new Pair<>(key, attributeKey);
                })
                .filter(pair -> keyMap.containsKey(pair.getValue1()))
                .map(pair -> keyMap.get(pair.getValue1()).create(pair.getValue0(), config))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Map<String, Attribute<?>> createAllMap(ConfigurationSection config) {
        Map<String, Attribute<?>> result = new LinkedHashMap<>();
        keyMap.keySet().stream()
                .map(key -> {
                    var attributeKey = key;
                    var section = config.getConfigurationSection(key);
                    if (section != null && section.getString("type") != null)
                        attributeKey = section.getString("type");
                    return new Pair<>(key, attributeKey);
                })
                .filter(pair -> keyMap.containsKey(pair.getValue1()))
                .forEach(pair -> result.put(pair.getValue0(), keyMap.get(pair.getValue1()).create(pair.getValue0(), config)));
        return result;
    }

    public Map<String, AttributeFactory> getKeyMap() {
        return keyMap;
    }

}
