package me.wyne.wutils.common.range;

import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class VectorRangeIterator implements Iterator<Vector> {

    private final Vector min;
    private final Vector max;
    private final double step;

    private double x, y, z;
    private boolean hasNext = true;

    public VectorRangeIterator(Vector min, Vector max, double step) {
        this.min = min;
        this.max = max;
        this.step = step;

        this.x = min.getX();
        this.y = min.getY();
        this.z = min.getZ();
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Vector next() {
        if (!hasNext())
            throw new NoSuchElementException();

        Vector current = new Vector(x, y, z);

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
