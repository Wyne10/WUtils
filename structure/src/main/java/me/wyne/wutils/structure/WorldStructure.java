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

    public void spawn() {
        snapshot = getRegionSnapshot();
        pasteStructure();
        setProtectedRegion();
    }

    public @NotNull WorldStructureMemento capture() {
        Preconditions.checkNotNull(snapshot, "Structure " + uniqueKey + " cannot be captured before it is spawned");
        return new WorldStructureMemento(uniqueKey, location, region, clipboardRegion, transform, clipboard, snapshot);
    }

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

    public @NotNull BlockVector3 toWorld(@NotNull BlockVector3 clipboardPos) {
        return ClipboardScan.toWorld(clipboardPos, clipboard.getOrigin(), location.toVector().toBlockPoint(), transform);
    }

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
        snapshotModifiers.forEach(snapshotModifier -> snapshotModifier.apply(forwardExtentCopy, clipboardRegion.getWorld()));
        try {
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {
            throw new RuntimeException("Structure " + uniqueKey + " snapshot exception", e);
        }
        return snapshot;
    }

    private void pasteStructure() {
        Preconditions.checkNotNull(clipboardRegion.getWorld(), "Clipboard region world was null during " + uniqueKey + " paste");
        try (var editSession = WorldEdit.getInstance().newEditSession(clipboardRegion.getWorld())) {
            var clipboardHolder = new ClipboardHolder(clipboard);
            clipboardHolder.setTransform(transform);
            var pasteBuilder = clipboardHolder
                    .createPaste(editSession)
                    .to(location.toVector().toBlockPoint());
            pasteModifiers.forEach(pasteModifier -> pasteModifier.apply(pasteBuilder, clipboardRegion.getWorld()));
            Operations.complete(pasteBuilder.build());
            editSession.flushSession();
            editSessionModifiers.forEach(editSessionModifier -> {
                editSessionModifier.apply(editSession, clipboardRegion);
                editSession.flushSession();
            });
        } catch (WorldEditException e) {
            throw new RuntimeException("Structure " + uniqueKey + " paste exception", e);
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
