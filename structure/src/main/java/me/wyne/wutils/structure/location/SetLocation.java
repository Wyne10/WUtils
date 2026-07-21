package me.wyne.wutils.structure.location;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public record SetLocation(@NotNull Location location) implements StructureLocation {
    @Override
    public @NotNull Location getLocation() {
        return location;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder()
                .append(depth, "world", location.getWorld() != null ? location.getWorld().getName() : null)
                .append(depth, "location", location.toVector())
                .buildNoTrail();
    }

    public static final class Factory implements GenericFactory<StructureLocation> {
        @Override
        public StructureLocation create(String key, ConfigurationSection config) {
            var section = ConfigUtils.getConfigurationSection(config, key);
            var world = Bukkit.getWorld(section.getString("world", "world"));
            var vector = ConfigUtils.getVectorOrZero(section, "location");
            return new SetLocation(new Location(world, vector.getX(), vector.getY(), vector.getZ()));
        }
    }
}
