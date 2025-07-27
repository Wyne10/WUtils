package me.wyne.wutils.common;

import org.bukkit.util.Vector;

public final class VectorUtils {

    public static final Vector ZERO_VECTOR = new Vector();

    public static Vector getVector(String string, Vector def) {
        var args = new Args(string, ",");
        var x = args.get(0).isEmpty() ? def.getX() : Double.parseDouble(args.get(0));
        var y = args.get(1).isEmpty() ? def.getY() : Double.parseDouble(args.get(1));
        var z = args.get(2).isEmpty() ? def.getZ() : Double.parseDouble(args.get(2));
        return new Vector(x, y, z);
    }

    public static Vector getVectorOrZero(String string) {
        return getVector(string, ZERO_VECTOR);
    }

    public static String toString(Vector vector) {
        return vector.getX() + "," + vector.getY() + "," + vector.getZ();
    }

}
