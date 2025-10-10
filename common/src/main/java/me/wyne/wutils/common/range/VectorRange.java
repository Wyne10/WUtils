package me.wyne.wutils.common.range;

import me.wyne.wutils.common.vector.VectorUtils;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class VectorRange extends Range<Vector> {

    public VectorRange(Vector min, Vector max) {
        super(VectorUtils.getMin(min, max), VectorUtils.getMax(min, max));
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
