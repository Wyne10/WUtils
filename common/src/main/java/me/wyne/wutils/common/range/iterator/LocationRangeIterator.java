package me.wyne.wutils.common.range.iterator;

import me.wyne.wutils.common.location.LocationUtils;
import me.wyne.wutils.common.range.LocationRange;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Iterates every point of a {@code [min, max]} box in {@code world} at {@code step}-sized
 * intervals, wrapping each point into a {@link Location}.
 * <p>
 * Delegates to {@link VectorRangeIterator}, so axis order (X fastest, then Y, then Z slowest),
 * inclusivity, step arithmetic and empty-range behavior are that class's — see it for details.
 * <p>
 * Each call to {@link #next()} returns a freshly allocated {@link Location}; no instance is
 * reused or mutated across calls.
 */
public class LocationRangeIterator implements Iterator<Location> {

    private final World world;
    private final VectorRangeIterator points;

    public LocationRangeIterator(@NotNull World world, @NotNull Vector min, @NotNull Vector max, double step) {
        this.world = world;
        this.points = new VectorRangeIterator(min, max, step);
    }

    public LocationRangeIterator(@NotNull LocationRange range, double step) {
        this(range.getWorld(), range.getMin(), range.getMax(), step);
    }

    @Override
    public boolean hasNext() {
        return points.hasNext();
    }

    @Override
    public @NotNull Location next() {
        return LocationUtils.of(world, points.next());
    }

}
