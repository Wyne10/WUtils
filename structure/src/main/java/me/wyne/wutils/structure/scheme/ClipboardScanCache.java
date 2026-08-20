package me.wyne.wutils.structure.scheme;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.mask.Mask;
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

/**
 * Memoizes {@link ClipboardScan} results per {@link Clipboard}, so repeated lookups against the same
 * clipboard (e.g. across many placements of the same structure) do not re-scan it.
 *
 * <p>Cached per clipboard in a {@link WeakHashMap} keyed by identity, so entries are collected once
 * a clipboard is no longer referenced elsewhere. Within one clipboard's entry, results are further
 * keyed by an {@link Object} — a {@code Set<BlockType>} for {@link #find(Clipboard, BlockType...)},
 * or a caller-chosen {@link String} for the two {@code find} overloads that take one. All three
 * {@code find} overloads share this one key namespace per clipboard, so a caller-chosen
 * {@code String} key must be unique for what it scans, or it will collide with (and return stale
 * results for) a different scan reusing the same key. Returned lists are immutable copies, safe to
 * hand out without defensive copying by the caller.</p>
 */
public final class ClipboardScanCache {

    private final Map<Clipboard, Map<Object, List<BlockVector3>>> cache =
            Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * Returns clipboard-local positions matching any of {@code types}, keyed on the exact type set
     * so different combinations of types are cached independently.
     */
    public @NotNull List<BlockVector3> find(@NotNull Clipboard clipboard, @NotNull BlockType @NotNull ... types) {
        Set<BlockType> key = Set.copyOf(Arrays.asList(types));
        return resolve(clipboard, key, scan -> List.copyOf(scan.find(types)));
    }

    public @NotNull List<BlockVector3> find(@NotNull Clipboard clipboard, @NotNull String key, @NotNull Mask mask) {
        return resolve(clipboard, key, scan -> List.copyOf(scan.find(mask)));
    }

    /**
     * Resolves via an arbitrary {@code scanner} rather than a built-in {@link Mask} lookup, still
     * cached under {@code key}.
     */
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
