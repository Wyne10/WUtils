package me.wyne.wutils.common.random;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtils {
    public static <K, V extends Number> Map.Entry<K, V> weightedRandom(Map<K, V> map) {
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
