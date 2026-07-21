package me.wyne.wutils.structure;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.structure.modifier.EditSessionModifier;
import me.wyne.wutils.structure.modifier.PasteModifier;
import me.wyne.wutils.structure.modifier.SnapshotModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class WorldStructure implements AutoCloseable {

    private final String uniqueKey;
    private final Clipboard clipboard;
    private final Location location;
    private final ProtectedCuboidRegion region;
    private final Region clipboardRegion;
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
        this.snapshotModifiers = modifierContainer.getSet(SnapshotModifier.class);
        this.pasteModifiers = modifierContainer.getSet(PasteModifier.class);
        this.editSessionModifiers = modifierContainer.getSet(EditSessionModifier.class);
    }

    public void spawn() {
        snapshot = getRegionSnapshot();
        pasteStructure();
        setProtectedRegion();
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public Clipboard getClipboard() {
        return clipboard;
    }

    public Location getLocation() {
        return location;
    }

    public ProtectedCuboidRegion getRegion() {
        return region;
    }

    public Region getClipboardRegion() {
        return clipboardRegion;
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
            var pasteBuilder = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(location.toVector().toBlockPoint());
            pasteModifiers.forEach(pasteModifier -> pasteModifier.apply(pasteBuilder, clipboardRegion.getWorld()));
            Operations.complete(pasteBuilder.build());
            editSessionModifiers.forEach(editSessionModifier -> editSessionModifier.apply(editSession, clipboardRegion));
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
