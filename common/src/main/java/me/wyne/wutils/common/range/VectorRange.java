package me.wyne.wutils.common.range;

import me.wyne.wutils.common.range.iterator.VectorRangeIterator;
import me.wyne.wutils.common.vector.VectorUtils;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * A closed axis-aligned box, inclusive of both bounds on every axis per {@link #contains}.
 * <p>
 * The {@link #VectorRange(Vector, Vector)} constructor normalizes each axis independently via
 * {@link VectorUtils#getMin} / {@link VectorUtils#getMax}, so {@code min} and {@code max} may be
 * given in either order. The {@link #VectorRange(Vector, double, double, double)} and
 * {@link #VectorRange(Vector, double)} constructors do <b>not</b> normalize: a negative width,
 * height or depth produces a box whose stored minimum exceeds its maximum on that axis, which
 * breaks {@link #contains} for that axis.
 * <p>
 * Iterates the full volume via {@link VectorRangeIterator} with a fixed step of {@code 1.0}, in
 * X-fastest, then Y, then Z-slowest order. For a boundary-only traversal see
 * {@link me.wyne.wutils.common.range.iterator.VectorRangeEdgeIterator}.
 */
public class VectorRange extends Range<Vector> {

    public VectorRange(@NotNull Vector min, @NotNull Vector max) {
        super(VectorUtils.getMin(min, max), VectorUtils.getMax(min, max), min.getMidpoint(max), VectorUtils.getMax(min, max).subtract(VectorUtils.getMin(min, max)));
    }

    /**
     * Builds a box centered on {@code center}, extending {@code width}/{@code height}/
     * {@code depth} in total along X/Y/Z respectively (half in each direction). Negative
     * dimensions are not corrected; see the class documentation.
     */
    public VectorRange(@NotNull Vector center, double width, double height, double depth) {
        super(
                center.clone().subtract(new Vector(width / 2, height / 2, depth / 2)),
                center.clone().add(new Vector(width / 2, height / 2, depth / 2)),
                center,
                new Vector(width, height, depth)
        );
    }

    public VectorRange(@NotNull Vector center, double radius) {
        this(center, radius, radius, radius);
    }

    /**
     * Returns a random point in the closed box, sampling each axis independently and inclusively.
     *
     * <p>An axis whose minimum and maximum coincide yields that value rather than throwing, so a
     * flat or degenerate box — a fixed-Y region, a line, a single block — samples correctly.</p>
     */
    @Override
    public @NotNull Vector getRandom() {
        return new Vector(
                randomInclusive(getMin().getX(), getMax().getX()),
                randomInclusive(getMin().getY(), getMax().getY()),
                randomInclusive(getMin().getZ(), getMax().getZ())
        );
    }

    @Override
    public boolean contains(@NotNull Vector value) {
        return (value.getX() >= getMin().getX() && value.getX() <= getMax().getX())
                && (value.getY() >= getMin().getY() && value.getY() <= getMax().getY())
                && (value.getZ() >= getMin().getZ() && value.getZ() <= getMax().getZ());
    }

    @Override
    public @NotNull Iterator<@NotNull Vector> iterator() {
        return new VectorRangeIterator(getMin(), getMax(), 1.0);
    }

    @Override
    public @NotNull String toString() {
        return getMin() + ".." + getMax();
    }

    /**
     * Parses a range from {@code "minX,minY,minZ..maxX,maxY,maxZ"}.
     */
    public static @NotNull VectorRange getVectorRange(@NotNull String string) {
        var split = string.split("\\.\\.");
        return new VectorRange(VectorUtils.getVectorOrZero(split[0]), VectorUtils.getVectorOrZero(split[1]));
    }

}
