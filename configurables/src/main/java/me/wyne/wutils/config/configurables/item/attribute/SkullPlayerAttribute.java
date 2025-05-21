package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import me.wyne.wutils.config.configurables.item.PlayerAwareAttribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullPlayerAttribute extends MetaAttribute<Void> implements PlayerAwareAttribute<Void> {

    public SkullPlayerAttribute() {
        super(ItemAttribute.SKULL_PLAYER.getKey(), null);
    }

    @Override
    public void apply(ItemMeta meta) {}

    @Override
    public void apply(ItemStack item, Player player) {
        if (!(item.getItemMeta() instanceof SkullMeta)) return;
        item.editMeta(meta -> ((SkullMeta)meta).setOwningPlayer(player));
    }

    @Override
    public String toString() {
        return "";
    }

}
