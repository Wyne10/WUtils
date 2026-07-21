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

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder()
                .append(depth, "world", range.getWorld().getName())
                .append(depth, "range", range)
                .append(depth, "except", except)
                .buildNoTrail();
    }

    public static final class Factory implements GenericFactory<StructureLocation> {
        @Override
        public StructureLocation create(String key, ConfigurationSection config) {
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
