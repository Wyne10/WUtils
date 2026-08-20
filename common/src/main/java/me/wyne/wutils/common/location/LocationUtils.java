package me.wyne.wutils.common.location;

import me.wyne.wutils.common.vector.VectorUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Location-building and relative-offset helpers. The {@code addRelative} overloads rotate an
 * offset (given either as separate axis components/{@link BlockFace} or as a {@link Vector}
 * relative to a facing direction) into world space, mirroring the equivalent overloads in
 * {@link VectorUtils}.
 */
public final class LocationUtils {

    /** Builds a {@link Location} in {@code world} at {@code vector}'s coordinates, with no direction set. */
    public static @NotNull Location of(@NotNull World world, @NotNull Vector vector) {
        return new Location(world, vector.getX(), vector.getY(), vector.getZ());
    }

    /** @see #of(World, Vector) */
    public static @NotNull Location of(@NotNull World world, @NotNull Vector vector, @NotNull Vector direction) {
        var location = of(world, vector);
        location.setDirection(direction);
        return location;
    }

    /**
     * Offsets {@code location} by {@code horizontal}/{@code vertical} rotated so that
     * {@code horizontal} runs along the axis {@code face} is not aligned with.
     *
     * @see #addRelative(Location, double, double, double, BlockFace)
     */
    public static @NotNull Location addRelative(@NotNull Location location, double horizontal, double vertical, @NotNull BlockFace face) {
        if (face.getModZ() != 0)
            return location.clone().add(horizontal, vertical, 0.0);
        else if (face.getModX() != 0)
            return  location.clone().add(0.0, vertical, horizontal);
        else
            return location.clone().add(horizontal, vertical, 0.0);
    }

    /**
     * Offsets {@code location} by {@code width}/{@code height}/{@code depth}, swapping
     * {@code width} and {@code depth} depending on which axis {@code face} runs along, so the
     * offset stays oriented relative to {@code face} rather than to world axes.
     */
    public static @NotNull Location addRelative(@NotNull Location location, double width, double height, double depth, @NotNull BlockFace face) {
        if (face.getModZ() != 0)
            return location.clone().add(width, height, depth);
        else if (face.getModX() != 0)
            return location.clone().add(depth, height, width);
        else
            return location.clone().add(width, height, depth);
    }

    /** Offsets {@code location} by {@code relativeOffset}, rotated relative to {@code location}'s own direction. */
    public static @NotNull Location addRelative(@NotNull Location location, @NotNull Vector relativeOffset) {
        return addRelative(location, relativeOffset, location.getDirection());
    }

    /** Offsets {@code location} by {@code relativeOffset}, rotated relative to {@code face}'s direction. */
    public static @NotNull Location addRelative(@NotNull Location location, @NotNull Vector relativeOffset, @NotNull BlockFace face) {
        return addRelative(location, relativeOffset, face.getDirection());
    }

    /**
     * Offsets {@code location} by {@code relativeOffset} (x = right, y = up, z = forward)
     * rotated so {@code forward} points along the given {@code forward} direction vector.
     */
    public static @NotNull Location addRelative(@NotNull Location location, @NotNull Vector relativeOffset, @NotNull Vector forward) {
        if (VectorUtils.isEmpty(relativeOffset))
            return location.clone();
        var up = BlockFace.UP.getDirection().clone();
        var right = forward.clone().crossProduct(up).normalize();
        var worldOffset = right.multiply(relativeOffset.getX())
                .add(up.multiply(relativeOffset.getY()))
                .add(forward.multiply(relativeOffset.getZ()));
        return location.clone().add(worldOffset);
    }

    /** Picks a uniformly random point on the circle of the given {@code radius} around {@code center}, at the same Y. */
    public static @NotNull Location getRandomPointNear(@NotNull Location center, int radius) {
        double angle = ThreadLocalRandom.current().nextDouble(0, Math.PI * 2);
        int x = (int) Math.round(Math.cos(angle) * radius);
        int z = (int) Math.round(Math.sin(angle) * radius);
        return center.clone().add(x, 0, z);
    }

}
