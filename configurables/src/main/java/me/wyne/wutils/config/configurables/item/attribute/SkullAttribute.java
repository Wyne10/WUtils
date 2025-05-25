package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.AttributeBase;
import me.wyne.wutils.config.configurables.item.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullAttribute extends AttributeBase<OfflinePlayer> implements MetaAttribute, ConfigurableAttribute<OfflinePlayer> {

    public SkullAttribute(String key, OfflinePlayer value) {
        super(key, value);
    }

    public SkullAttribute(OfflinePlayer value) {
        super(ItemAttribute.SKULL.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof SkullMeta)) return;
        ((SkullMeta)meta).setOwningPlayer(getValue());
    }

}
