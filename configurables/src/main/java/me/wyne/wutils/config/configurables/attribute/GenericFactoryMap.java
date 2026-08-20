package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An insertion-ordered registry of config key to {@link GenericFactory}, resolving keys with the
 * same key / {@code attributeType} alias scheme as {@link AttributeMap} — see that class for the
 * resolution rules and the two alias body shapes, both of which apply here identically.
 *
 * <p>This is a line-for-line copy of {@link AttributeMap} with the {@link Attribute} bound removed,
 * kept for consumers that want the same key-resolution scheme for non-attribute objects. Nothing in
 * this module uses it; a fix made to {@link AttributeMap} needs to be made here too.</p>
 *
 * @param <T> the type this registry builds
 */
public class GenericFactoryMap<T> {

    private final Map<String, GenericFactory<T>> keyMap = new LinkedHashMap<>();

    public GenericFactoryMap() {}

    public GenericFactoryMap(@NotNull Map<@NotNull String, @NotNull GenericFactory<T>> keyMap) {
        this.keyMap.putAll(keyMap);
    }

    public void put(@NotNull String key, @NotNull GenericFactory<T> factory) {
        keyMap.put(key, factory);
    }

    public void putAll(@NotNull Map<@NotNull String, @NotNull GenericFactory<T>> keyMap) {
        this.keyMap.putAll(keyMap);
    }

    public @NotNull Set<@NotNull T> createAll(@NotNull ConfigurationSection config) {
        var attributeKeys = getAttributeKeyMap(config);
        return keyMap.keySet().stream()
                .flatMap(key ->
                    attributeKeys.keyMap.get(key).stream()
                            .map(configKey -> keyMap.get(key).create(configKey, sectionFor(config, configKey, attributeKeys)))
                )
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public @NotNull Map<@NotNull String, @NotNull T> createAllMap(@NotNull ConfigurationSection config) {
        var attributeKeys = getAttributeKeyMap(config);
        Map<String, T> result = new LinkedHashMap<>();
        keyMap.keySet().stream()
                .forEach(key ->
                        attributeKeys.keyMap.get(key).stream()
                                .forEach(configKey -> result.put(configKey, keyMap.get(key).create(configKey, sectionFor(config, configKey, attributeKeys))))
                );
        return result;
    }

    private @NotNull ConfigurationSection sectionFor(@NotNull ConfigurationSection config, @NotNull String configKey, @NotNull AttributeKeys attributeKeys) {
        return attributeKeys.typedKeys.contains(configKey)
                ? ConfigUtils.getConfigurationSection(config, configKey)
                : config;
    }

    private @NotNull AttributeKeys getAttributeKeyMap(@NotNull ConfigurationSection config) {
        Map<String, Set<String>> attributeKeyMap = new LinkedHashMap<>();
        Set<String> typedKeys = new LinkedHashSet<>();
        keyMap.keySet().forEach(key -> attributeKeyMap.put(key, new LinkedHashSet<>()));
        keyMap.keySet().stream()
                .filter(config::contains)
                .forEach(key -> attributeKeyMap.get(key).add(key));
        config.getKeys(false).stream()
                .map(config::getConfigurationSection)
                .filter(Objects::nonNull)
                .filter(section -> section.contains("attributeType") && section.isString("attributeType"))
                .filter(section -> attributeKeyMap.containsKey(section.getString("attributeType")))
                .forEach(section -> {
                    attributeKeyMap.get(section.getString("attributeType")).add(section.getName());
                    typedKeys.add(section.getName());
                });
        return new AttributeKeys(attributeKeyMap, typedKeys);
    }

    private record AttributeKeys(@NotNull Map<@NotNull String, @NotNull Set<@NotNull String>> keyMap, @NotNull Set<@NotNull String> typedKeys) {}

    public @NotNull Map<@NotNull String, @NotNull GenericFactory<T>> getKeyMap() {
        return keyMap;
    }

}
