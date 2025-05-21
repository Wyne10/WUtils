package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.ListConfigurable;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import me.wyne.wutils.config.configurables.item.PlayerAwareAttribute;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class LoreAttribute extends MetaAttribute<List<String>> implements PlayerAwareAttribute<List<String>> {

    public LoreAttribute(List<String> value) {
        super(ItemAttribute.LORE.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        meta.setLoreComponents(getValue().stream().map(s -> I18n.global.getPlaceholderComponent(null, s).bungee()).toList());
    }

    @Override
    public void apply(ItemStack item, Player player) {
        item.editMeta(meta ->
                meta.setLoreComponents(getValue().stream().map(s -> I18n.global.getPlaceholderComponent(player.locale(), player, s).bungee()).toList())
        );
    }

    public void apply(ItemMeta meta, TextReplacement... replacements) {
        meta.setLoreComponents(getValue().stream().map(s -> I18n.global.getPlaceholderComponent(null, s, replacements).bungee()).toList());
    }

    public void apply(ItemStack item, Player player, TextReplacement... replacements) {
        item.editMeta(meta ->
                meta.setLoreComponents(getValue().stream().map(s -> I18n.global.getPlaceholderComponent(player.locale(), player, s, replacements).bungee()).toList())
        );
    }

    public void apply(ItemMeta meta, ComponentReplacement... replacements) {
        meta.setLoreComponents(getValue().stream().map(s -> I18n.global.getPlaceholderComponent(null, s).replace(replacements).bungee()).toList());
    }

    public void apply(ItemStack item, Player player, ComponentReplacement... replacements) {
        item.editMeta(meta ->
                meta.setLoreComponents(getValue().stream().map(s -> I18n.global.getPlaceholderComponent(player.locale(), player, s).replace(replacements).bungee()).toList())
        );
    }

    @Override
    public String toString() {
        return new ListConfigurable<>(getValue()).toConfig(0, null);
    }

}
