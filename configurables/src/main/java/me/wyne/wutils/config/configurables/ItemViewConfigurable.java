package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.Collections;

public class ItemViewConfigurable extends ViewConfigurable {

    private final ItemStackConfigurable itemConfigurable;

    public ItemViewConfigurable(ConfigurationSection section) {
        itemConfigurable = new ItemStackConfigurable();
        fromConfig(section);
    }

    public ItemViewConfigurable(String name, Collection<String> lore, ItemStackConfigurable itemConfigurable) {
        super(name, lore);
        this.itemConfigurable = itemConfigurable;
    }

    public ItemViewConfigurable(String name, ItemStackConfigurable itemConfigurable) {
        this(name, Collections.emptyList(), itemConfigurable);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.appendComposite(depth, "item", itemConfigurable, configEntry);
        return super.toConfig(configEntry) + configBuilder.build();
    }

    @Override
    public void fromConfig(Object configObject) {
        super.fromConfig(configObject);
        ConfigurationSection section = (ConfigurationSection) configObject;
        itemConfigurable.fromConfig(section.getConfigurationSection("item"));
    }

    public ItemStackConfigurable getItem() {
        return itemConfigurable;
    }

}
