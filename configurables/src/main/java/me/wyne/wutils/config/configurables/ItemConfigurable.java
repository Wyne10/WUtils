package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurables.item.Attribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.ItemConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public class ItemConfigurable implements CompositeConfigurable {

    private ItemConfig itemConfig = new ItemConfig();

    public ItemConfigurable() {}

    public ItemConfigurable(Object configObject) {
        fromConfig(configObject);
    }

    public ItemConfigurable(ItemConfig itemConfig) {
        this.itemConfig = itemConfig;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return "\n" + itemConfig.toConfig(depth);
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        itemConfig = ItemConfig.fromConfig((ConfigurationSection) configObject);
    }

    public ItemConfig getItemConfig() {
        return itemConfig;
    }

    public <T> Attribute<T> getAttribute(ItemAttribute attribute) {
        return itemConfig.getAttribute(attribute.getKey());
    }

}
