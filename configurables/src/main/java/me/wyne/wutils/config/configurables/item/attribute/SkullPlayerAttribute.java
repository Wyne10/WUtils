package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Sets a {@link SkullMeta} owner to the context player, so the head shows whoever the item is
 * built for rather than a fixed name. No-ops when {@code false} or on any meta that is not a
 * {@link SkullMeta}. A {@code null} context player clears the skull owner.
 */
public class SkullPlayerAttribute extends ConfigurableAttribute<Boolean> implements ContextMetaAttribute {

    public SkullPlayerAttribute(@NotNull String key, @NotNull Boolean value) {
        super(key, value);
    }

    public SkullPlayerAttribute(@NotNull Boolean value) {
        super(ItemAttribute.SKULL_PLAYER.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemMeta meta, @NotNull ItemAttributeContext context) {
        if (!getValue()) return;
        if (!(meta instanceof SkullMeta)) return;
        ((SkullMeta)meta).setOwningPlayer(context.getPlayer());
    }

    public static final class Factory implements AttributeFactory<SkullPlayerAttribute> {
        @Override
        public @NotNull SkullPlayerAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new SkullPlayerAttribute(key, config.getBoolean(key, false));
        }
    }

}
