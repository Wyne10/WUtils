package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import me.wyne.wutils.config.configurables.item.PlayerAwareAttribute;
import me.wyne.wutils.i18n.I18n;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class NameAttribute extends MetaAttribute<String> implements PlayerAwareAttribute<String> {

    public NameAttribute(String value) {
        super(ItemAttribute.NAME.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        meta.setDisplayNameComponent(I18n.global.getPlaceholderComponent(null, getValue()).bungee());
    }

    @Override
    public void apply(ItemStack item, Player player) {
        item.editMeta(meta ->
                meta.setDisplayNameComponent(I18n.global.getPlaceholderComponent(player.locale(), player, getValue()).bungee())
        );
    }

    @Override
    public String toString() {
        return getValue();
    }

}
