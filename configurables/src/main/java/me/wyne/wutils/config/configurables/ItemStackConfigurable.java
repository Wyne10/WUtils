package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.ConfigUtils;
import me.wyne.wutils.common.MapUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.component.LocalizedComponent;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemStackConfigurable implements CompositeConfigurable {

    private String name;
    private Material material;
    private int slot;
    private int model;
    private final Collection<String> lore = new ArrayList<>();
    private final Collection<ItemFlag> flags = new ArrayList<>();
    private final GenericMapConfigurable<Enchantment, Integer> enchantments = new GenericMapConfigurable<>(
            entry -> MapUtils.entry(entry.getKey().getKey().toString(), entry.getValue().toString()),
            entry -> MapUtils.entry(Enchantment.getByKey(NamespacedKey.fromString(entry.getKey())), (int) entry.getValue())
    );

    ItemStackConfigurable() {}

    public ItemStackConfigurable(ConfigurationSection section) {
        fromConfig(section);
    }

    public ItemStackConfigurable(String name, Material material, int slot, int model, Collection<String> lore, Collection<ItemFlag> flags, Map<Enchantment, Integer> enchantments) {
        this.name = name;
        this.material = material;
        this.slot = slot;
        this.model = model;
        this.lore.addAll(lore);
        this.flags.addAll(flags);
        this.enchantments.putAll(enchantments);
    }

    public ItemStackConfigurable(String name, Material material, Collection<String> lore, Collection<ItemFlag> flags, Map<Enchantment, Integer> enchantments) {
        this(name, material, -1, -1, lore, flags, enchantments);
    }

    public ItemStackConfigurable(String name, Material material, int slot, Collection<String> lore, Collection<ItemFlag> flags, Map<Enchantment, Integer> enchantments) {
        this(name, material, slot, -1, lore, flags, enchantments);
    }

    public ItemStackConfigurable(String name, Material material, Collection<String> lore) {
        this(name, material, -1, -1, lore, Collections.emptyList(), Collections.emptyMap());
    }

    public ItemStackConfigurable(String name, Material material) {
        this(name, material, -1, -1, Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
    }

    public ItemStackConfigurable(String name, Material material, int slot, Collection<String> lore) {
        this(name, material, slot, -1, lore, Collections.emptyList(), Collections.emptyMap());
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.appendIfNotEqual(depth, "name", name, "");
        configBuilder.append(depth, "material", material);
        configBuilder.append(depth, "slot", slot != -1 ? slot : null);
        configBuilder.append(depth, "model", model != -1 ? model : null);
        configBuilder.appendCollection(depth, "lore", lore);
        configBuilder.appendCollection(depth, "flags", flags);
        configBuilder.appendComposite(depth, "enchantments", enchantments, configEntry);
        return configBuilder.build();
    }

    @Override
    public void fromConfig(Object configObject) {
        ConfigurationSection config = (ConfigurationSection) configObject;
        name = config.getString("name", "");
        material = Material.matchMaterial(config.getString("material", "stone"));
        slot = config.getInt("slot", -1);
        model = config.getInt("model", -1);
        lore.clear();
        flags.clear();
        lore.addAll(ConfigUtils.getStringList(config, "lore"));
        flags.addAll(ConfigUtils.getStringList(config, "flags").stream().map(ItemFlag::valueOf).toList());
        enchantments.fromConfig(config.getConfigurationSection("enchantments"));
    }

    public ItemStack build(TextReplacement... textReplacements) {
        return build(null, textReplacements);
    }

    public ItemStack build(@Nullable Player player, TextReplacement... textReplacements) {
        ItemStack itemStack = new ItemStack(getMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayNameComponent(getName(player, textReplacements).bungee());
        itemMeta.setLoreComponents(getLore(player, textReplacements).stream().map(LocalizedComponent::bungee).toList());
        if (model != -1)
            itemMeta.setCustomModelData(model);
        itemMeta.addItemFlags(flags.toArray(ItemFlag[]::new));
        enchantments.getMap().forEach((enchantment, integer) -> itemMeta.addEnchant(enchantment, integer, true));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack buildComponent(ComponentReplacement... componentReplacements) {
        return buildComponent(null, componentReplacements);
    }

    public ItemStack buildComponent(@Nullable Player player, ComponentReplacement... componentReplacements) {
        ItemStack itemStack = new ItemStack(getMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayNameComponent(getComponentName(player, componentReplacements).bungee());
        itemMeta.setLoreComponents(getComponentLore(player, componentReplacements).stream().map(LocalizedComponent::bungee).toList());
        if (model != -1)
            itemMeta.setCustomModelData(model);
        itemMeta.addItemFlags(flags.toArray(ItemFlag[]::new));
        enchantments.getMap().forEach((enchantment, integer) -> itemMeta.addEnchant(enchantment, integer, true));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public String getName() {
        return name;
    }

    public LocalizedComponent getName(@Nullable Player player, TextReplacement... textReplacements) {
        return I18n.global.getPlaceholderComponent(I18n.toLocale(player), player, name, textReplacements);
    }

    public Component getNameWithLore(@Nullable Player player, TextReplacement... textReplacements) {
        return getName(player, textReplacements).asComponent().hoverEvent(HoverEvent.showText(I18n.reduceComponent(getLore(player, textReplacements))));
    }

    public LocalizedComponent getComponentName(@Nullable Player player, ComponentReplacement... componentReplacements) {
        return I18n.global.getPlaceholderComponent(I18n.toLocale(player), player, name).replace(componentReplacements);
    }

    public Component getComponentNameWithLore(@Nullable Player player, ComponentReplacement... componentReplacements) {
        return getComponentName(player, componentReplacements).asComponent().hoverEvent(HoverEvent.showText(I18n.reduceComponent(getComponentLore(player, componentReplacements))));
    }

    public Material getMaterial() {
        return material;
    }

    public int getSlot() {
        return slot;
    }

    public int getModel() {
        return model;
    }

    public Collection<String> getLore() {
        return lore;
    }

    public List<LocalizedComponent> getLore(@Nullable Player player, TextReplacement... textReplacements) {
        return I18n.ofComponents(lore, s -> I18n.global.getPlaceholderComponent(I18n.toLocale(player), player, s, textReplacements));
    }

    public List<LocalizedComponent> getComponentLore(@Nullable Player player, ComponentReplacement... componentReplacements) {
        return I18n.ofComponents(lore, s -> I18n.global.getPlaceholderComponent(I18n.toLocale(player), player, s).replace(componentReplacements));
    }

    public Collection<ItemFlag> getFlags() {
        return flags;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments.getMap();
    }

}
