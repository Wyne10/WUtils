package me.wyne.wutils.structure.modifier.region;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.structure.modifier.RegionModifier;
import org.jetbrains.annotations.NotNull;

/**
 * Base for {@link RegionModifier}s that resize a region by a computed delta.
 */
public abstract class DeltaRegionModifier<V> extends ConfigurableAttribute<V> implements RegionModifier {

    protected DeltaRegionModifier(@NotNull String key, @NotNull V value) {
        super(key, value);
    }

    /**
     * Rebuilds the region with new bounds, preserving id, flags and priority via
     * {@link ProtectedCuboidRegion#copyFrom}. WorldGuard regions are immutable in their bounds,
     * so this is the only way to change shape.
     */
    protected @NotNull ProtectedCuboidRegion resize(@NotNull ProtectedCuboidRegion region, @NotNull BlockVector3 min, @NotNull BlockVector3 max) {
        var newRegion = new ProtectedCuboidRegion(region.getId(), region.isTransient(), min, max);
        newRegion.copyFrom(region);
        return newRegion;
    }

    /**
     * Clamps each component of {@code v} to be non-negative. Combined with {@link #negative},
     * this is what keeps an expand/contract delta growing only in the direction's sign rather
     * than shrinking the opposite bound.
     */
    protected static @NotNull BlockVector3 positive(@NotNull BlockVector3 v) {
        return BlockVector3.at(Math.max(v.getX(), 0), Math.max(v.getY(), 0), Math.max(v.getZ(), 0));
    }

    /**
     * Clamps each component of {@code v} to be non-positive. See {@link #positive}.
     */
    protected static @NotNull BlockVector3 negative(@NotNull BlockVector3 v) {
        return BlockVector3.at(Math.min(v.getX(), 0), Math.min(v.getY(), 0), Math.min(v.getZ(), 0));
    }
}