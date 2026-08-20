package me.wyne.wutils.structure.region.condition;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.structure.IntermediateStructure;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Requires every WorldGuard region overlapping the structure's protected region to be in
 * {@code regions}. Requires WorldGuard.
 */
public record RegionWhitelistCondition(@NotNull Set<@NotNull String> regions) implements RegionCondition {
    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder()
                .appendCollection(depth, "region-whitelist", regions)
                .buildNoTrail();
    }

    /**
     * Fetches every existing WorldGuard region applicable to {@code region} and returns
     * whether all of them are contained in {@link #regions()}.
     */
    @Override
    public boolean isValid(@NotNull IntermediateStructure intermediateStructure, @NotNull ProtectedCuboidRegion region) {
        var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        Preconditions.checkNotNull(intermediateStructure.clipboardRegion().getWorld(), "Intermediate structure " + intermediateStructure.uniqueKey() + " clipboard region world was null");
        var regions = container.get(intermediateStructure.clipboardRegion().getWorld());
        Preconditions.checkNotNull(regions, "Regions manager for " + intermediateStructure.uniqueKey() + " was null");
        var applicableRegions = regions.getApplicableRegions(region);
        return applicableRegions.getRegions().stream().allMatch(r -> this.regions.contains(r.getId()));
    }
}
