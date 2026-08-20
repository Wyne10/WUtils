package me.wyne.wutils.common.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;

/** {@link TileStateLoader} for {@link CreatureSpawner}s, persisting the spawner's mob type as the item's display name. */
public class SpawnerLoader implements TileStateLoader {

    /** No-op (returns {@code item} unchanged) if {@code blockState} is not a {@link CreatureSpawner}. */
    @Override
    public @NotNull ItemStack save(@NotNull ItemStack item, @NotNull BlockState blockState) {
        if (!(blockState instanceof CreatureSpawner spawner)) return item;
        item.editMeta(meta -> {
            meta.displayName(Component.empty().decoration(TextDecoration.ITALIC, false).append(Component.translatable(org.bukkit.Bukkit.getUnsafe().getTranslationKey(spawner.getSpawnedType()))));
        });
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
