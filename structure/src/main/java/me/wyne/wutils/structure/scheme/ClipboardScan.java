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

/**
 * Scans a {@link Clipboard} for block positions matching a {@link Mask} or a set of
 * {@link BlockType}s. Each call re-scans the whole clipboard; see {@link ClipboardScanCache} for a
 * cached, memoized alternative.
 */
public record ClipboardScan(Clipboard clipboard) {

    public ClipboardScan(@NotNull Clipboard clipboard) {
        this.clipboard = clipboard;
    }

    @Override
    public @NotNull Clipboard clipboard() {
        return clipboard;
    }

    /**
     * Returns every clipboard-local position, in region iteration order, where {@code mask} matches.
     */
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

    /**
     * Maps a clipboard-local position to world coordinates: offsets it from {@code origin}, applies
     * {@code transform}, then re-anchors it at {@code to}.
     */
    public static @NotNull BlockVector3 toWorld(@NotNull BlockVector3 clipboardPos, @NotNull BlockVector3 origin,
                                                @NotNull BlockVector3 to, @NotNull Transform transform) {
        return to.add(transform.apply(clipboardPos.subtract(origin).toVector3()).toBlockPoint());
    }
}
