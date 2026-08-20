package me.wyne.wutils.structure;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.common.plugin.PluginUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.structure.persistence.WorldStructureMemento;
import me.wyne.wutils.structure.scheme.ClipboardScan;
import me.wyne.wutils.structure.modifier.EditSessionModifier;
import me.wyne.wutils.structure.modifier.PasteModifier;
import me.wyne.wutils.structure.modifier.SnapshotModifier;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * A structure placed in the world: the {@link Structure} recipe's output after a valid location and
 * region have been resolved by {@link Structure#create}.
 *
 * <p>Requires WorldEdit and WorldGuard on the runtime classpath (both {@code compileOnlyApi} — the
 * consumer must supply them).</p>
 *
 * <p>Lifecycle: {@link #spawn()} snapshots the target region, pastes the clipboard, and registers a
 * WorldGuard region; if any of that throws, the snapshot is rolled back before the exception
 * propagates, leaving no partial paste behind. {@link #close()} unregisters the WorldGuard region and
 * restores the snapshot, undoing the spawn — implementing {@link AutoCloseable} so a structure can be
 * placed and torn down with try-with-resources. Individual {@code SnapshotModifier}, {@code
 * PasteModifier}, and {@code EditSessionModifier} instances that throw during {@link #spawn()} are
 * logged and skipped rather than propagated, so one misbehaving modifier does not abort the whole
 * spawn.</p>
 */
public class WorldStructure implements AutoCloseable {

    private final String uniqueKey;
    private final Clipboard clipboard;
    private final Location location;
    private final ProtectedCuboidRegion region;
    private final Region clipboardRegion;
    private final Transform transform;
    private final Set<SnapshotModifier> snapshotModifiers;
    private final Set<PasteModifier> pasteModifiers;
    private final Set<EditSessionModifier> editSessionModifiers;

    private Clipboard snapshot;

    public WorldStructure(@NotNull IntermediateStructure intermediateStructure, @NotNull ProtectedCuboidRegion region, @NotNull AttributeContainer modifierContainer) {
        this.uniqueKey = intermediateStructure.uniqueKey();
        this.clipboard = intermediateStructure.clipboard();
        this.location = BukkitAdapter.adapt(intermediateStructure.location());
        this.region = region;
        this.clipboardRegion = intermediateStructure.clipboardRegion();
        this.transform = intermediateStructure.transform();
        this.snapshotModifiers = modifierContainer.getSet(SnapshotModifier.class);
        this.pasteModifiers = modifierContainer.getSet(PasteModifier.class);
        this.editSessionModifiers = modifierContainer.getSet(EditSessionModifier.class);
    }

    private WorldStructure(@NotNull WorldStructureMemento memento) {
        this.uniqueKey = memento.uniqueKey();
        this.clipboard = memento.clipboard();
        this.location = memento.location();
        this.region = memento.region();
        this.clipboardRegion = memento.clipboardRegion();
        this.transform = memento.transform();
        this.snapshotModifiers = Set.of();
        this.pasteModifiers = Set.of();
        this.editSessionModifiers = Set.of();
        this.snapshot = memento.snapshot();
    }

    /**
     * Snapshots the target region, pastes the clipboard into the world, and registers the WorldGuard
     * protected region. If pasting or registration throws, the snapshot is restored before the
     * exception is rethrown, so a failed spawn leaves the world unchanged.
     */
    public void spawn() {
        snapshot = getRegionSnapshot();
        try {
            pasteStructure();
            setProtectedRegion();
        } catch (RuntimeException e) {
            restoreRegionSnapshot();
            throw e;
        }
    }

    /**
     * Captures this structure's current state, including the pre-spawn snapshot, as a
     * {@link WorldStructureMemento} that can be persisted and later handed to {@link #restore}.
     *
     * @throws NullPointerException if this structure has not been {@link #spawn() spawned} yet
     */
    public @NotNull WorldStructureMemento capture() {
        Preconditions.checkNotNull(snapshot, "Structure " + uniqueKey + " cannot be captured before it is spawned");
        return new WorldStructureMemento(uniqueKey, location, region, clipboardRegion, transform, clipboard, snapshot);
    }

    /**
     * Rebuilds a {@link WorldStructure} from a previously {@link #capture() captured} memento,
     * without re-running {@link Structure#create}. The result already carries its restore-snapshot
     * but has no modifiers attached, since modifiers are not part of the persisted state.
     */
    public static @NotNull WorldStructure restore(@NotNull WorldStructureMemento memento) {
        return new WorldStructure(memento);
    }

    public @NotNull String getUniqueKey() {
        return uniqueKey;
    }

    public @NotNull Clipboard getClipboard() {
        return clipboard;
    }

    public @NotNull Location getLocation() {
        return location;
    }

    public @NotNull ProtectedCuboidRegion getRegion() {
        return region;
    }

    public @NotNull Region getClipboardRegion() {
        return clipboardRegion;
    }

    public @NotNull Transform getTransform() {
        return transform;
    }

    /**
     * Maps a position in clipboard-local coordinates to its world position after this structure's
     * transform and placement, via {@link ClipboardScan#toWorld}.
     */
    public @NotNull BlockVector3 toWorld(@NotNull BlockVector3 clipboardPos) {
        return ClipboardScan.toWorld(clipboardPos, clipboard.getOrigin(), location.toVector().toBlockPoint(), transform);
    }

    /**
     * Teleports every non-spectator player standing inside this structure's region up to the
     * highest solid block at their column, so they are not left inside newly pasted terrain.
     * Players with no solid block in the column are left where they are.
     */
    public void liftPlayersToSurface() {
        Preconditions.checkNotNull(clipboardRegion.getWorld(), "Clipboard region world was null during " + uniqueKey + " player lift");
        World world = BukkitAdapter.adapt(clipboardRegion.getWorld());
        for (Player player : world.getPlayers()) {
            if (player.getGameMode() == GameMode.SPECTATOR) continue;
            var loc = player.getLocation();
            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();
            if (!region.contains(x, y, z))
                continue;
            int surfaceY = highestSolidY(world, x, z);
            if (surfaceY < world.getMinHeight())
                continue;
            var destination = new org.bukkit.Location(world, x + 0.5, surfaceY + 1, z + 0.5, loc.getYaw(), loc.getPitch());
            player.teleport(destination);
        }
    }

    private static int highestSolidY(@NotNull World world, int x, int z) {
        for (int y = world.getMaxHeight() - 1; y >= world.getMinHeight(); y--) {
            if (world.getBlockAt(x, y, z).getType().isSolid())
                return y;
        }
        return world.getMinHeight() - 1;
    }

    private Clipboard getRegionSnapshot() {
        Preconditions.checkNotNull(clipboardRegion.getWorld(), "Clipboard region world was null during " + uniqueKey + " snapshot");
        var region = new CuboidRegion(clipboardRegion.getWorld(), this.region.getMinimumPoint(), this.region.getMaximumPoint());
        var snapshot = new BlockArrayClipboard(region);
        var forwardExtentCopy = new ForwardExtentCopy(clipboardRegion.getWorld(), region, snapshot, region.getMinimumPoint());
        snapshotModifiers.forEach(snapshotModifier -> {
            try {
                snapshotModifier.apply(forwardExtentCopy, clipboardRegion.getWorld());
            } catch (RuntimeException e) {
                PluginUtils.getLogger().error("Structure {} snapshot modifier {} threw an exception, skipping", uniqueKey, snapshotModifier.getClass().getSimpleName(), e);
            }
        });
        try {
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {
            throw new RuntimeException("Structure " + uniqueKey + " snapshot exception", e);
        }
        return snapshot;
    }

    private void pasteStructure() {
        var world = clipboardRegion.getWorld();
        Preconditions.checkNotNull(world, "Clipboard region world was null during " + uniqueKey + " paste");
        try (var editSession = WorldEdit.getInstance().newEditSession(world)) {
            var clipboardHolder = new ClipboardHolder(clipboard);
            clipboardHolder.setTransform(transform);
            var pasteBuilder = clipboardHolder
                    .createPaste(editSession)
                    .to(location.toVector().toBlockPoint());
            pasteModifiers.forEach(pasteModifier -> {
                try {
                    pasteModifier.apply(pasteBuilder, clipboardRegion.getWorld());
                } catch (RuntimeException e) {
                    PluginUtils.getLogger().error("Structure {} paste modifier {} threw an exception, skipping", uniqueKey, pasteModifier.getClass().getSimpleName(), e);
                }
            });
            Operations.complete(pasteBuilder.build());
        } catch (WorldEditException e) {
            throw new RuntimeException("Structure " + uniqueKey + " paste exception", e);
        }
        for (EditSessionModifier modifier : editSessionModifiers) {
            try (var editSession = WorldEdit.getInstance().newEditSession(clipboardRegion.getWorld())) {
                modifier.apply(editSession, clipboardRegion);
            } catch (RuntimeException e) {
                PluginUtils.getLogger().error("Structure {} edit session modifier {} threw an exception, skipping", uniqueKey, modifier.getClass().getSimpleName(), e);
            }
        }
    }

    private void setProtectedRegion() {
        Preconditions.checkNotNull(clipboardRegion.getWorld(), "Clipboard region world was null during " + uniqueKey + " protected region set");
        var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        var regions = container.get(clipboardRegion.getWorld());
        Preconditions.checkNotNull(regions, "Regions manager for " + uniqueKey + " was null");
        regions.addRegion(region);
    }

    private void removeProtectedRegion() {
        Preconditions.checkNotNull(clipboardRegion.getWorld(), "Clipboard region world was null during " + uniqueKey + " protected region set");
        var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        var regions = container.get(clipboardRegion.getWorld());
        Preconditions.checkNotNull(regions, "Regions manager for " + uniqueKey + " was null");
        regions.removeRegion(region.getId());
    }

    private void restoreRegionSnapshot() {
        Preconditions.checkNotNull(clipboardRegion.getWorld(), "Clipboard region world was null during " + uniqueKey + " snapshot restore");
        try (var editSession = WorldEdit.getInstance().newEditSession(clipboardRegion.getWorld())) {
            var pasteBuilder = new ClipboardHolder(snapshot)
                    .createPaste(editSession)
                    .ignoreAirBlocks(false)
                    .copyBiomes(snapshot.hasBiomes())
                    .copyEntities(true)
                    .to(snapshot.getOrigin());
            Operations.complete(pasteBuilder.build());
        } catch (WorldEditException e) {
            throw new RuntimeException("Structure " + uniqueKey + " snapshot restore exception", e);
        }
    }

    @Override
    public void close() {
        removeProtectedRegion();
        restoreRegionSnapshot();
    }

}
