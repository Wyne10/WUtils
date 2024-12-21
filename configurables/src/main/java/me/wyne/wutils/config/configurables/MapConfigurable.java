package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurable.Configurable;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class MapConfigurable<T> implements Configurable {

    private final Map<String, T> map = new HashMap<>();

    public MapConfigurable(Map<String, T> map) {
        this.map.putAll(map);
    }

    public MapConfigurable(Object configObject) {
        fromConfig(configObject);
    }

    public MapConfigurable() {}

    @Override
    public String toConfig(ConfigEntry configEntry) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        map.forEach((key, value) -> configBuilder.append(2, key, value));
        return configBuilder.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fromConfig(Object configObject) {
        ConfigurationSection config = (ConfigurationSection) configObject;
        map.clear();
        config.getKeys(false).forEach(key -> map.put(key, (T) config.get(key)));
    }

    public Map<String, T> getMap() {
        return map;
    }

}
