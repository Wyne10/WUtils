package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.MapUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

@SuppressWarnings("unchecked")
public class MapConfigurable<V> extends GenericMapConfigurable<String, V> {

    public MapConfigurable(ConfigurationSection section, MapUtils.MapFunction<String, V, String, String> valueMapper, MapUtils.MapFunction<String, Object, String, V> configMapper) {
        super(section, valueMapper, configMapper);
    }

    public MapConfigurable(Map<String, V> map, MapUtils.MapFunction<String, V, String, String> valueMapper, MapUtils.MapFunction<String, Object, String, V> configMapper) {
        super(map, valueMapper, configMapper);
    }

    public MapConfigurable(MapUtils.MapFunction<String, V, String, String> valueMapper, MapUtils.MapFunction<String, Object, String, V> configMapper) {
        super(valueMapper, configMapper);
    }

    public MapConfigurable(Map<String, V> map) {
        this(map,
                entry -> MapUtils.entry(entry.getKey(), entry.getValue().toString()),
                entry -> MapUtils.entry(entry.getKey(), (V) entry.getValue()));
    }

    public MapConfigurable(ConfigurationSection section) {
        this(section,
                entry -> MapUtils.entry(entry.getKey(), entry.getValue().toString()),
                entry -> MapUtils.entry(entry.getKey(), (V) entry.getValue()));
    }

}
