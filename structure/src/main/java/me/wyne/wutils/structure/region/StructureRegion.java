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

/**
 * Builds the WorldGuard {@link ProtectedCuboidRegion} shape a structure is protected by.
 *
 * <p>{@link #getRegion} constructs a {@link ProtectedCuboidRegion} around the pasted
 * clipboard's world-space bounds; subclasses differ only in how those bounds are adjusted
 * (see {@link SchemeRegion}, {@link MarginRegion}). Requires WorldEdit and WorldGuard.</p>
 */
public abstract class StructureRegion implements CompositeConfigSerializable {

    private final RegionData regionData;

    public StructureRegion(@NotNull RegionData regionData) {
        this.regionData = regionData;
    }

    public @NotNull RegionData getRegionData() {
        return regionData;
    }

    /**
     * Builds the protected region for a structure pasted at {@code location} with
     * {@code transform} applied, sized around {@code clipboard}'s bounds.
     */
    public abstract @NotNull ProtectedCuboidRegion getRegion(@NotNull Clipboard clipboard, @NotNull Location location, @NotNull Transform transform);

    /**
     * Substitutes {@code <x>}/{@code <y>}/{@code <z>} in {@code id} with {@code location}'s
     * block coordinates, then strips every character WorldGuard rejects in a region id.
     */
    public static @NotNull String validateId(@NotNull String id, @NotNull Location location) {
        return id.replace("<x>", String.valueOf(location.getBlockX()))
                .replace("<y>", String.valueOf(location.getBlockY()))
                .replace("<z>", String.valueOf(location.getBlockZ()))
                .replaceAll("[^A-Za-z0-9_,'+/-]", "");
    }

    /**
     * Builds a {@link StructureRegion} from config: a {@code margin} key selects
     * {@link MarginRegion}, otherwise {@link SchemeRegion}.
     */
    public static final class Factory implements GenericFactory<StructureRegion> {
        @Override
        public @NotNull StructureRegion create(@NotNull String key, @NotNull ConfigurationSection config) {
            if (ConfigUtils.getConfigurationSection(config, key).contains("margin")) {
                return new MarginRegion.Factory().create(key, config);
            } else {
                return new SchemeRegion.Factory().create(key, config);
            }
        }
    }

}
