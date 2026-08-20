package me.wyne.wutils.structure.pattern;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.pattern.Pattern;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Wraps WorldEdit's pattern parser with {@code setRestricted(false)} and rethrows any parse
 * failure as an {@link IllegalArgumentException} naming the offending input. Requires WorldEdit
 * on the classpath.
 *
 * <p>Which overload to call matters: the {@link ParserContext} given to the parser determines
 * which pattern syntaxes resolve. No context means only context-free syntaxes work; a world or
 * extent context unlocks the syntaxes that need one. The two {@code parsePattern(String, World)}
 * overloads differ only in whether {@code World} is a Bukkit or a WorldEdit type. In the
 * world-and-extent overload, {@code setWorld} must be called before {@code setExtent} because it
 * overwrites the extent.</p>
 */
public final class PatternUtils {

    public static @NotNull Pattern parsePattern(@NotNull String input) {
        ParserContext context = new ParserContext();
        context.setRestricted(false);
        try {
            return WorldEdit.getInstance().getPatternFactory().parseFromInput(input, context);
        } catch (InputParseException e) {
            throw new IllegalArgumentException("Invalid pattern '" + input + "': " + e.getMessage(), e);
        }
    }

    public static @NotNull Pattern parsePattern(@NotNull String input, @NotNull World world) {
        ParserContext context = new ParserContext();
        context.setWorld(BukkitAdapter.adapt(world));
        context.setRestricted(false);
        try {
            return WorldEdit.getInstance().getPatternFactory().parseFromInput(input, context);
        } catch (InputParseException e) {
            throw new IllegalArgumentException("Invalid pattern '" + input + "': " + e.getMessage(), e);
        }
    }

    public static @NotNull Pattern parsePattern(@NotNull String input, @NotNull com.sk89q.worldedit.world.World world) {
        ParserContext context = new ParserContext();
        context.setWorld(world);
        context.setRestricted(false);
        try {
            return WorldEdit.getInstance().getPatternFactory().parseFromInput(input, context);
        } catch (InputParseException e) {
            throw new IllegalArgumentException("Invalid pattern '" + input + "': " + e.getMessage(), e);
        }
    }

    public static @NotNull Pattern parsePattern(@NotNull String input, @NotNull Extent extent) {
        ParserContext context = new ParserContext();
        context.setExtent(extent);
        context.setRestricted(false);
        try {
            return WorldEdit.getInstance().getPatternFactory().parseFromInput(input, context);
        } catch (InputParseException e) {
            throw new IllegalArgumentException("Invalid pattern '" + input + "': " + e.getMessage(), e);
        }
    }

    public static @NotNull Pattern parsePattern(@NotNull String input, @NotNull com.sk89q.worldedit.world.World world, @NotNull Extent extent) {
        ParserContext context = new ParserContext();
        // setWorld also sets the extent, so it must come first to keep the explicit extent below.
        context.setWorld(world);
        context.setExtent(extent);
        context.setRestricted(false);
        try {
            return WorldEdit.getInstance().getPatternFactory().parseFromInput(input, context);
        } catch (InputParseException e) {
            throw new IllegalArgumentException("Invalid pattern '" + input + "': " + e.getMessage(), e);
        }
    }

}
