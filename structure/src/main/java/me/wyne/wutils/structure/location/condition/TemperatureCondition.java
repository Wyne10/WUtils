package me.wyne.wutils.structure.location.condition;

import me.wyne.wutils.common.comparator.DoubleComparator;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public record TemperatureCondition(@NotNull DoubleComparator comparator) implements LocationCondition {
    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder()
                .append(depth, "temperature", comparator)
                .buildNoTrail();
    }

    @Override
    public boolean isValid(@NotNull Location location) {
        return comparator.compare(location.getWorld().getTemperature(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }
}
