package me.wyne.wutils.common.vector;

import me.wyne.wutils.common.Args;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public final class VectorUtils {

    public static Vector zero() {
        return new Vector();
    }

    public static Vector getVector(String string, Vector def) {
        var args = new Args(string, ",");
        var x = args.get(0).isEmpty() ? def.getX() : Double.parseDouble(args.get(0));
        var y = args.get(1).isEmpty() ? def.getY() : Double.parseDouble(args.get(1));
        var z = args.get(2).isEmpty() ? def.getZ() : Double.parseDouble(args.get(2));
        return new Vector(x, y, z);
    }

    public static Vector getVectorOrZero(String string) {
        return getVector(string, zero());
    }

    public static String toString(Vector vector) {
        return vector.getX() + "," + vector.getY() + "," + vector.getZ();
    }

    public static Vector getMin(Vector vector1, Vector vector2) {
        return new Vector(
                Math.min(vector1.getX(), vector2.getX()),
                Math.min(vector1.getY(), vector2.getY()),
                Math.min(vector1.getZ(), vector2.getZ())
        );
    }

    public static Vector getMax(Vector vector1, Vector vector2) {
        return new Vector(
                Math.max(vector1.getX(), vector2.getX()),
                Math.max(vector1.getY(), vector2.getY()),
                Math.max(vector1.getZ(), vector2.getZ())
        );
    }

    public static boolean isEmpty(Vector vector) {
        return vector.getX() == 0.0 && vector.getY() == 0.0 && vector.getZ() == 0.0;
    }

    public static Vector addRelative(Vector vector, double horizontal, double vertical, BlockFace face) {
        if (face.getModZ() != 0)
            return vector.clone().add(new Vector(horizontal, vertical, 0.0));
        else if (face.getModX() != 0)
            return  vector.clone().add(new Vector(0.0, vertical, horizontal));
        else
            return vector.clone().add(new Vector(horizontal, vertical, 0.0));
    }

    public static Vector addRelative(Vector vector, double width, double height, double depth, BlockFace face) {
        if (face.getModZ() != 0)
            return vector.clone().add(new Vector(width, height, depth));
        else if (face.getModX() != 0)
            return  vector.clone().add(new Vector(depth, height, width));
        else
            return vector.clone().add(new Vector(width, height, depth));
    }

    public static Vector addRelative(Vector vector, Vector relativeOffset, BlockFace face) {
        return addRelative(vector, relativeOffset, face.getDirection());
    }

    public static Vector addRelative(Vector vector, Vector relativeOffset, Vector forward) {
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
