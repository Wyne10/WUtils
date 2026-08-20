package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.util.TreeGenerator;
import me.wyne.wutils.common.Args;
import org.jetbrains.annotations.NotNull;

/**
 * Config value for {@link ForestEditModifier}, parsed by {@link #parse} from a single string:
 * {@code margin(0) treeType(tree) density(5) includeClipboard(false)}. {@code treeType} is
 * resolved via {@link TreeGenerator.TreeType#lookup}; an unknown name throws
 * {@link IllegalArgumentException}. {@code includeClipboard} controls whether trees are allowed
 * to grow on the structure's own footprint; when {@code false} the footprint is excluded.
 *
 * <p>Unlike most of its siblings, tokens here are split with {@link Args}'s default
 * colon-or-whitespace delimiter rather than {@link Args#SPACE_DELIMITER}, so a value containing
 * a colon is split there too.</p>
 */
public record ForestSettings(int margin, @NotNull TreeGenerator.TreeType type, double density, boolean includeClipboard) {

    public static @NotNull ForestSettings parse(@NotNull String input) {
        var args = new Args(input);
        int margin = Integer.parseInt(args.get(0, "0"));
        String typeName = args.get(1, "tree");
        TreeGenerator.TreeType type = TreeGenerator.TreeType.lookup(typeName);
        if (type == null)
            throw new IllegalArgumentException("Unknown tree type '" + typeName + "'");
        double density = Double.parseDouble(args.get(2, "5"));
        boolean includeClipboard = Boolean.parseBoolean(args.get(3, "false"));
        return new ForestSettings(margin, type, density, includeClipboard);
    }

    @Override
    public @NotNull String toString() {
        return margin + " " + type.name().toLowerCase() + " " + density + " " + includeClipboard;
    }
}
