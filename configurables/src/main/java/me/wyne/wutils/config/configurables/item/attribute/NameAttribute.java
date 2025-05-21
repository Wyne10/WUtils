package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import me.wyne.wutils.config.configurables.item.PlayerAwareAttribute;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
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

    public void apply(ItemMeta meta, TextReplacement... replacements) {
        meta.setDisplayNameComponent(I18n.global.getPlaceholderComponent(null, getValue(), replacements).bungee());
    }

    public void apply(ItemStack item, Player player, TextReplacement... replacements) {
        item.editMeta(meta ->
                meta.setDisplayNameComponent(I18n.global.getPlaceholderComponent(player.locale(), player, getValue(), replacements).bungee())
        );
    }

    public void apply(ItemMeta meta, ComponentReplacement... replacements) {
        meta.setDisplayNameComponent(I18n.global.getPlaceholderComponent(null, getValue()).replace(replacements).bungee());
    }

    public void apply(ItemStack item, Player player, ComponentReplacement... replacements) {
        item.editMeta(meta ->
                meta.setDisplayNameComponent(I18n.global.getPlaceholderComponent(player.locale(), player, getValue()).replace(replacements).bungee())
        );
    }

    @Override
    public String toString() {
        return getValue();
    }

}
