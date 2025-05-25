package me.wyne.wutils.config.configurables.item;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.AttributeBase;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class CompositeAttribute extends AttributeBase<Map<String, ConfigurableAttribute<?>>> implements ItemStackAttribute, ConfigurableAttribute<Map<String, ConfigurableAttribute<?>>> {

    public CompositeAttribute(String key, ConfigurationSection config, AttributeFactory factory) {
        super(key, new LinkedHashMap<>());
        config.getConfigurationSection(key).getKeys(false).forEach(
                itemKey -> getValue().put(itemKey, factory.create(itemKey, config.getConfigurationSection(key)))
        );
    }

    @Override
    public void apply(ItemStack item) {
        getValue().values().stream()
                .filter(attribute -> attribute instanceof ItemStackAttribute)
                .forEach(attribute -> ((ItemStackAttribute)attribute).apply(item));
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        ConfigBuilder builder = new ConfigBuilder();
        getValue().keySet().forEach(key -> builder.appendComposite(depth, key, getValue().get(key), configEntry));
        return builder.build();
    }

}
