package me.wyne.wutils.common.range.iterator;

import me.wyne.wutils.common.vector.VectorUtils;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates {@code segments} points evenly spaced around a circle of {@code radius} centered on
 * {@code center}, starting at {@code startAngle} degrees. Each point is computed directly from
 * its angle with trigonometry, then optionally rotated by yaw/pitch/roll — there is no
 * block-grid snapping, so points are continuous doubles, not discretized to block coordinates.
 * <p>
 * Every yielded point is exactly {@code radius} away from {@code center} (angle-based
 * discretization only; the radius itself is never approximated). The {@code segments} points
 * span angles {@code [startAngle, startAngle + 360 * (segments - 1) / segments)} degrees, so the
 * circle is not closed by a duplicate of the first point.
 * <p>
 * The circle lies in the local XY-plane before rotation (Z is 0 relative to {@code center}),
 * then {@code yaw} rotates it around Y, {@code pitch} around X and {@code roll} around Z, in
 * that order, before translating to {@code center}. Each call to {@link #next()} returns a
 * freshly allocated {@link Vector}; no instance is reused across calls.
 */
public class CircleEdgeIterator implements Iterator<Vector> {

    private final Vector center;
    private final double radius;
    private final double startAngleRadians;
    private final int segments;
    private final double step;
    private final double yawRadians;
    private final double pitchRadians;
    private final double rollRadians;

    private int index = 0;

    public CircleEdgeIterator(@NotNull Vector center, double radius, double startAngle, int segments, double yaw, double pitch, double roll) {
        this.center = center;
        this.radius = radius;
        this.startAngleRadians = Math.toRadians(startAngle);
        this.segments = segments;
        this.step = (2.0 * Math.PI) / segments;
        this.yawRadians = Math.toRadians(yaw);
        this.pitchRadians = Math.toRadians(pitch);
        this.rollRadians = Math.toRadians(roll);
    }

    @Override
    public boolean hasNext() {
        return index < segments;
    }

    @Override
    public @NotNull Vector next() {
        if (!hasNext())
            throw new NoSuchElementException();

        double angle = startAngleRadians + index * step;
        index++;

        double x = Math.cos(angle) * radius;
        double y = Math.sin(angle) * radius;
        double z = 0.0;

        Vector v = new Vector(x, y, z);

        if (yawRadians != 0.0)   v = v.rotateAroundY(yawRadians);
        if (pitchRadians != 0.0) v = v.rotateAroundX(pitchRadians);
        if (rollRadians != 0.0)  v = v.rotateAroundZ(rollRadians);

        return v.add(center);
    }

    /**
     * Fluent builder for {@link CircleEdgeIterator}. Unset fields default to a unit circle of 1
     * segment centered at the origin, with no rotation.
     */
    public static class Builder {

        private Vector center = VectorUtils.zero();
        private double radius = 1.0;
        private double startAngle = 0.0;
        private int segments = 1;
        private double yaw = 0.0;
        private double pitch = 0.0;
        private double roll = 0.0;

        public Builder() {
        }

        public Builder(@NotNull Vector center, double radius) {
            this.center = center;
            this.radius = radius;
        }

        public Builder(@NotNull Vector center, double radius, int segments) {
            this.center = center;
            this.radius = radius;
            this.segments = segments;
        }

        public @NotNull Builder setCenter(@NotNull Vector center) {
            this.center = center;
            return this;
        }

        public @NotNull Builder setRadius(double radius) {
            this.radius = radius;
            return this;
        }

        public @NotNull Builder setStartAngle(double startAngle) {
            this.startAngle = startAngle;
            return this;
        }

        public @NotNull Builder setSegments(int segments) {
            this.segments = segments;
            return this;
        }

        public @NotNull Builder setYaw(double yaw) {
            this.yaw = yaw;
            return this;
        }

        public @NotNull Builder setPitch(double pitch) {
            this.pitch = pitch;
            return this;
        }

        public @NotNull Builder setRoll(double roll) {
            this.roll = roll;
            return this;
        }

        public @NotNull CircleEdgeIterator build() {
            return new CircleEdgeIterator(center, radius, startAngle, segments, yaw, pitch, roll);
        }

    }

}
