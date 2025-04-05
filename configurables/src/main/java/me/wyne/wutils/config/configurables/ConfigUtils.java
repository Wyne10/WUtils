package me.wyne.wutils.config.configurables;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.List;

final class ConfigUtils {

    public static List<String> getStringList(ConfigurationSection config, String path) {
        if (!config.isList(path))
            return List.of(config.getString(path, ""));
        return config.getStringList(path);
    }

    public static String getString(ConfigurationSection config, String path, String def) {
        if (config.isList(path))
            return reduceString(config.getStringList(path));
        return config.getString(path, def);
    }

    public static String reduceString(Collection<String> stringList)
    {
        return stringList.stream().reduce(ConfigUtils::reduceString).orElse("");
    }

    public static String reduceString(String s1, String s2)
    {
        return s1 + "\n" + s2;
    }

}
