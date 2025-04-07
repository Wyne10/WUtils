package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.MapUtils;

import java.util.Map;

public class MapConfigurable<V> extends GenericMapConfigurable<String, V> {

    public MapConfigurable(Map<String, V> map, MapUtils.MapFunction<String, V, String, String> valueMapper, MapUtils.MapFunction<String, Object, String, V> configMapper) {
        super(map, valueMapper, configMapper);
    }

    public MapConfigurable(Object configObject, MapUtils.MapFunction<String, V, String, String> valueMapper, MapUtils.MapFunction<String, Object, String, V> configMapper) {
        super(configObject, valueMapper, configMapper);
    }

    public MapConfigurable(MapUtils.MapFunction<String, V, String, String> valueMapper, MapUtils.MapFunction<String, Object, String, V> configMapper) {
        super(valueMapper, configMapper);
    }

    @SuppressWarnings("unchecked")
    public MapConfigurable(Map<String, V> map) {
        this(map,
                entry -> MapUtils.entry(entry.getKey(), entry.getValue().toString()),
                entry -> MapUtils.entry(entry.getKey(), (V) entry.getValue()));
    }

}
