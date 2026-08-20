package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A map of config key to {@code List<E>}, rendered as one {@link ConfigBuilder#appendCollection}
 * block sequence per key.
 *
 * @param <E> the list element type
 */
public class ListMapConfigurable<E> implements CompositeConfigSerializable, ConfigDeserializable {

    private final Map<String, List<E>> map = new HashMap<>();

    public ListMapConfigurable() {}

    public ListMapConfigurable(@NotNull ConfigurationSection section) {
        fromConfig(section);
    }

    public ListMapConfigurable(@NotNull Map<@NotNull String, @NotNull List<E>> map) {
        this.map.putAll(map);
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        map.forEach((key, value) -> {
            configBuilder.appendCollection(depth, key, value);
        });
        return configBuilder.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        map.clear();
        ConfigurationSection config = (ConfigurationSection) configObject;
        config.getKeys(false).forEach(key -> map.put(key, (List<E>) config.getList(key)));
    }

    public @NotNull Map<@NotNull String, @NotNull List<E>> getMap() {
        return map;
    }

}
