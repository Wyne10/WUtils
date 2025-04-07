package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.MapUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurable.Configurable;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class GenericMapConfigurable<K, V> implements Configurable, CompositeConfigurable {

    private final Map<K, V> map = new HashMap<>();
    private final MapUtils.MapFunction<K, V, String, String> valueMapper;
    private final MapUtils.MapFunction<String, Object, K, V> configMapper;

    public GenericMapConfigurable(Map<K, V> map, MapUtils.MapFunction<K, V, String, String> valueMapper, MapUtils.MapFunction<String, Object, K, V> configMapper) {
        this.map.putAll(map);
        this.configMapper = configMapper;
        this.valueMapper = valueMapper;
    }

    public GenericMapConfigurable(Object configObject, MapUtils.MapFunction<K, V, String, String> valueMapper, MapUtils.MapFunction<String, Object, K, V> configMapper) {
        fromConfig(configObject);
        this.configMapper = configMapper;
        this.valueMapper = valueMapper;
    }

    public GenericMapConfigurable(MapUtils.MapFunction<K, V, String, String> valueMapper, MapUtils.MapFunction<String, Object, K, V> configMapper) {
        this.configMapper = configMapper;
        this.valueMapper = valueMapper;
    }

    @Override
    public String toConfig(ConfigEntry configEntry) {
        return toConfig(2, configEntry);
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
    public void fromConfig(Object configObject) {
        ConfigurationSection config = (ConfigurationSection) configObject;
        map.clear();
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
