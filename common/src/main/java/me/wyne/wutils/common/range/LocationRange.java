package me.wyne.wutils.common.range;

import me.wyne.wutils.common.Args;
import me.wyne.wutils.common.location.LocationUtils;
import me.wyne.wutils.common.range.iterator.LocationRangeIterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link VectorRange} anchored to a specific {@link World}, inclusive of both bounds on every
 * axis (see the {@link VectorRange} class documentation for the exact contract, including the
 * caveats on {@link #getRandom()} and on negative dimensions).
 * <p>
 * Despite the constructor parameter name, {@link #LocationRange(Location, double)} builds a
 * cube extending {@code radius} in every direction, not a sphere — containment is still the
 * axis-aligned box check inherited from {@link VectorRange}, not a distance check.
 */
public class LocationRange extends VectorRange {

    private final World world;

    public LocationRange(@NotNull World world, @NotNull Vector min, @NotNull Vector max) {
        super(min, max);
        this.world = world;
    }

    public LocationRange(@NotNull World world, @NotNull VectorRange range) {
        super(range.getMin(), range.getMax());
        this.world = world;
    }

    public LocationRange(@NotNull Location center, double width, double height, double depth) {
        super(center.toVector(), width, height, depth);
        this.world = center.getWorld();
    }

    /**
     * Builds a cube centered on {@code center}, extending {@code radius} in every direction —
     * not a sphere; see the class documentation.
     */
    public LocationRange(@NotNull Location center, double radius) {
        this(center, radius, radius, radius);
    }

    public @NotNull World getWorld() {
        return world;
    }

    public @NotNull Location getRandomLocation() {
        return LocationUtils.of(getWorld(), getRandom());
    }

    public boolean contains(@NotNull Location location) {
        if (location.getWorld() != world) return false;
        return contains(location.toVector());
    }

    public <T extends Entity> boolean contains(@NotNull T entity) {
        return contains(entity.getLocation());
    }

    public boolean contains(@NotNull Block block) {
        return contains(block.getLocation());
    }

    public @NotNull LocationRangeIterator locationIterator() {
        return locationIterator(1.0);
    }

    /**
     * Iterates the full volume at {@code step}-sized intervals in world coordinates via
     * {@link LocationRangeIterator}.
     */
    public @NotNull LocationRangeIterator locationIterator(double step) {
        return new LocationRangeIterator(this, step);
    }

    @Override
    public @NotNull String toString() {
        return world.getName() + " " + super.toString();
    }

    /**
     * Parses a range from {@code "world minX,minY,minZ..maxX,maxY,maxZ"}.
     */
    public static @NotNull LocationRange getLocationRange(@NotNull String string) {
        var args = new Args(string);
        return new LocationRange(Bukkit.getWorld(args.get(0)), VectorRange.getVectorRange(args.get(1)));
    }

}
