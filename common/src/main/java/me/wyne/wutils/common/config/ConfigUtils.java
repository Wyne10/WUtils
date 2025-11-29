package me.wyne.wutils.common.config;

import me.wyne.wutils.common.comparator.Comparators;
import me.wyne.wutils.common.comparator.DoubleComparator;
import me.wyne.wutils.common.comparator.IntComparator;
import me.wyne.wutils.common.duration.Durations;
import me.wyne.wutils.common.duration.TimeSpan;
import me.wyne.wutils.common.operation.DoubleOperation;
import me.wyne.wutils.common.operation.IntOperation;
import me.wyne.wutils.common.operation.Operations;
import me.wyne.wutils.common.range.TimeSpanRange;
import me.wyne.wutils.common.range.VectorRange;
import me.wyne.wutils.common.vector.VectorUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.util.*;

public final class ConfigUtils {

    @SuppressWarnings("DataFlowIssue")
    public static List<String> getStringList(ConfigurationSection config, String path) {
        if (!config.isList(path))
            return config.getString(path, "").isEmpty() ? Collections.emptyList() : List.of(config.getString(path));
        return config.getStringList(path);
    }

    public static String getString(ConfigurationSection config, String path, String def) {
        if (config.isList(path))
            return reduceString(config.getStringList(path));
        return config.getString(path, def);
    }

    public static String reduceString(Collection<String> stringList) {
        return stringList.stream().reduce(ConfigUtils::reduceString).orElse("");
    }

    public static String reduceString(String s1, String s2) {
        return s1 + "\n" + s2;
    }

    public static Vector getVector(ConfigurationSection config, String path, Vector def) {
        return VectorUtils.getVector(config.getString(path, ""), def);
    }

    public static Vector getVectorOrZero(ConfigurationSection config, String path) {
        return getVector(config, path, VectorUtils.zero());
    }

    public static IntComparator getIntComparator(ConfigurationSection config, String path) {
        return Comparators.getIntComparator(config.getString(path));
    }

    public static DoubleComparator getDoubleComparator(ConfigurationSection config, String path) {
        return Comparators.getDoubleComparator(config.getString(path));
    }

    public static IntOperation getIntOperation(ConfigurationSection config, String path) {
        return Operations.getIntOperation(config.getString(path));
    }

    public static DoubleOperation getDoubleOperation(ConfigurationSection config, String path) {
        return Operations.getDoubleOperation(config.getString(path));
    }

    public static TimeSpan getTimeSpan(ConfigurationSection config, String path) {
        return Durations.getTimeSpan(config.getString(path));
    }

    public static long getMillis(ConfigurationSection config, String path) {
        return Durations.getMillis(config.getString(path));
    }

    public static long getTicks(ConfigurationSection config, String path) {
        return Durations.getTicks(config.getString(path));
    }

    public static TimeSpanRange getTimeSpanRange(ConfigurationSection config, String path) {
        return Durations.getTimeSpanRange(config.getString(path));
    }

    public static VectorRange getVectorRange(ConfigurationSection config, String path) {
        return VectorRange.getVectorRange(config.getString(path));
    }

    public static <E extends Enum<E>> EnumSet<E> getEnumSet(ConfigurationSection section, String key, Class<E> enumClass) {
        if (section.isBoolean(key) && section.getBoolean(key)) {
            return EnumSet.allOf(enumClass);
        }

        if (section.isBoolean(key) && !section.getBoolean(key)) {
            return EnumSet.noneOf(enumClass);
        }

        List<String> values = section.getStringList(key);
        EnumSet<E> result = EnumSet.noneOf(enumClass);

        for (String value : values) {
            try {
                result.add(Enum.valueOf(enumClass, value));
            } catch (IllegalArgumentException ignored) {}
        }

        return result;
    }

}
