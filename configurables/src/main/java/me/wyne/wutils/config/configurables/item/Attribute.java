package me.wyne.wutils.config.configurables.item;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface Attribute<V> extends CompositeConfigurable {
    String getKey();

    V getValue();

    void apply(ItemStack item);

    @Override
    default String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue()).buildNoSpace();
    }

    @Override
    default void fromConfig(@Nullable Object configObject) {
        throw new NotImplementedException();
    }
}
