package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.Mask2D;
import com.sk89q.worldedit.function.mask.MaskIntersection;
import com.sk89q.worldedit.function.mask.Masks;
import com.sk89q.worldedit.function.mask.RegionMask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.structure.modifier.EditSessionModifier;
import me.wyne.wutils.structure.modifier.RegionModifier;
import org.jetbrains.annotations.NotNull;

/**
 * Base for edit modifiers that reshape a ring of terrain around the pasted structure rather than
 * a sphere around its centre.
 *
 * <p>{@link #apply} clones the pasted {@code region} and expands it by {@link #margin()} blocks
 * on every axis, giving the expanded region to {@link #applyEdit} as the area to work on; the
 * original, unexpanded region is passed through unchanged as {@code clipboardRegion} so
 * subclasses can still tell where the structure itself sits within it.</p>
 *
 * <p>When {@link #excludeFootprint()} is {@code true} (the default), {@code apply} also installs
 * a mask on the {@link EditSession} that excludes the structure's own footprint, via
 * {@link #outsideFootprint}, so the edit reshapes the surroundings without touching the pasted
 * building. That test is 2D (X/Z only), so the exclusion is a full-height column over the
 * footprint, not a box bounded by its Y range. Any mask already set on the session is intersected
 * with — not replaced by — this one, and the previous mask is always restored in a {@code finally}
 * once {@link #applyEdit} returns or throws.</p>
 *
 * <p>A {@code MarginEditModifier} also implements {@link RegionModifier}: {@link #apply(ProtectedCuboidRegion, Region)}
 * grows the WorldGuard protected region to cover the margin the edit is about to touch, so the
 * region stays large enough to protect what this modifier changes.</p>
 *
 * @param <V> the attribute's config value type
 */
public abstract class MarginEditModifier<V> extends ConfigurableAttribute<V> implements EditSessionModifier, RegionModifier {

    protected MarginEditModifier(@NotNull String key, @NotNull V value) {
        super(key, value);
    }

    /**
     * Returns how far, in blocks, to expand the pasted region on every axis before editing.
     */
    protected abstract int margin();

    /**
     * Performs the edit over {@code region} — the pasted region already expanded by
     * {@link #margin()}. {@code clipboardRegion} is the original, unexpanded region, kept
     * available so implementations can locate the structure's own footprint within it.
     */
    protected abstract void applyEdit(@NotNull EditSession editSession, @NotNull Region region, @NotNull Region clipboardRegion);

    /**
     * Returns whether {@link #apply} should mask the structure's own footprint out of the edit.
     * {@code true} by default; subclasses whose config exposes an "include clipboard" toggle
     * override this to reflect it.
     */
    protected boolean excludeFootprint() {
        return true;
    }

    /**
     * Returns a 2D (X/Z-only) mask that accepts positions outside {@code clipboardRegion}'s
     * horizontal bounds — the structure's footprint as a column, ignoring Y.
     */
    protected static @NotNull Mask2D outsideFootprint(@NotNull Region clipboardRegion) {
        var min = clipboardRegion.getMinimumPoint();
        var max = clipboardRegion.getMaximumPoint();
        int minX = min.getBlockX();
        int maxX = max.getBlockX();
        int minZ = min.getBlockZ();
        int maxZ = max.getBlockZ();
        return vector -> {
            int x = vector.getBlockX();
            int z = vector.getBlockZ();
            return x < minX || x > maxX || z < minZ || z > maxZ;
        };
    }

    @Override
    public void apply(@NotNull EditSession editSession, @NotNull Region region) {
        int margin = margin();
        Region expanded = region.clone();
        try {
            expanded.expand(
                    BlockVector3.at(margin, margin, margin),
                    BlockVector3.at(-margin, -margin, -margin));
        } catch (RegionOperationException e) {
            throw new RuntimeException("Modifier '" + getKey() + "' could not expand region", e);
        }

        Mask previousMask = editSession.getMask();
        Mask outsideClipboard = Masks.negate(new RegionMask(region));
        if (excludeFootprint())
            editSession.setMask(previousMask == null
                    ? outsideClipboard
                    : new MaskIntersection(previousMask, outsideClipboard));
        try {
            applyEdit(editSession, expanded, region);
        } finally {
            editSession.setMask(previousMask);
        }
    }

    @Override
    public @NotNull ProtectedCuboidRegion apply(@NotNull ProtectedCuboidRegion region, @NotNull Region clipboardRegion) {
        int margin = margin();
        var smoothMin = clipboardRegion.getMinimumPoint().subtract(margin, margin, margin);
        var smoothMax = clipboardRegion.getMaximumPoint().add(margin, margin, margin);

        var newMin = region.getMinimumPoint().getMinimum(smoothMin);
        var newMax = region.getMaximumPoint().getMaximum(smoothMax);
        if (newMin.equals(region.getMinimumPoint()) && newMax.equals(region.getMaximumPoint()))
            return region;

        var newRegion = new ProtectedCuboidRegion(region.getId(), region.isTransient(), newMin, newMax);
        newRegion.copyFrom(region);
        return newRegion;
    }
}
