package me.wyne.wutils.config.configurables;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.component.LocalizedComponent;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class GuiItemConfigurable extends ItemStackConfigurable {

    private String print;
    private final SoundConfigurable sound;

    public GuiItemConfigurable(Object configObject) {
        this.sound = new SoundConfigurable();
        fromConfig(configObject);
    }

    public GuiItemConfigurable(String name, Material material, int slot, int model, Collection<String> lore, Collection<ItemFlag> flags, Map<Enchantment, Integer> enchantments, @Nullable String print, @Nullable Sound sound) {
        super(name, material, slot, model, lore, flags, enchantments);
        this.print = print;
        this.sound = new SoundConfigurable(sound);
    }

    public GuiItemConfigurable(String name, Material material, Collection<String> lore, Collection<ItemFlag> flags, Map<Enchantment, Integer> enchantments, @Nullable String print, @Nullable Sound sound) {
        super(name, material, lore, flags, enchantments);
        this.print = print;
        this.sound = new SoundConfigurable(sound);
    }

    public GuiItemConfigurable(String name, Material material, int slot, Collection<String> lore, Collection<ItemFlag> flags, Map<Enchantment, Integer> enchantments, @Nullable String print, @Nullable Sound sound) {
        super(name, material, slot, lore, flags, enchantments);
        this.print = print;
        this.sound = new SoundConfigurable(sound);
    }

    public GuiItemConfigurable(String name, Material material, Collection<String> lore, @Nullable String print, @Nullable Sound sound) {
        super(name, material, lore);
        this.print = print;
        this.sound = new SoundConfigurable(sound);
    }

    public GuiItemConfigurable(String name, Material material, @Nullable String print, @Nullable Sound sound) {
        super(name, material);
        this.print = print;
        this.sound = new SoundConfigurable(sound);
    }

    public GuiItemConfigurable(String name, Material material, int slot, Collection<String> lore, @Nullable String print, @Nullable Sound sound) {
        super(name, material, slot, lore);
        this.print = print;
        this.sound = new SoundConfigurable(sound);
    }

    public GuiItemConfigurable(String name, Material material, int slot, int model, Collection<String> lore, Collection<ItemFlag> flags, Map<Enchantment, Integer> enchantments) {
        super(name, material, slot, model, lore, flags, enchantments);
        this.sound = new SoundConfigurable();
    }

    public GuiItemConfigurable(String name, Material material, Collection<String> lore, Collection<ItemFlag> flags, Map<Enchantment, Integer> enchantments) {
        super(name, material, lore, flags, enchantments);
        this.sound = new SoundConfigurable();
    }

    public GuiItemConfigurable(String name, Material material, int slot, Collection<String> lore, Collection<ItemFlag> flags, Map<Enchantment, Integer> enchantments) {
        super(name, material, slot, lore, flags, enchantments);
        this.sound = new SoundConfigurable();
    }

    public GuiItemConfigurable(String name, Material material, Collection<String> lore) {
        super(name, material, lore);
        this.sound = new SoundConfigurable();
    }

    public GuiItemConfigurable(String name, Material material) {
        super(name, material);
        this.sound = new SoundConfigurable();
    }

    public GuiItemConfigurable(String name, Material material, int slot, Collection<String> lore) {
        super(name, material, slot, lore);
        this.sound = new SoundConfigurable();
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        String itemStackConfig = super.toConfig(depth, configEntry);
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.append(depth, "print", print);
        configBuilder.appendComposite(depth, "sound", sound, configEntry);
        return itemStackConfig + configBuilder.build();
    }

    @Override
    public void fromConfig(Object configObject) {
        super.fromConfig(configObject);
        ConfigurationSection config = (ConfigurationSection) configObject;
        print = config.getString("print");
        sound.fromConfig(config.getConfigurationSection("sound"));
    }

    public GuiItem buildGuiItem(TextReplacement... textReplacements)
    {
        return ItemBuilder.from(build(textReplacements))
                .asGuiItem(event -> {
                    getPrint(null, textReplacements).ifPresent(print -> print.sendMessage(event.getWhoClicked()));
                    getSound().ifPresent(sound -> event.getWhoClicked().playSound(sound));
                });
    }

    public GuiItem buildGuiItem(GuiAction<InventoryClickEvent> action, TextReplacement... textReplacements)
    {
        return ItemBuilder.from(build(textReplacements))
                .asGuiItem(event -> {
                    action.execute(event);
                    getPrint(null, textReplacements).ifPresent(print -> print.sendMessage(event.getWhoClicked()));
                    getSound().ifPresent(sound -> event.getWhoClicked().playSound(sound));
                });
    }

    public GuiItem buildGuiItem(@Nullable Player player, TextReplacement... textReplacements)
    {
        return ItemBuilder.from(build(player, textReplacements))
                .asGuiItem(event -> {
                    getPrint(player, textReplacements).ifPresent(print -> print.sendMessage(event.getWhoClicked()));
                    getSound().ifPresent(sound -> event.getWhoClicked().playSound(sound));
                });
    }

    public GuiItem buildGuiItem(GuiAction<InventoryClickEvent> action, @Nullable Player player, TextReplacement... textReplacements)
    {
        return ItemBuilder.from(build(player, textReplacements))
                .asGuiItem(event -> {
                    action.execute(event);
                    getPrint(player, textReplacements).ifPresent(print -> print.sendMessage(event.getWhoClicked()));
                    getSound().ifPresent(sound -> event.getWhoClicked().playSound(sound));
                });
    }

    public Optional<String> getPrint() {
        return Optional.ofNullable(print);
    }

    public Optional<LocalizedComponent> getPrint(@Nullable Player player, TextReplacement... textReplacements) {
        return getPrint().map(printString -> I18n.global.getPlaceholderComponent(I18n.toLocale(player), player, printString, textReplacements));
    }

    public Optional<Sound> getSound() {
        return Optional.ofNullable(sound.getSound());
    }

}