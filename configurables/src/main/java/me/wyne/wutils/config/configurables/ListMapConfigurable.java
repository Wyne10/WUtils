package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMapConfigurable<E> implements CompositeConfigurable {

    private final Map<String, List<E>> map = new HashMap<>();

    public ListMapConfigurable(Map<String, List<E>> map) {
        this.map.putAll(map);
    }

    public ListMapConfigurable(Object configObject) {
        fromConfig(configObject);
    }

    public ListMapConfigurable() {}

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        map.forEach((key, value) -> {
            configBuilder.appendCollection(depth, key, value);
        });
        return configBuilder.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fromConfig(Object configObject) {
        ConfigurationSection config = (ConfigurationSection) configObject;
        map.clear();
        config.getKeys(false).forEach(key -> map.put(key, (List<E>) config.getList(key)));
    }

    public Map<String, List<E>> getMap() {
        return map;
    }

}
