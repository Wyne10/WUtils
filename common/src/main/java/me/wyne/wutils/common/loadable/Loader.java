package me.wyne.wutils.common.loadable;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Loader {

    public static final String DEFAULT_PATH = "config.yml";

    public static final Loader global = new Loader();

    private final Map<String, ConfigurationSection> configMap = new HashMap<>();
    private final Map<Loadable, String> loadableMap = new LinkedHashMap<>();

    public void registerLoadable(Loadable loadable) {
        loadableMap.put(loadable, loadable.getPath());
    }

    public void registerConfig(String path, ConfigurationSection config) {
        configMap.put(path, config);
    }

    public void load() {
        loadableMap.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getPriority()))
                .forEachOrdered(entry -> {
                    if (configMap.containsKey(entry.getValue()))
                        entry.getKey().load(configMap.get(entry.getValue()));
                });
    }

}
