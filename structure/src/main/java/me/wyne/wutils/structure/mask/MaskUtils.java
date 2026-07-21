package me.wyne.wutils.structure.mask;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.mask.Mask;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public final class MaskUtils {

    public static @NotNull Mask parseMask(@NotNull String input, @NotNull World world) {
        ParserContext context = new ParserContext();
        context.setWorld(BukkitAdapter.adapt(world));
        context.setRestricted(false);
        try {
            return WorldEdit.getInstance().getMaskFactory().parseFromInput(input, context);
        } catch (InputParseException e) {
            throw new IllegalArgumentException("Invalid mask '" + input + "': " + e.getMessage(), e);
        }
    }

    public static @NotNull Mask parseMask(@NotNull String input, @NotNull com.sk89q.worldedit.world.World world) {
        ParserContext context = new ParserContext();
        context.setWorld(world);
        context.setRestricted(false);
        try {
            return WorldEdit.getInstance().getMaskFactory().parseFromInput(input, context);
        } catch (InputParseException e) {
            throw new IllegalArgumentException("Invalid mask '" + input + "': " + e.getMessage(), e);
        }
    }

}
