package me.wyne.wutils.structure.region;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import me.wyne.wutils.structure.scheme.Scheme;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class RadiusRegion extends StructureRegion {

    private final int radius;

    public RadiusRegion(@NotNull RegionData regionData, int radius) {
        super(regionData);
        this.radius = radius;
    }

    @Override
    public @NotNull ProtectedCuboidRegion getRegion(@NotNull Clipboard clipboard, @NotNull Location location) {
        var editLocation = BukkitAdapter.adapt(location);
        var worldRegion = Scheme.toWorld(clipboard, editLocation);
        var region = new ProtectedCuboidRegion(
                validateId(getRegionData().id(), location),
                getRegionData().isTransient(),
                worldRegion.getMinimumPoint().subtract(radius, radius, radius),
                worldRegion.getMaximumPoint().add(radius, radius, radius)
        );
        getRegionData().apply(region);
        return region;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder()
                .append(depth, "radius", radius)
                .buildNoTrail();
    }

    public static final class Factory implements GenericFactory<StructureRegion> {
        @Override
        public StructureRegion create(String key, ConfigurationSection config) {
            var section = ConfigUtils.getConfigurationSection(config, key);
            return new RadiusRegion(new RegionData.Factory().create(key, config), section.getInt("radius"));
        }
    }
}
