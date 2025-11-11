package me.wyne.wutils.common.range;

import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class VectorRangeEdgeIterator implements Iterator<Vector> {

    private final Vector min;
    private final Vector max;
    private final double step;

    private int edgeIndex = 0;
    private double x, y, z;

    public VectorRangeEdgeIterator(Vector min, Vector max, double step) {
        this.min = min;
        this.max = max;
        this.step = step;
        resetEdge();
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    private void resetEdge() {
        switch (edgeIndex) {
            // Edges parallel to X-axis (4 edges)
            case 0: x = min.getX(); y = min.getY(); z = min.getZ(); break;
            case 1: x = min.getX(); y = min.getY(); z = max.getZ(); break;
            case 2: x = min.getX(); y = max.getY(); z = min.getZ(); break;
            case 3: x = min.getX(); y = max.getY(); z = max.getZ(); break;

            // Edges parallel to Y-axis (4 edges)
            case 4: x = min.getX(); y = min.getY(); z = min.getZ(); break;
            case 5: x = max.getX(); y = min.getY(); z = min.getZ(); break;
            case 6: x = min.getX(); y = min.getY(); z = max.getZ(); break;
            case 7: x = max.getX(); y = min.getY(); z = max.getZ(); break;

            // Edges parallel to Z-axis (4 edges)
            case 8:  x = min.getX(); y = min.getY(); z = min.getZ(); break;
            case 9:  x = max.getX(); y = min.getY(); z = min.getZ(); break;
            case 10: x = min.getX(); y = max.getY(); z = min.getZ(); break;
            case 11: x = max.getX(); y = max.getY(); z = min.getZ(); break;
        }
    }

    @Override
    public boolean hasNext() {
        return edgeIndex < 12;
    }

    @Override
    public Vector next() {
        if (!hasNext())
            throw new NoSuchElementException();
        Vector point = new Vector(x, y, z);

        // Move along the current edge
        switch (edgeIndex) {
            case 0: case 1: case 2: case 3: // X-axis edges
                x += step;
                if (x > max.getX()) { edgeIndex++; resetEdge(); }
                break;

            case 4: case 5: case 6: case 7: // Y-axis edges
                y += step;
                if (y > max.getY()) { edgeIndex++; resetEdge(); }
                break;

            case 8: case 9: case 10: case 11: // Z-axis edges
                z += step;
                if (z > max.getZ()) { edgeIndex++; resetEdge(); }
                break;
        }

        return point;
    }

}
