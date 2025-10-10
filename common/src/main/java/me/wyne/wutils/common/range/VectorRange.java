package me.wyne.wutils.common.range;

import me.wyne.wutils.common.vector.VectorUtils;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class VectorRange extends Range<Vector> {

    public VectorRange(Vector min, Vector max) {
        super(VectorUtils.getMin(min, max), VectorUtils.getMax(min, max), min.getMidpoint(max));
    }

    public VectorRange(Vector center, double width, double height, double depth) {
        super(
                center.clone().subtract(new Vector(width / 2, height / 2, depth / 2)),
                center.clone().add(new Vector(width / 2, height / 2, depth / 2)),
                center
        );
    }

    public VectorRange(Vector center, double radius) {
        this(center, radius, radius, radius);
    }

    @Override
    public Vector getRandom() {
        return new Vector(
                ThreadLocalRandom.current().nextDouble(getMin().getX(), getMax().getX()),
                ThreadLocalRandom.current().nextDouble(getMin().getY(), getMax().getY()),
                ThreadLocalRandom.current().nextDouble(getMin().getZ(), getMax().getZ())
        );
    }

    @Override
    public boolean contains(Vector value) {
        return (value.getX() >= getMin().getX() && value.getX() <= getMax().getX())
                && (value.getY() >= getMin().getY() && value.getY() <= getMax().getY())
                && (value.getZ() >= getMin().getZ() && value.getZ() <= getMax().getZ());
    }

    @Override
    public @NotNull Iterator<Vector> iterator() {
        return new VectorEdgeIterator(getMin(), getMax(), 0.25);
    }

}
