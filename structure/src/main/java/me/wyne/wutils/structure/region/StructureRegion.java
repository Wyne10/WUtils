package me.wyne.wutils.structure.region;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public abstract class StructureRegion implements CompositeConfigSerializable {

    private final RegionData regionData;

    public StructureRegion(@NotNull RegionData regionData) {
        this.regionData = regionData;
    }

    public @NotNull RegionData getRegionData() {
        return regionData;
    }

    public abstract @NotNull ProtectedCuboidRegion getRegion(@NotNull Clipboard clipboard, @NotNull Location location, @NotNull Transform transform);

    public static @NotNull String validateId(@NotNull String id, @NotNull Location location) {
        return id.replace("<x>", String.valueOf(location.getBlockX()))
                .replace("<y>", String.valueOf(location.getBlockY()))
                .replace("<z>", String.valueOf(location.getBlockZ()))
                .replaceAll("[^A-Za-z0-9_,'+/-]", "");
    }

    public static final class Factory implements GenericFactory<StructureRegion> {
        @Override
        public StructureRegion create(String key, ConfigurationSection config) {
            if (ConfigUtils.getConfigurationSection(config, key).contains("margin")) {
                return new MarginRegion.Factory().create(key, config);
            } else {
                return new SchemeRegion.Factory().create(key, config);
            }
        }
    }

}
