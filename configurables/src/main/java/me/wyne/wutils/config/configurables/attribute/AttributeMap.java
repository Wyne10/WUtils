package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
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
        var attributeKeyMap = getAttributeKeyMap(config);
        return keyMap.keySet().stream()
                .flatMap(key ->
                    attributeKeyMap.get(key).stream()
                            .map(configKey -> keyMap.get(key).create(configKey, config))
                )
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Map<String, Attribute<?>> createAllMap(ConfigurationSection config) {
        var attributeKeyMap = getAttributeKeyMap(config);
        Map<String, Attribute<?>> result = new LinkedHashMap<>();
        keyMap.keySet().stream()
                .forEach(key ->
                        attributeKeyMap.get(key).stream()
                                .forEach(configKey -> result.put(configKey, keyMap.get(key).create(configKey, config)))
                );
        return result;
    }

    private Map<String, Set<String>> getAttributeKeyMap(ConfigurationSection config) {
        Map<String, Set<String>> attributeKeyMap = new LinkedHashMap<>();
        keyMap.keySet().forEach(key -> attributeKeyMap.put(key, new LinkedHashSet<>()));
        config.getKeys(false).stream()
                .map(config::getConfigurationSection)
                .filter(Objects::nonNull)
                .filter(section -> section.contains("attributeType") && section.isString("attributeType"))
                .filter(section -> attributeKeyMap.containsKey(section.getString("attributeType")))
                .forEach(section -> attributeKeyMap.get(section.getString("attributeType")).add(section.getName()));
        keyMap.keySet().stream()
                .filter(config::contains)
                .forEach(key -> attributeKeyMap.get(key).add(key));
        return attributeKeyMap;
    }

    public Map<String, AttributeFactory> getKeyMap() {
        return keyMap;
    }

}
