package me.wyne.wutils.common.range.iterator;

import me.wyne.wutils.common.range.VectorRange;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates every point of a {@code [min, max]} box at {@code step}-sized intervals on each axis,
 * inclusive of {@code max} on every axis. Traversal order is nested: X varies fastest, then Y,
 * then Z varies slowest — equivalent to a triple loop with X as the innermost loop and Z as the
 * outermost.
 * <p>
 * Each axis is counted once up front and its coordinate derived as {@code min + index * step}, so
 * floating-point error does not accumulate along a row or across rows; see {@link Steps}.
 * <p>
 * Each call to {@link #next()} returns a freshly allocated {@link Vector}; no instance is reused
 * or mutated across calls, so results are safe to collect into a list.
 * <p>
 * A range whose {@code min} exceeds {@code max} on any axis is empty — {@link #hasNext()} is
 * {@code false} from construction.
 */
public class VectorRangeIterator implements Iterator<Vector> {

    private final Vector min;
    private final Vector max;
    private final double step;

    private final long countX, countY, countZ;
    private long indexX, indexY, indexZ;
    private boolean hasNext;

    public VectorRangeIterator(@NotNull Vector min, @NotNull Vector max, double step) {
        this.min = min;
        this.max = max;
        this.step = step;

        this.countX = Steps.count(min.getX(), max.getX(), step);
        this.countY = Steps.count(min.getY(), max.getY(), step);
        this.countZ = Steps.count(min.getZ(), max.getZ(), step);
        this.hasNext = countX > 0 && countY > 0 && countZ > 0;
    }

    public VectorRangeIterator(@NotNull VectorRange range, double step) {
        this(range.getMin(), range.getMax(), step);
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public @NotNull Vector next() {
        if (!hasNext())
            throw new NoSuchElementException();

        Vector current = new Vector(
                Steps.at(min.getX(), max.getX(), step, indexX),
                Steps.at(min.getY(), max.getY(), step, indexY),
                Steps.at(min.getZ(), max.getZ(), step, indexZ)
        );

        if (++indexX >= countX) {
            indexX = 0;
            if (++indexY >= countY) {
                indexY = 0;
                if (++indexZ >= countZ)
                    hasNext = false;
            }
        }

        return current;
    }

}
