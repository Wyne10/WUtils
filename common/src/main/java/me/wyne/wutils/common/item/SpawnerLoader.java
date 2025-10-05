package me.wyne.wutils.common.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class SpawnerLoader implements TileStateLoader {
    @Override
    public ItemStack save(ItemStack item, BlockState blockState) {
        if (!(blockState instanceof CreatureSpawner spawner)) return item;
        item.editMeta(meta -> {
            meta.displayName(Component.empty().decoration(TextDecoration.ITALIC, false).append(Component.translatable(org.bukkit.Bukkit.getUnsafe().getTranslationKey(spawner.getSpawnedType()))));
        });
        return item;

    }

    @Override
    public BlockState load(ItemStack item, BlockState blockState) {
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
