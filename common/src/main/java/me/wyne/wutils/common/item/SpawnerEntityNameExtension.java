package me.wyne.wutils.common.item;

import net.kyori.adventure.text.Component;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.inventory.ItemStack;

public class SpawnerEntityNameExtension implements BlockStateMetaExtension {
    @Override
    public ItemStack extend(ItemStack item, BlockState blockState) {
        if (!(blockState instanceof CreatureSpawner spawner)) return item;
        item.editMeta(meta -> {
            meta.displayName(Component.translatable(org.bukkit.Bukkit.getUnsafe().getTranslationKey(spawner.getSpawnedType())));
        });
        return item;
    }
}
