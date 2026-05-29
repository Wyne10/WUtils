package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.stream.Collectors;

public class GenericFactoryMap<T> {

    private final Map<String, GenericFactory<T>> keyMap = new LinkedHashMap<>();

    public GenericFactoryMap() {}

    public GenericFactoryMap(Map<String, GenericFactory<T>> keyMap) {
        this.keyMap.putAll(keyMap);
    }

    public void put(String key, GenericFactory<T> factory) {
        keyMap.put(key, factory);
    }

    public void putAll(Map<String, GenericFactory<T>> keyMap) {
        this.keyMap.putAll(keyMap);
    }

    public Set<T> createAll(ConfigurationSection config) {
        var attributeKeys = getAttributeKeyMap(config);
        return keyMap.keySet().stream()
                .flatMap(key ->
                    attributeKeys.keyMap.get(key).stream()
                            .map(configKey -> keyMap.get(key).create(configKey, sectionFor(config, configKey, attributeKeys)))
                )
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Map<String, T> createAllMap(ConfigurationSection config) {
        var attributeKeys = getAttributeKeyMap(config);
        Map<String, T> result = new LinkedHashMap<>();
        keyMap.keySet().stream()
                .forEach(key ->
                        attributeKeys.keyMap.get(key).stream()
                                .forEach(configKey -> result.put(configKey, keyMap.get(key).create(configKey, sectionFor(config, configKey, attributeKeys))))
                );
        return result;
    }

    private ConfigurationSection sectionFor(ConfigurationSection config, String configKey, AttributeKeys attributeKeys) {
        return attributeKeys.typedKeys.contains(configKey)
                ? ConfigUtils.getConfigurationSection(config, configKey)
                : config;
    }

    private AttributeKeys getAttributeKeyMap(ConfigurationSection config) {
        Map<String, Set<String>> attributeKeyMap = new LinkedHashMap<>();
        Set<String> typedKeys = new HashSet<>();
        keyMap.keySet().forEach(key -> attributeKeyMap.put(key, new LinkedHashSet<>()));
        config.getKeys(false).stream()
                .map(config::getConfigurationSection)
                .filter(Objects::nonNull)
                .filter(section -> section.contains("attributeType") && section.isString("attributeType"))
                .filter(section -> attributeKeyMap.containsKey(section.getString("attributeType")))
                .forEach(section -> {
                    attributeKeyMap.get(section.getString("attributeType")).add(section.getName());
                    typedKeys.add(section.getName());
                });
        keyMap.keySet().stream()
                .filter(config::contains)
                .forEach(key -> attributeKeyMap.get(key).add(key));
        return new AttributeKeys(attributeKeyMap, typedKeys);
    }

    private record AttributeKeys(Map<String, Set<String>> keyMap, Set<String> typedKeys) {}

    public Map<String, GenericFactory<T>> getKeyMap() {
        return keyMap;
    }

}
