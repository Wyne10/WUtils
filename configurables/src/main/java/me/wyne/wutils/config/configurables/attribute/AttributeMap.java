package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An insertion-ordered registry of config key to {@link AttributeFactory}, and the resolver that
 * decides which config keys feed which factory when reading a {@link ConfigurationSection}.
 *
 * <p>Resolution runs two passes over the section, in {@link #getAttributeKeyMap}:</p>
 * <ul>
 *     <li><b>Direct keys</b> — for every registered key the section {@code contains}, the config key
 *     <em>is</em> the registered key, and the factory is handed the enclosing section.</li>
 *     <li><b>{@code attributeType} aliases</b> — every direct child that is itself a section, carries
 *     a string {@code attributeType}, and whose {@code attributeType} names a registered key, is
 *     registered as an additional occurrence of that key under its own name. An aliased key's
 *     factory is handed <em>its own child section</em> rather than the enclosing one.</li>
 * </ul>
 *
 * <p>{@code attributeType} is the escape hatch for needing more than one attribute of the same kind
 * under a single registered key, since a registered key can only appear once as a YAML key while an
 * alias can appear under any number of names.</p>
 *
 * <p>One substitution explains aliasing: where the direct form calls the factory with the registered
 * key and the <em>enclosing</em> section, an alias calls it with the alias name and the alias's own
 * section. Whatever the factory read from the enclosing section at the registered key, it now reads
 * from the alias section at the alias name. Every attribute can be aliased; only the shape of the
 * alias body differs.</p>
 *
 * <p>A factory that reads a value at {@code key} needs the alias body to carry a key named after the
 * alias itself — {@code set1: {attributeType: set, set1: '...'}}. A factory that resolves its body
 * section instead takes the fields directly, with no repetition; a
 * {@link CompositeAttributeFactory} does that via its fallback branch, and a plain
 * {@link AttributeFactory} does it in one line with
 * {@code ConfigUtils.getConfigurationSection(config, key)}, which returns the child section at
 * {@code key} or the section itself when {@code key} is not a section. Writing the body in the
 * shape the factory does not expect silently yields the attribute's defaults, with nothing
 * logged.</p>
 *
 * <p>{@link #createAllMap} and {@link #createAll} iterate this registry's key set, not the config's,
 * so the resulting attribute order — and therefore any order-dependent application of those
 * attributes downstream — follows registration order, not the order keys appear in YAML.</p>
 */
public class AttributeMap {

    private final Map<String, AttributeFactory<?>> keyMap = new LinkedHashMap<>();

    public AttributeMap() {}

    public AttributeMap(@NotNull Map<@NotNull String, @NotNull AttributeFactory<?>> keyMap) {
        this.keyMap.putAll(keyMap);
    }

    /**
     * Registers {@code factory} under {@code key}, overwriting any factory already registered there.
     */
    public void put(@NotNull String key, @NotNull AttributeFactory<?> factory) {
        keyMap.put(key, factory);
    }

    public void putAll(@NotNull Map<@NotNull String, @NotNull AttributeFactory<?>> keyMap) {
        this.keyMap.putAll(keyMap);
    }

    /**
     * Resolves and builds every attribute this registry's keys (and their {@code attributeType}
     * aliases) match in {@code config}, in registration order.
     */
    public @NotNull Set<@NotNull Attribute<?>> createAll(@NotNull ConfigurationSection config) {
        var attributeKeys = getAttributeKeyMap(config);
        return keyMap.keySet().stream()
                .flatMap(key ->
                    attributeKeys.keyMap.get(key).stream()
                            .map(configKey -> keyMap.get(key).create(configKey, sectionFor(config, configKey, attributeKeys)))
                )
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Same resolution as {@link #createAll}, keyed by each attribute's own config key rather than
     * collected into a set. The returned map preserves registration order.
     */
    public @NotNull Map<@NotNull String, @NotNull Attribute<?>> createAllMap(@NotNull ConfigurationSection config) {
        var attributeKeys = getAttributeKeyMap(config);
        Map<String, Attribute<?>> result = new LinkedHashMap<>();
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

    public @NotNull Map<@NotNull String, @NotNull AttributeFactory<?>> getKeyMap() {
        return keyMap;
    }

}
