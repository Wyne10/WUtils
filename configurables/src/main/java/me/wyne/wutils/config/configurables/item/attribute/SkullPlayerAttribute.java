package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullPlayerAttribute extends ConfigurableAttribute<Boolean> implements ContextMetaAttribute {

    public SkullPlayerAttribute(String key, Boolean value) {
        super(key, value);
    }

    public SkullPlayerAttribute(Boolean value) {
        super(ItemAttribute.SKULL_PLAYER.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta, ItemAttributeContext context) {
        if (!getValue()) return;
        if (!(meta instanceof SkullMeta)) return;
        ((SkullMeta)meta).setOwningPlayer(context.getPlayer());
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public SkullPlayerAttribute create(String key, ConfigurationSection config) {
            return new SkullPlayerAttribute(key, config.getBoolean(key, false));
        }
    }

}
