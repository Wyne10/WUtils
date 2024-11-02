package me.wyne.wutils.config.configurables;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class GuiItemConfigurable extends ItemStackConfigurable {

    private Optional<String> print;
    private Optional<Sound> sound;

    public GuiItemConfigurable(ConfigurationSection itemSection) {
        super(itemSection);
    }

    public GuiItemConfigurable(String name, Material material, int slot, int model, Collection<String> lore, @Nullable String print, @Nullable Sound sound) {
        super(name, material, slot, model, lore);
        this.print = Optional.ofNullable(print);
        this.sound = Optional.ofNullable(sound);
    }

    @Override
    public String toConfig(ConfigEntry configEntry) {
        String itemStackConfig = super.toConfig(configEntry);
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.append(1, "print", print.orElse(null));
        configBuilder.append(1, "sound", sound.map(soundMapper -> soundMapper.name().asString()).orElse(null));
        return itemStackConfig + configBuilder.build();
    }

    @Override
    public void fromConfig(Object configObject) {
        super.fromConfig(configObject);
        ConfigurationSection config = (ConfigurationSection) configObject;
        print = Optional.ofNullable(config.getString("print"));
        sound = Optional.ofNullable(config.contains("sound") ? Sound.sound(Key.key(config.getString("sound")), Sound.Source.MASTER, 1f, 1f) : null);
    }

    public GuiItem buildGuiItem(TagResolver... tagResolvers)
    {
        return ItemBuilder.from(build(tagResolvers))
                .asGuiItem(event -> {
                    getPrint(null, tagResolvers).ifPresent(print -> event.getWhoClicked().sendMessage(print));
                    getSound().ifPresent(sound -> event.getWhoClicked().playSound(sound));
                });
    }

    public GuiItem buildGuiItem(GuiAction<InventoryClickEvent> action, TagResolver... tagResolvers)
    {
        return ItemBuilder.from(build(tagResolvers))
                .asGuiItem(event -> {
                    action.execute(event);
                    getPrint(null, tagResolvers).ifPresent(print -> event.getWhoClicked().sendMessage(print));
                    getSound().ifPresent(sound -> event.getWhoClicked().playSound(sound));
                });
    }

    public GuiItem buildGuiItem(@Nullable Player player, TagResolver... tagResolvers)
    {
        return ItemBuilder.from(build(player, tagResolvers))
                .asGuiItem(event -> {
                    getPrint(player, tagResolvers).ifPresent(print -> event.getWhoClicked().sendMessage(print));
                    getSound().ifPresent(sound -> event.getWhoClicked().playSound(sound));
                });
    }

    public GuiItem buildGuiItem(GuiAction<InventoryClickEvent> action, @Nullable Player player, TagResolver... tagResolvers)
    {
        return ItemBuilder.from(build(player, tagResolvers))
                .asGuiItem(event -> {
                    action.execute(event);
                    getPrint(player, tagResolvers).ifPresent(print -> event.getWhoClicked().sendMessage(print));
                    getSound().ifPresent(sound -> event.getWhoClicked().playSound(sound));
                });
    }

    public GuiItem buildLegacyGuiItem(TextReplacement... textReplacements)
    {
        return ItemBuilder.from(buildLegacy(textReplacements))
                .asGuiItem(event -> {
                    getLegacyPrint(null, textReplacements).ifPresent(print -> event.getWhoClicked().sendMessage(print));
                    getSound().ifPresent(sound -> event.getWhoClicked().playSound(sound));
                });
    }

    public GuiItem buildLegacyGuiItem(GuiAction<InventoryClickEvent> action, TextReplacement... textReplacements)
    {
        return ItemBuilder.from(buildLegacy(textReplacements))
                .asGuiItem(event -> {
                    action.execute(event);
                    getLegacyPrint(null, textReplacements).ifPresent(print -> event.getWhoClicked().sendMessage(print));
                    getSound().ifPresent(sound -> event.getWhoClicked().playSound(sound));
                });
    }

    public GuiItem buildLegacyGuiItem(@Nullable Player player, TextReplacement... textReplacements)
    {
        return ItemBuilder.from(buildLegacy(player, textReplacements))
                .asGuiItem(event -> {
                    getLegacyPrint(player, textReplacements).ifPresent(print -> event.getWhoClicked().sendMessage(print));
                    getSound().ifPresent(sound -> event.getWhoClicked().playSound(sound));
                });
    }

    public GuiItem buildLegacyGuiItem(GuiAction<InventoryClickEvent> action, @Nullable Player player, TextReplacement... textReplacements)
    {
        return ItemBuilder.from(buildLegacy(player, textReplacements))
                .asGuiItem(event -> {
                    action.execute(event);
                    getLegacyPrint(player, textReplacements).ifPresent(print -> event.getWhoClicked().sendMessage(print));
                    getSound().ifPresent(sound -> event.getWhoClicked().playSound(sound));
                });
    }

    public Optional<String> getPrint() {
        return print;
    }

    public Optional<Component> getLegacyPrint(@Nullable Player player, TextReplacement... textReplacements) {
        return print.map(printString -> I18n.global.getLegacyPlaceholderComponent(I18n.toLocale(player), player, printString, textReplacements));
    }

    public Optional<Component> getPrint(@Nullable Player player, TagResolver... tagResolvers) {
        return print.map(printString -> I18n.global.getPlaceholderComponent(I18n.toLocale(player), player, printString, tagResolvers));
    }

    public Optional<Sound> getSound() {
        return sound;
    }
}