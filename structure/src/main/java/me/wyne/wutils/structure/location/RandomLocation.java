package me.wyne.wutils.structure.location;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.common.location.LocationUtils;
import me.wyne.wutils.common.range.LocationRange;
import me.wyne.wutils.common.range.VectorRange;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A location drawn uniformly at random from {@code range}, optionally excluding a
 * sub-volume.
 *
 * <p><b>Sharp edge:</b> {@link #getLocation()} loops until it draws a point outside
 * {@code except}, so an {@code except} that covers the whole {@code range} hangs forever.</p>
 */
public record RandomLocation(@NotNull LocationRange range, @Nullable VectorRange except) implements StructureLocation {
    @Override
    public @NotNull Location getLocation() {
        var random = range.getRandom();
        if (except != null) {
            while (except.contains(random)) {
                random = range.getRandom();
            }
        }
        return LocationUtils.of(range.getWorld(), random);
    }

    /**
     * Returns whether {@code location} falls within {@code range} and, if set, outside
     * {@code except}.
     */
    public boolean withinBounds(@NotNull Location location) {
        if (!range.contains(location))
            return false;
        return except == null || !except.contains(location.toVector());
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder()
                .append(depth, "world", range.getWorld().getName())
                .append(depth, "range", range)
                .append(depth, "except", except)
                .buildNoTrail();
    }

    public static final class Factory implements GenericFactory<StructureLocation> {
        @Override
        public @NotNull StructureLocation create(@NotNull String key, @NotNull ConfigurationSection config) {
            var section = ConfigUtils.getConfigurationSection(config, key);
            var world = Bukkit.getWorld(section.getString("world", "world"));
            var vectorRange = ConfigUtils.getVectorRange(section, "range");
            var locationRange = new LocationRange(world, vectorRange);
            VectorRange except = null;
            if (section.contains("except")) {
                except = ConfigUtils.getVectorRange(section, "except");
            }
            return new RandomLocation(locationRange, except);
        }
    }
}
