package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.*;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class NameAttribute extends MetaAttribute<String> implements Attribute<String>, PlayerAwareAttribute, ContextPlaceholderAttribute {

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
    public void apply(ItemStack item, TextReplacement... replacements) {
        item.editMeta(meta ->
                meta.setDisplayNameComponent(I18n.global.getPlaceholderComponent(null, getValue(), replacements).bungee())
        );
    }

    @Override
    public void apply(ItemStack item, Player player, TextReplacement... replacements) {
        item.editMeta(meta ->
                meta.setDisplayNameComponent(I18n.global.getPlaceholderComponent(player.locale(), player, getValue(), replacements).bungee())
        );
    }

    @Override
    public void apply(ItemStack item, ComponentReplacement... replacements) {
        item.editMeta(meta ->
                meta.setDisplayNameComponent(I18n.global.getPlaceholderComponent(null, getValue()).replace(replacements).bungee())
        );
    }

    @Override
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
