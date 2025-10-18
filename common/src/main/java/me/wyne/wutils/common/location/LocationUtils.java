package me.wyne.wutils.common.location;

import me.wyne.wutils.common.vector.VectorUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public final class LocationUtils {

    public static Location of(World world, Vector vector) {
        return new Location(world, vector.getX(), vector.getY(), vector.getZ());
    }

    public static Location of(World world, Vector vector, Vector direction) {
        var location = of(world, vector);
        location.setDirection(direction);
        return location;
    }

    public static Location addRelative(Location location, double horizontal, double vertical, BlockFace face) {
        if (face.getModZ() != 0)
            return location.clone().add(horizontal, vertical, 0.0);
        else if (face.getModX() != 0)
            return  location.clone().add(0.0, vertical, horizontal);
        else
            return location.clone().add(horizontal, vertical, 0.0);
    }

    public static Location addRelative(Location location, double width, double height, double depth, BlockFace face) {
        if (face.getModZ() != 0)
            return location.clone().add(width, height, depth);
        else if (face.getModX() != 0)
            return location.clone().add(depth, height, width);
        else
            return location.clone().add(width, height, depth);
    }

    public static Location addRelative(Location location, Vector relativeOffset) {
        return addRelative(location, relativeOffset, location.getDirection());
    }

    public static Location addRelative(Location location, Vector relativeOffset, BlockFace face) {
        return addRelative(location, relativeOffset, face.getDirection());
    }

    public static Location addRelative(Location location, Vector relativeOffset, Vector forward) {
        if (VectorUtils.isEmpty(relativeOffset))
            return location.clone();
        var up = BlockFace.UP.getDirection().clone();
        var right = forward.clone().crossProduct(up).normalize();
        var worldOffset = right.multiply(relativeOffset.getX())
                .add(up.multiply(relativeOffset.getY()))
                .add(forward.multiply(relativeOffset.getZ()));
        return location.clone().add(worldOffset);
    }

}
