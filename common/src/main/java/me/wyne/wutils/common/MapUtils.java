package me.wyne.wutils.common;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class MapUtils {

    public static <K, V, K2, V2> Map<K2, V2> map(Map<K, V> map, MapFunction<K, V, K2, V2> mapFunction) {
        return MapUtils.map(map, mapFunction, HashMap::new);
    }

    public static <K, V, K2, V2> Map<K2, V2> map(Map<K, V> map, MapFunction<K, V, K2, V2> mapFunction, Supplier<Map<K2, V2>> supplier) {
        Map<K2, V2> mapped = supplier.get();
        map.entrySet().stream()
                .map(mapFunction::map)
                .forEach(entry -> mapped.put(entry.getKey(), entry.getValue()));
        return mapped;
    }

    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    @FunctionalInterface
    public interface MapFunction<K, V, K2, V2> {
        Map.Entry<K2, V2> map(Map.Entry<K, V> entry);
    }

}
