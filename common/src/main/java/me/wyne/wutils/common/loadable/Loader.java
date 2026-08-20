package me.wyne.wutils.common.loadable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Drives a set of registered {@link Loadable}s from registered {@link ConfigurationSection}s,
 * in priority order. A shared {@link #global} instance is provided for callers that don't need
 * an isolated loader.
 */
public class Loader {

    public static final @NotNull String DEFAULT_PATH = "config.yml";

    public static final @NotNull Loader global = new Loader();

    private final Map<String, ConfigurationSection> configMap = new HashMap<>();
    private final Map<Loadable, String> loadableMap = new LinkedHashMap<>();

    /** Registers {@code loadable}, capturing its current {@link Loadable#getPath()} as the config path it will load from. */
    public void registerLoadable(@NotNull Loadable loadable) {
        loadableMap.put(loadable, loadable.getPath());
    }

    /** Associates a config section with {@code path} for loadables registered under that path. */
    public void registerConfig(@NotNull String path, @NotNull ConfigurationSection config) {
        configMap.put(path, config);
    }

    /**
     * Loads every registered loadable whose path has a matching registered config, in ascending
     * {@link Loadable#getPriority()} order. Loadables with {@link Loadable#isLate()} true are
     * deferred to the next server tick via {@link Bukkit#getScheduler()} instead of loading
     * immediately. Afterward, any loadable with {@link Loadable#persist()} false is unregistered
     * and will be skipped by future calls to this method.
     */
    public void load(@NotNull Plugin plugin) {
        loadableMap.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getPriority()))
                .forEachOrdered(entry -> {
                    if (configMap.containsKey(entry.getValue())) {
                        if (entry.getKey().isLate())
                            Bukkit.getScheduler().runTask(plugin, () -> { entry.getKey().load(configMap.get(entry.getValue())); });
                        else
                            entry.getKey().load(configMap.get(entry.getValue()));
                    }
                });
        loadableMap.keySet().removeIf(loadable -> !loadable.persist());
    }

}
