package me.wyne.wutils.common.range;

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

}
