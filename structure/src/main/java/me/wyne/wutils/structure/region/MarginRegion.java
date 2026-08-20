package me.wyne.wutils.structure.region;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import me.wyne.wutils.structure.scheme.Scheme;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link StructureRegion} expanded by {@code margin} blocks on every axis beyond the
 * pasted clipboard's bounds.
 */
public class MarginRegion extends StructureRegion {

    private final int margin;

    public MarginRegion(@NotNull RegionData regionData, int margin) {
        super(regionData);
        this.margin = margin;
    }

    @Override
    public @NotNull ProtectedCuboidRegion getRegion(@NotNull Clipboard clipboard, @NotNull Location location, @NotNull Transform transform) {
        var editLocation = BukkitAdapter.adapt(location);
        var worldRegion = Scheme.toWorld(clipboard, editLocation, transform);
        var region = new ProtectedCuboidRegion(
                validateId(getRegionData().id(), location),
                getRegionData().isTransient(),
                worldRegion.getMinimumPoint().subtract(margin, margin, margin),
                worldRegion.getMaximumPoint().add(margin, margin, margin)
        );
        getRegionData().apply(region);
        return region;
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder()
                .append(depth, "margin", margin)
                .buildNoTrail()
                + getRegionData().toConfig(depth, configEntry);
    }

    public static final class Factory implements GenericFactory<StructureRegion> {
        @Override
        public @NotNull StructureRegion create(@NotNull String key, @NotNull ConfigurationSection config) {
            var section = ConfigUtils.getConfigurationSection(config, key);
            return new MarginRegion(new RegionData.Factory().create(key, config), section.getInt("margin"));
        }
    }
}
