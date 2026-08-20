package me.wyne.wutils.common.vector;

import me.wyne.wutils.common.Args;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * Vector-building, parsing and relative-offset helpers. The {@code addRelative} overloads rotate
 * an offset into world space relative to a facing direction, mirroring the equivalent overloads
 * in {@link me.wyne.wutils.common.location.LocationUtils}.
 */
public final class VectorUtils {

    /** @return a new zero vector; unlike a shared constant, each call returns a distinct mutable instance */
    public static @NotNull Vector zero() {
        return new Vector();
    }

    /**
     * Parses a {@code "x,y,z"}-formatted {@code string} into a {@link Vector}, falling back to
     * the matching component of {@code def} for any axis left blank.
     */
    public static @NotNull Vector getVector(@NotNull String string, @NotNull Vector def) {
        var args = new Args(string, ",");
        var x = args.get(0).isEmpty() ? def.getX() : Double.parseDouble(args.get(0));
        var y = args.get(1).isEmpty() ? def.getY() : Double.parseDouble(args.get(1));
        var z = args.get(2).isEmpty() ? def.getZ() : Double.parseDouble(args.get(2));
        return new Vector(x, y, z);
    }

    /** @see #getVector(String, Vector) */
    public static @NotNull Vector getVectorOrZero(@NotNull String string) {
        return getVector(string, zero());
    }

    /** @return a new vector with the component-wise minimum of {@code vector1} and {@code vector2} */
    public static @NotNull Vector getMin(@NotNull Vector vector1, @NotNull Vector vector2) {
        return new Vector(
                Math.min(vector1.getX(), vector2.getX()),
                Math.min(vector1.getY(), vector2.getY()),
                Math.min(vector1.getZ(), vector2.getZ())
        );
    }

    /** @return a new vector with the component-wise maximum of {@code vector1} and {@code vector2} */
    public static @NotNull Vector getMax(@NotNull Vector vector1, @NotNull Vector vector2) {
        return new Vector(
                Math.max(vector1.getX(), vector2.getX()),
                Math.max(vector1.getY(), vector2.getY()),
                Math.max(vector1.getZ(), vector2.getZ())
        );
    }

    /** @return {@code true} if every component of {@code vector} is exactly zero */
    public static boolean isEmpty(@NotNull Vector vector) {
        return vector.getX() == 0.0 && vector.getY() == 0.0 && vector.getZ() == 0.0;
    }

    /**
     * Offsets {@code vector} by {@code horizontal}/{@code vertical} rotated so that
     * {@code horizontal} runs along the axis {@code face} is not aligned with.
     *
     * @see #addRelative(Vector, double, double, double, BlockFace)
     */
    public static @NotNull Vector addRelative(@NotNull Vector vector, double horizontal, double vertical, @NotNull BlockFace face) {
        if (face.getModZ() != 0)
            return vector.clone().add(new Vector(horizontal, vertical, 0.0));
        else if (face.getModX() != 0)
            return  vector.clone().add(new Vector(0.0, vertical, horizontal));
        else
            return vector.clone().add(new Vector(horizontal, vertical, 0.0));
    }

    /**
     * Offsets {@code vector} by {@code width}/{@code height}/{@code depth}, swapping
     * {@code width} and {@code depth} depending on which axis {@code face} runs along, so the
     * offset stays oriented relative to {@code face} rather than to world axes.
     */
    public static @NotNull Vector addRelative(@NotNull Vector vector, double width, double height, double depth, @NotNull BlockFace face) {
        if (face.getModZ() != 0)
            return vector.clone().add(new Vector(width, height, depth));
        else if (face.getModX() != 0)
            return  vector.clone().add(new Vector(depth, height, width));
        else
            return vector.clone().add(new Vector(width, height, depth));
    }

    /** Offsets {@code vector} by {@code relativeOffset}, rotated relative to {@code face}'s direction. */
    public static @NotNull Vector addRelative(@NotNull Vector vector, @NotNull Vector relativeOffset, @NotNull BlockFace face) {
        return addRelative(vector, relativeOffset, face.getDirection());
    }

    /**
     * Offsets {@code vector} by {@code relativeOffset} (x = right, y = up, z = forward) rotated
     * so {@code forward} points along the given {@code forward} direction vector.
     */
    public static @NotNull Vector addRelative(@NotNull Vector vector, @NotNull Vector relativeOffset, @NotNull Vector forward) {
        if (isEmpty(relativeOffset))
            return vector.clone();
        var up = BlockFace.UP.getDirection().clone();
        var right = forward.clone().crossProduct(up).normalize();
        var worldOffset = right.multiply(relativeOffset.getX())
                .add(up.multiply(relativeOffset.getY()))
                .add(forward.multiply(relativeOffset.getZ()));
        return vector.clone().add(worldOffset);
    }

}
