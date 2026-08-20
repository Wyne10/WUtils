package me.wyne.wutils.structure.modifier;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.jetbrains.annotations.NotNull;

/**
 * Runs in the structure creation pipeline to reshape the WorldGuard protection region before
 * region conditions are evaluated against it.
 *
 * <p>WorldGuard regions are immutable in their bounds, so an implementation that changes shape
 * builds a new {@link ProtectedCuboidRegion} and copies the old one's id, flags and priority onto
 * it rather than mutating in place; the returned region is the one used downstream.</p>
 *
 * <p>Invoked directly by {@link me.wyne.wutils.structure.Structure#create}; an implementation
 * that throws propagates to the caller rather than being caught and skipped.</p>
 */
public interface RegionModifier {

    /**
     * Returns the region to use in place of {@code region}.
     *
     * <p>{@code clipboardRegion} is the pasted clipboard's footprint in world space — the reference
     * geometry for implementations that size themselves against the structure rather than against
     * the current region bounds.</p>
     */
    @NotNull ProtectedCuboidRegion apply(@NotNull ProtectedCuboidRegion region, @NotNull Region clipboardRegion);
}
