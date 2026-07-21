package me.wyne.wutils.structure.region;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class StructureRegion implements CompositeConfigurable {

    private final RegionData regionData;

    public StructureRegion(@NotNull RegionData regionData) {
        this.regionData = regionData;
    }

    public @NotNull RegionData getRegionData() {
        return regionData;
    }

    public abstract @NotNull ProtectedCuboidRegion getRegion(@NotNull Clipboard clipboard, @NotNull Location location);

    @Override
    public void fromConfig(@Nullable Object configObject) {
        throw new UnsupportedOperationException("StructureRegion is deserialized via StructureRegion.Factory");
    }

    public @NotNull String validateId(@NotNull String id, @NotNull Location location) {
        return id.replace("<x>", String.valueOf(location.getBlockX()))
                .replace("<y>", String.valueOf(location.getBlockY()))
                .replace("<z>", String.valueOf(location.getBlockZ()))
                .replace(".0", "")
                .replace(",", "-");
    }

    public static final class Factory implements GenericFactory<StructureRegion> {
        @Override
        public StructureRegion create(String key, ConfigurationSection config) {
            if (ConfigUtils.getConfigurationSection(config, key).contains("radius")) {
                return new RadiusRegion.Factory().create(key, config);
            } else {
                return new SchemeRegion.Factory().create(key, config);
            }
        }
    }

}
