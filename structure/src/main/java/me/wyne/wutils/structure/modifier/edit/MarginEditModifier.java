package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.function.mask.Mask;
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

public abstract class MarginEditModifier<V> extends ConfigurableAttribute<V> implements EditSessionModifier, RegionModifier {

    protected MarginEditModifier(@NotNull String key, @NotNull V value) {
        super(key, value);
    }

    protected abstract int margin();

    protected abstract void applyEdit(@NotNull EditSession editSession, @NotNull Region region, @NotNull Mask ringMask);

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
        editSession.setMask(previousMask == null
                ? outsideClipboard
                : new MaskIntersection(previousMask, outsideClipboard));
        try {
            applyEdit(editSession, expanded, outsideClipboard);
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
