package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.MapUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@code Map<K, V>} read from and rendered to config via caller-supplied mapper functions, for key
 * and value types {@link ConfigBuilder} does not know how to render or parse on its own.
 *
 * <p>Has no no-arg constructor: both {@code valueMapper} and {@code configMapper} are required at
 * construction, so an instance is always ready to read config immediately.</p>
 *
 * @param <K> the map key type
 * @param <V> the map value type
 */
public class GenericMapConfigurable<K, V> implements CompositeConfigSerializable, ConfigDeserializable {

    private final Map<K, V> map = new HashMap<>();
    private final MapUtils.MapFunction<K, V, String, String> valueMapper;
    private final MapUtils.MapFunction<String, Object, K, V> configMapper;

    public GenericMapConfigurable(@NotNull ConfigurationSection section, @NotNull MapUtils.MapFunction<K, V, String, String> valueMapper, @NotNull MapUtils.MapFunction<String, Object, K, V> configMapper) {
        this.valueMapper = valueMapper;
        this.configMapper = configMapper;
        fromConfig(section);
    }

    public GenericMapConfigurable(@NotNull Map<K, V> map, @NotNull MapUtils.MapFunction<K, V, String, String> valueMapper, @NotNull MapUtils.MapFunction<String, Object, K, V> configMapper) {
        this.map.putAll(map);
        this.valueMapper = valueMapper;
        this.configMapper = configMapper;
    }

    public GenericMapConfigurable(@NotNull MapUtils.MapFunction<K, V, String, String> valueMapper, @NotNull MapUtils.MapFunction<String, Object, K, V> configMapper) {
        this.valueMapper = valueMapper;
        this.configMapper = configMapper;
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        map.entrySet().stream()
                .map(valueMapper::map)
                .forEach(entry -> configBuilder.appendString(depth, entry.getKey(), entry.getValue()));
        return configBuilder.build();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        map.clear();
        ConfigurationSection config = (ConfigurationSection) configObject;
        config.getKeys(false).stream()
                .map(key -> configMapper.map(MapUtils.entry(key, config.get(key))))
                .forEach(entry -> map.put(entry.getKey(), entry.getValue()));
    }

    public @NotNull Map<K, V> getMap() {
        return map;
    }

    void putAll(@NotNull Map<K, V> map) {
        this.map.putAll(map);
    }

}
