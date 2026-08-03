package me.wyne.wutils.structure.scheme;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class ClipboardScanCache {

    private final Map<Clipboard, Map<Object, List<BlockVector3>>> cache =
            Collections.synchronizedMap(new WeakHashMap<>());

    public @NotNull List<BlockVector3> find(@NotNull Clipboard clipboard, @NotNull BlockType @NotNull ... types) {
        Set<BlockType> key = Set.copyOf(Arrays.asList(types));
        return resolve(clipboard, key, scan -> List.copyOf(scan.find(types)));
    }

    public @NotNull List<BlockVector3> find(@NotNull Clipboard clipboard, @NotNull String key,
                                            @NotNull Function<ClipboardScan, List<BlockVector3>> scanner) {
        return resolve(clipboard, key, scan -> List.copyOf(scanner.apply(scan)));
    }

    private @NotNull List<BlockVector3> resolve(@NotNull Clipboard clipboard, @NotNull Object key,
                                                @NotNull Function<ClipboardScan, List<BlockVector3>> scanner) {
        Map<Object, List<BlockVector3>> perClipboard = cache.computeIfAbsent(clipboard, c -> new ConcurrentHashMap<>());
        return perClipboard.computeIfAbsent(key, k -> scanner.apply(new ClipboardScan(clipboard)));
    }

    public void invalidate(@NotNull Clipboard clipboard) {
        cache.remove(clipboard);
    }

    public void clear() {
        cache.clear();
    }
}
