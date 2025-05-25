package me.wyne.wutils.config.configurables.item;

import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ContextPlaceholderAttribute {
    void apply(ItemStack item, TextReplacement... replacements);

    void apply(ItemStack item, Player player, TextReplacement... replacements);

    void apply(ItemStack item, ComponentReplacement... replacements);

    void apply(ItemStack item, Player player, ComponentReplacement... replacements);
}
