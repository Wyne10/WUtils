package me.wyne.wutils.config.configurables.item;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class CompositeAttribute implements Attribute<Map<String, Attribute<?>>> {

    private final String key;
    private final Map<String, Attribute<?>> attributes = new LinkedHashMap<>();

    public CompositeAttribute(String key, ConfigurationSection config, AttributeFactory factory) {
        this.key = key;
        config.getConfigurationSection(key).getKeys(false).forEach(
                itemKey -> attributes.put(itemKey, factory.create(itemKey, config.getConfigurationSection(key)))
        );
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Map<String, Attribute<?>> getValue() {
        return attributes;
    }

    @Override
    public void apply(ItemStack item) {
        getValue().values().forEach(attribute -> attribute.apply(item));
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        ConfigBuilder builder = new ConfigBuilder();
        attributes.keySet().forEach(key -> builder.appendComposite(depth, key, attributes.get(key), configEntry));
        return builder.build();
    }

}
