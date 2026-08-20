package me.wyne.wutils.common;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Utility methods for transforming and constructing {@link Map}s.
 */
public final class MapUtils {

    /**
     * Equivalent to {@link #map(Map, MapFunction, Supplier) map(map, mapFunction, HashMap::new)}.
     */
    public static <K, V, K2, V2> @NotNull Map<K2, V2> map(@NotNull Map<K, V> map, @NotNull MapFunction<K, V, K2, V2> mapFunction) {
        return MapUtils.map(map, mapFunction, HashMap::new);
    }

    /**
     * Builds a new map by applying {@code mapFunction} to every entry of {@code map}.
     *
     * @param supplier creates the (initially empty) map instance to populate and return
     */
    public static <K, V, K2, V2> @NotNull Map<K2, V2> map(@NotNull Map<K, V> map, @NotNull MapFunction<K, V, K2, V2> mapFunction, @NotNull Supplier<Map<K2, V2>> supplier) {
        Map<K2, V2> mapped = supplier.get();
        map.entrySet().stream()
                .map(mapFunction::map)
                .forEach(entry -> mapped.put(entry.getKey(), entry.getValue()));
        return mapped;
    }

    /**
     * Creates an immutable {@link Map.Entry} not tied to any {@link Map}, useful for building
     * entries to feed into {@link #map(Map, MapFunction)}.
     */
    public static <K, V> @NotNull Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    /**
     * Transforms one map entry into another, potentially changing both key and value types.
     */
    @FunctionalInterface
    public interface MapFunction<K, V, K2, V2> {
        @NotNull Map.Entry<K2, V2> map(@NotNull Map.Entry<K, V> entry);
    }

}
