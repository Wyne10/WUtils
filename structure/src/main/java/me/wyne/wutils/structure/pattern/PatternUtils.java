package me.wyne.wutils.structure.pattern;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.pattern.Pattern;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public final class PatternUtils {

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

}
