package me.wyne.wutils.common.range;

import me.wyne.wutils.common.vector.VectorUtils;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.NoSuchElementException;

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

    public CircleEdgeIterator(Vector center, double radius, double startAngle, int segments, double yaw, double pitch, double roll) {
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
    public Vector next() {
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

        public Builder(Vector center, double radius) {
            this.center = center;
            this.radius = radius;
        }

        public Builder(double radius, Vector center, int segments) {
            this.radius = radius;
            this.center = center;
            this.segments = segments;
        }

        public void setCenter(Vector center) {
            this.center = center;
        }

        public void setRadius(double radius) {
            this.radius = radius;
        }

        public void setStartAngle(double startAngle) {
            this.startAngle = startAngle;
        }

        public void setSegments(int segments) {
            this.segments = segments;
        }

        public void setYaw(double yaw) {
            this.yaw = yaw;
        }

        public void setPitch(double pitch) {
            this.pitch = pitch;
        }

        public void setRoll(double roll) {
            this.roll = roll;
        }

        public CircleEdgeIterator build() {
            return new CircleEdgeIterator(center, radius, startAngle, segments, yaw, pitch, roll);
        }

    }

}
