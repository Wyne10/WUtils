package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.util.TreeGenerator;
import me.wyne.wutils.common.Args;
import org.jetbrains.annotations.NotNull;

public record ForestSettings(int margin, @NotNull TreeGenerator.TreeType type, double density) {

    public static @NotNull ForestSettings parse(@NotNull String input) {
        var args = new Args(input);
        int margin = Integer.parseInt(args.get(0, "0"));
        String typeName = args.get(1, "tree");
        TreeGenerator.TreeType type = TreeGenerator.TreeType.lookup(typeName);
        if (type == null)
            throw new IllegalArgumentException("Unknown tree type '" + typeName + "'");
        double density = Double.parseDouble(args.get(2, "5"));
        return new ForestSettings(margin, type, density);
    }

    @Override
    public @NotNull String toString() {
        return margin + " " + type.name().toLowerCase() + " " + density;
    }
}
