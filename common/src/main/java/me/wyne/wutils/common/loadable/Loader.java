package me.wyne.wutils.common.loadable;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class Loader {

    public static final String DEFAULT_PATH = "config.yml";

    public static final Loader global = new Loader();

    private final Map<String, ConfigurationSection> configMap = new HashMap<>();
    private final Map<Loadable, String> loadableMap = new HashMap<>();

    public void registerLoadable(Loadable loadable) {
        String path = DEFAULT_PATH;
        if (loadable.getClass().isAnnotationPresent(LoadablePath.class)) {
            path = loadable.getClass().getAnnotation(LoadablePath.class).path();
        }
        loadableMap.put(loadable, path);
    }

    public void registerConfig(String path, ConfigurationSection config) {
        configMap.put(path, config);
    }

    public void load() {
        loadableMap.forEach((loadable, path) -> {
            loadable.load(configMap.get(path));
        });
    }

}
