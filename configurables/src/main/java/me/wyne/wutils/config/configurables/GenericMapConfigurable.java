package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.MapUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GenericMapConfigurable<K, V> implements CompositeConfigurable {

    private final Map<K, V> map = new HashMap<>();
    private final MapUtils.MapFunction<K, V, String, String> valueMapper;
    private final MapUtils.MapFunction<String, Object, K, V> configMapper;

    public GenericMapConfigurable(ConfigurationSection section, MapUtils.MapFunction<K, V, String, String> valueMapper, MapUtils.MapFunction<String, Object, K, V> configMapper) {
        this.valueMapper = valueMapper;
        this.configMapper = configMapper;
        fromConfig(section);
    }

    public GenericMapConfigurable(Map<K, V> map, MapUtils.MapFunction<K, V, String, String> valueMapper, MapUtils.MapFunction<String, Object, K, V> configMapper) {
        this.map.putAll(map);
        this.valueMapper = valueMapper;
        this.configMapper = configMapper;
    }

    public GenericMapConfigurable(MapUtils.MapFunction<K, V, String, String> valueMapper, MapUtils.MapFunction<String, Object, K, V> configMapper) {
        this.valueMapper = valueMapper;
        this.configMapper = configMapper;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        map.entrySet().stream()
                .map(valueMapper::map)
                .forEach(entry -> configBuilder.appendString(depth, entry.getKey(), entry.getValue()));
        return configBuilder.build();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        map.clear();
        if (configObject == null)
            return;
        ConfigurationSection config = (ConfigurationSection) configObject;
        config.getKeys(false).stream()
                .map(key -> configMapper.map(MapUtils.entry(key, config.get(key))))
                .forEach(entry -> map.put(entry.getKey(), entry.getValue()));
    }

    public Map<K, V> getMap() {
        return map;
    }

    void putAll(Map<K, V> map) {
        this.map.putAll(map);
    }

}
