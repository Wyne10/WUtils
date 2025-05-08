package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

@Deprecated
public class ItemViewConfigurable extends ViewConfigurable {

    @Nullable
    private ItemStackConfigurable itemConfigurable;

    public ItemViewConfigurable(ConfigurationSection section) {
        fromConfig(section);
    }

    public ItemViewConfigurable(String name, Collection<String> lore, @Nullable ItemStackConfigurable itemConfigurable) {
        super(name, lore);
        this.itemConfigurable = itemConfigurable;
    }

    public ItemViewConfigurable(String name, ItemStackConfigurable itemConfigurable) {
        this(name, Collections.emptyList(), itemConfigurable);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        if (itemConfigurable == null)
            return super.toConfig(depth, configEntry);
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.appendComposite(depth, "item", itemConfigurable, configEntry);
        return super.toConfig(configEntry) + configBuilder.build();
    }

    @Override
    public void fromConfig(Object configObject) {
        super.fromConfig(configObject);
        ConfigurationSection section = (ConfigurationSection) configObject;
        if (section.contains("item"))
            itemConfigurable = new ItemStackConfigurable(section.getConfigurationSection("item"));
    }

    @Nullable
    public ItemStackConfigurable getItem() {
        return itemConfigurable;
    }

}
