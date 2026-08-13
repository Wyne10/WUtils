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

public class LocationRange extends VectorRange {

    private final World world;

    public LocationRange(World world, Vector min, Vector max) {
        super(min, max);
        this.world = world;
    }

    public LocationRange(World world, VectorRange range) {
        super(range.getMin(), range.getMax());
        this.world = world;
    }

    public LocationRange(Location center, double width, double height, double depth) {
        super(center.toVector(), width, height, depth);
        this.world = center.getWorld();
    }

    public LocationRange(Location center, double radius) {
        this(center, radius, radius, radius);
    }

    public World getWorld() {
        return world;
    }

    public Location getRandomLocation() {
        return LocationUtils.of(getWorld(), getRandom());
    }

    public boolean contains(Location location) {
        if (location.getWorld() != world) return false;
        return contains(location.toVector());
    }

    public <T extends Entity> boolean contains(T entity) {
        return contains(entity.getLocation());
    }

    public boolean contains(Block block) {
        return contains(block.getLocation());
    }

    public LocationRangeIterator locationIterator() {
        return locationIterator(1.0);
    }

    public LocationRangeIterator locationIterator(double step) {
        return new LocationRangeIterator(this, step);
    }

    @Override
    public String toString() {
        return world.getName() + " " + super.toString();
    }

    public static LocationRange getLocationRange(String string) {
        var args = new Args(string);
        return new LocationRange(Bukkit.getWorld(args.get(0)), VectorRange.getVectorRange(args.get(1)));
    }

}
