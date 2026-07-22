package me.wyne.wutils.structure.location.condition;

import me.wyne.wutils.common.comparator.IntComparator;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public record AltitudeCondition(@NotNull IntComparator comparator) implements LocationCondition {
    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder()
                .append(depth, "altitude", comparator)
                .buildNoTrail();
    }

    @Override
    public boolean isValid(@NotNull Location location) {
        return comparator.compare(location.getBlockY());
    }
}
