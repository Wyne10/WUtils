package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullPlayerAttribute extends AttributeBase<Boolean> implements ConfigurableAttribute<Boolean>, PlayerAwareAttribute {

    public SkullPlayerAttribute(String key, Boolean value) {
        super(key, value);
    }

    public SkullPlayerAttribute(Boolean value) {
        super(ItemAttribute.SKULL_PLAYER.getKey(), value);
    }

    @Override
    public void apply(ItemStack item, Player player) {
        if (!getValue()) return;
        if (!(item.getItemMeta() instanceof SkullMeta)) return;
        item.editMeta(meta -> ((SkullMeta)meta).setOwningPlayer(player));
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public ConfigurableAttribute<?> create(String key, ConfigurationSection config) {
            return new SkullPlayerAttribute(config.getBoolean(key, false));
        }
    }

}
