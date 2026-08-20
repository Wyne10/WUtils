package me.wyne.wutils.common.random;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtils {

    /**
     * Picks a random entry from {@code map}, where each value is its selection weight; entries
     * with a non-positive weight are never selected.
     *
     * @return {@code null} if {@code map} is empty or every weight is zero or negative
     */
    public static <K, V extends Number> @Nullable Map.Entry<K, V> weightedRandom(@NotNull Map<K, V> map) {
        if (map.isEmpty()) return null;

        double totalWeight = 0;
        for (V weight : map.values()) {
            double w = weight.doubleValue();
            if (w > 0) totalWeight += w;
        }

        if (totalWeight <= 0) return null;

        double randomWeight = ThreadLocalRandom.current().nextDouble(totalWeight);
        for (Map.Entry<K, V> entry : map.entrySet()) {
            double weight = entry.getValue().doubleValue();
            if (weight <= 0) continue;
            if (randomWeight < weight) return entry;
            randomWeight -= weight;
        }

        return null;
    }
}
