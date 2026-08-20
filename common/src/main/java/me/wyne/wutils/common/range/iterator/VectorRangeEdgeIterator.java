package me.wyne.wutils.common.range.iterator;

import me.wyne.wutils.common.range.VectorRange;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates the wireframe of a {@code [min, max]} box: only points lying on its 12 edges, not the
 * interior or the faces. Each edge is visited as a run of {@code step}-sized points along one
 * axis, with the other two axes held fixed at one of their {@code min}/{@code max} combos,
 * inclusive of the far endpoint of each edge.
 * <p>
 * Edges are visited in a fixed order: the 4 edges parallel to X first (at every combination of
 * min/max on Y and Z), then the 4 parallel to Y (at every combination of min/max on X and Z),
 * then the 4 parallel to Z (at every combination of min/max on X and Y). This covers all 12
 * edges of the box exactly once each, with no edge revisited. The corners are therefore visited
 * more than once — each is an endpoint of three edges.
 * <p>
 * Points along an edge are derived as {@code min + index * step} from a count taken once per
 * axis, so floating-point error does not accumulate along an edge; see {@link Steps}. An axis
 * whose extent is inverted contributes no points, and its four edges are skipped.
 * <p>
 * Each call to {@link #next()} returns a freshly allocated {@link Vector}; no instance is reused
 * or mutated across calls. For the full volume instead of just the edges, see
 * {@link VectorRangeIterator}.
 */
public class VectorRangeEdgeIterator implements Iterator<Vector> {

    private final Vector min;
    private final Vector max;
    private final double step;

    private final long countX, countY, countZ;

    private int edgeIndex = 0;
    private long index;
    private double baseX, baseY, baseZ;

    public VectorRangeEdgeIterator(@NotNull Vector min, @NotNull Vector max, double step) {
        this.min = min;
        this.max = max;
        this.step = step;

        this.countX = Steps.count(min.getX(), max.getX(), step);
        this.countY = Steps.count(min.getY(), max.getY(), step);
        this.countZ = Steps.count(min.getZ(), max.getZ(), step);

        resetEdge();
    }

    public VectorRangeEdgeIterator(@NotNull VectorRange range, double step) {
        this(range.getMin(), range.getMax(), step);
    }

    /** Positions the cursor at the start of {@link #edgeIndex}, skipping edges that hold no points. */
    @SuppressWarnings("DuplicateBranchesInSwitch")
    private void resetEdge() {
        while (edgeIndex < 12) {
            switch (edgeIndex) {
                // Edges parallel to X-axis (4 edges)
                case 0: baseX = min.getX(); baseY = min.getY(); baseZ = min.getZ(); break;
                case 1: baseX = min.getX(); baseY = min.getY(); baseZ = max.getZ(); break;
                case 2: baseX = min.getX(); baseY = max.getY(); baseZ = min.getZ(); break;
                case 3: baseX = min.getX(); baseY = max.getY(); baseZ = max.getZ(); break;

                // Edges parallel to Y-axis (4 edges)
                case 4: baseX = min.getX(); baseY = min.getY(); baseZ = min.getZ(); break;
                case 5: baseX = max.getX(); baseY = min.getY(); baseZ = min.getZ(); break;
                case 6: baseX = min.getX(); baseY = min.getY(); baseZ = max.getZ(); break;
                case 7: baseX = max.getX(); baseY = min.getY(); baseZ = max.getZ(); break;

                // Edges parallel to Z-axis (4 edges)
                case 8:  baseX = min.getX(); baseY = min.getY(); baseZ = min.getZ(); break;
                case 9:  baseX = max.getX(); baseY = min.getY(); baseZ = min.getZ(); break;
                case 10: baseX = min.getX(); baseY = max.getY(); baseZ = min.getZ(); break;
                case 11: baseX = max.getX(); baseY = max.getY(); baseZ = min.getZ(); break;
            }
            index = 0;
            if (edgeCount() > 0)
                return;
            edgeIndex++;
        }
    }

    /** Number of points on the current edge, taken from whichever axis that edge runs along. */
    private long edgeCount() {
        if (edgeIndex < 4) return countX;
        if (edgeIndex < 8) return countY;
        return countZ;
    }

    @Override
    public boolean hasNext() {
        return edgeIndex < 12;
    }

    @Override
    public @NotNull Vector next() {
        if (!hasNext())
            throw new NoSuchElementException();

        Vector point;
        if (edgeIndex < 4)
            point = new Vector(Steps.at(min.getX(), max.getX(), step, index), baseY, baseZ);
        else if (edgeIndex < 8)
            point = new Vector(baseX, Steps.at(min.getY(), max.getY(), step, index), baseZ);
        else
            point = new Vector(baseX, baseY, Steps.at(min.getZ(), max.getZ(), step, index));

        if (++index >= edgeCount()) {
            edgeIndex++;
            resetEdge();
        }

        return point;
    }

}
