package me.wyne.wutils.common.vector;

import me.wyne.wutils.common.Args;
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

}
