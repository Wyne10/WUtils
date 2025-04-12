package me.wyne.wutils.common.loadable;

import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class Loader {

    public static final String DEFAULT_PATH = "config.yml";

    public static final Loader global = new Loader();

    private final Map<String, ConfigurationSection> configMap = new HashMap<>();
    private final Map<Loadable, String> loadableMap = new TreeMap<>(Comparator.comparingInt(Loadable::getPriority));

    public void registerLoadable(Loadable loadable) {
        loadableMap.put(loadable, loadable.getPath());
    }

    public void registerConfig(String path, ConfigurationSection config) {
        configMap.put(path, config);
    }

    public void load() {
        loadableMap.forEach((loadable, path) -> {
            if (configMap.containsKey(path))
                loadable.load(configMap.get(path));
        });
    }

}
