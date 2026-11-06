package me.wyne.wutils.common.item;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;

/**
 * {@link TileStateLoader} for {@link CreatureSpawner}s, persisting the spawner's mob type as the item's display name.
 *
 * <p>The name is set as a BungeeCord component rather than through Paper's Adventure API, so it
 * still works in plugins that shade and relocate {@code net.kyori}.</p>
 */
public class SpawnerLoader implements TileStateLoader {

    /** No-op (returns {@code item} unchanged) if {@code blockState} is not a {@link CreatureSpawner}. */
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull ItemStack save(@NotNull ItemStack item, @NotNull BlockState blockState) {
        if (!(blockState instanceof CreatureSpawner spawner)) return item;
        var name = new TranslatableComponent(org.bukkit.Bukkit.getUnsafe().getTranslationKey(spawner.getSpawnedType()));
        name.setItalic(false);
        item.editMeta(meta -> meta.setDisplayNameComponent(new BaseComponent[]{name}));
        return item;
    }

    /**
     * No-op (returns {@code blockState} unchanged) if {@code blockState} is not a
     * {@link CreatureSpawner}, {@code item} has no block-state meta, or its saved state is not a
     * {@link CreatureSpawner}.
     */
    @Override
    public @NotNull BlockState load(@NotNull ItemStack item, @NotNull BlockState blockState) {
        if (!(blockState instanceof CreatureSpawner spawner)) return blockState;
        var meta = (BlockStateMeta) item.getItemMeta();
        if (!(meta.getBlockState() instanceof CreatureSpawner data)) return blockState;
        spawner.setDelay(data.getDelay());
        spawner.setMinSpawnDelay(data.getMinSpawnDelay());
        spawner.setMaxSpawnDelay(data.getMaxSpawnDelay());
        spawner.setMaxNearbyEntities(data.getMaxNearbyEntities());
        spawner.setSpawnCount(data.getSpawnCount());
        spawner.setRequiredPlayerRange(data.getRequiredPlayerRange());
        spawner.setSpawnedType(data.getSpawnedType());
        spawner.setSpawnRange(data.getSpawnRange());
        spawner.update();
        return spawner;
    }
}
