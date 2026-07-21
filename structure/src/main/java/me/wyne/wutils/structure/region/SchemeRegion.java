package me.wyne.wutils.structure.region;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import me.wyne.wutils.structure.scheme.Scheme;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class SchemeRegion extends StructureRegion {

    public SchemeRegion(@NotNull RegionData regionData) {
        super(regionData);
    }

    @Override
    public @NotNull ProtectedCuboidRegion getRegion(@NotNull Clipboard clipboard, @NotNull Location location) {
        var editLocation = BukkitAdapter.adapt(location);
        var worldRegion = Scheme.toWorld(clipboard, editLocation);
        var region = new ProtectedCuboidRegion(
                validateId(getRegionData().id(), location),
                getRegionData().isTransient(),
                worldRegion.getMinimumPoint(),
                worldRegion.getMaximumPoint()
        );
        getRegionData().apply(region);
        return region;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return "";
    }

    public static final class Factory implements GenericFactory<StructureRegion> {
        @Override
        public StructureRegion create(String key, ConfigurationSection config) {
            return new SchemeRegion(new RegionData.Factory().create(key, config));
        }
    }
}
