package me.wyne.wutils.common.range.iterator;

import me.wyne.wutils.common.location.LocationUtils;
import me.wyne.wutils.common.range.LocationRange;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LocationRangeIterator implements Iterator<Location> {

    private final World world;
    private final Vector min;
    private final Vector max;
    private final double step;

    private double x, y, z;
    private boolean hasNext = true;

    public LocationRangeIterator(World world, Vector min, Vector max, double step) {
        this.world = world;
        this.min = min;
        this.max = max;
        this.step = step;

        this.x = min.getX();
        this.y = min.getY();
        this.z = min.getZ();
    }

    public LocationRangeIterator(LocationRange range, double step) {
        this(range.getWorld(), range.getMin(), range.getMax(), step);
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Location next() {
        if (!hasNext())
            throw new NoSuchElementException();

        Location current = LocationUtils.of(world, new Vector(x, y, z));

        x += step;
        if (x > max.getX()) {
            x = min.getX();
            y += step;
            if (y > max.getY()) {
                y = min.getY();
                z += step;
                if (z > max.getZ()) {
                    hasNext = false;
                }
            }
        }

        return current;
    }

}