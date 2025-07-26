package me.wyne.wutils.common;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ConfigUtils {

    public static final Vector ZERO_VECTOR = new Vector();

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

    public static Vector getVector(String string, Vector def) {
        var args = new Args(string, ",");
        var x = args.size() < 0 ? def.getX() : Double.parseDouble(args.get(0));
        var y = args.size() < 1 ? def.getY() : Double.parseDouble(args.get(1));
        var z = args.size() < 2 ? def.getZ() : Double.parseDouble(args.get(2));
        return new Vector(x, y, z);
    }

    public static Vector getVectorOrZero(String string) {
        return getVector(string, ZERO_VECTOR);
    }

    public static Vector getVector(ConfigurationSection config, String path, Vector def) {
        return getVector(config.getString(path, ""), def);
    }

    public static Vector getVectorOrZero(ConfigurationSection config, String path) {
        return getVector(config, path, ZERO_VECTOR);
    }

    public static String toString(Vector vector) {
        return vector.getX() + "," + vector.getY() + "," + vector.getZ();
    }

}
