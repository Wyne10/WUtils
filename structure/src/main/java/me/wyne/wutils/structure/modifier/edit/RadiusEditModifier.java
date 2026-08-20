package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.structure.modifier.EditSessionModifier;
import org.jetbrains.annotations.NotNull;

/**
 * Base for edit modifiers that apply a sphere-shaped operation centred on the pasted region.
 *
 * <p>The attribute's {@link #getValue() value} is the sphere radius. {@link #apply} resolves the
 * centre from {@link Region#getCenter()} and delegates to {@link #applyAt}, which subclasses
 * implement with the actual WorldEdit call. A {@link MaxChangedBlocksException} thrown by that
 * call is not propagated as-is; it is wrapped in a {@link RuntimeException} naming this
 * modifier's {@link #getKey() key}, so a caller sees which config entry overran the block-change
 * limit.</p>
 */
public abstract class RadiusEditModifier extends ConfigurableAttribute<Double> implements EditSessionModifier {

    protected RadiusEditModifier(@NotNull String key, @NotNull Double value) {
        super(key, value);
    }

    @Override
    public void apply(@NotNull EditSession editSession, @NotNull Region region) {
        BlockVector3 center = region.getCenter().toBlockPoint();
        try {
            applyAt(editSession, region, center, getValue());
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException("Modifier '" + getKey() + "' is changing too many blocks", e);
        }
    }

    /**
     * Performs the sphere-shaped edit around {@code center} with the given {@code radius}.
     *
     * <p>Any {@link MaxChangedBlocksException} thrown here is turned by {@link #apply} into a
     * {@link RuntimeException} identifying this modifier, so implementations need not catch it
     * themselves unless they want to react to it directly.</p>
     */
    protected abstract void applyAt(@NotNull EditSession editSession, @NotNull Region region,
                                    @NotNull BlockVector3 center, double radius) throws MaxChangedBlocksException;

    /**
     * Returns {@code center}'s X/Z projected onto the bottom of {@code region} — the base of the
     * full-height column subclasses scan when an operation (snow, thaw, foliage growth) needs to
     * walk a whole vertical slice rather than a single point.
     */
    protected static @NotNull BlockVector3 columnBase(@NotNull Region region, @NotNull BlockVector3 center) {
        return BlockVector3.at(center.getBlockX(), region.getMinimumPoint().getBlockY(), center.getBlockZ());
    }

    /**
     * Returns the Y one above {@code region}'s top — the exclusive upper bound paired with
     * {@link #columnBase} for column-scanning operations.
     */
    protected static int columnTop(@NotNull Region region) {
        return region.getMaximumPoint().getBlockY() + 1;
    }
}
