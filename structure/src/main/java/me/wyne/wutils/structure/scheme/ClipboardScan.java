package me.wyne.wutils.structure.scheme;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.world.block.BlockType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record ClipboardScan(Clipboard clipboard) {

    public ClipboardScan(@NotNull Clipboard clipboard) {
        this.clipboard = clipboard;
    }

    @Override
    public @NotNull Clipboard clipboard() {
        return clipboard;
    }

    public @NotNull List<BlockVector3> find(@NotNull Mask mask) {
        var matches = new ArrayList<BlockVector3>();
        for (BlockVector3 pos : clipboard.getRegion()) {
            if (mask.test(pos))
                matches.add(BlockVector3.at(pos.getX(), pos.getY(), pos.getZ()));
        }
        return matches;
    }

    public @NotNull List<BlockVector3> find(@NotNull BlockType @NotNull ... types) {
        return find(new BlockTypeMask(clipboard, types));
    }

    public static @NotNull BlockVector3 toWorld(@NotNull BlockVector3 clipboardPos, @NotNull BlockVector3 origin,
                                                @NotNull BlockVector3 to, @NotNull Transform transform) {
        return to.add(transform.apply(clipboardPos.subtract(origin).toVector3()).toBlockPoint());
    }
}
