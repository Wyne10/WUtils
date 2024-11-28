package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurable.Configurable;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMapConfigurable<T> implements Configurable {

    private final Map<String, List<T>> map = new HashMap<>();

    public ListMapConfigurable(ConfigurationSection section) {
        fromConfig(section);
    }

    public ListMapConfigurable(Map<String, List<T>> map) {
        this.map.putAll(map);
    }

    public ListMapConfigurable() {}

    @Override
    public String toConfig(ConfigEntry configEntry) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        map.forEach((key, value) -> {
            configBuilder.appendCollection(2, key, value);
        });
        return configBuilder.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fromConfig(Object configObject) {
        ConfigurationSection config = (ConfigurationSection) configObject;
        map.clear();
        config.getKeys(false).forEach(key -> map.put(key, (List<T>) config.getList(key)));
    }

    public Map<String, List<T>> getMap() {
        return map;
    }

}
