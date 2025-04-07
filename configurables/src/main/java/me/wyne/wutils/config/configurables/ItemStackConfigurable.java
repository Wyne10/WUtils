package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.ConfigUtils;
import me.wyne.wutils.common.MapUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurable.Configurable;
import me.wyne.wutils.i18n.I18n;
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

public class ItemStackConfigurable implements Configurable {

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

    public ItemStackConfigurable(Object configObject) {
        fromConfig(configObject);
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

    public ItemStackConfigurable(String name, Material material, int slot, Collection<String> lore) {
        this(name, material, slot, -1, lore, Collections.emptyList(), Collections.emptyMap());
    }

    @Override
    public String toConfig(ConfigEntry configEntry) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.append(2, "name", name);
        configBuilder.append(2, "material", material);
        configBuilder.append(2, "slot", slot != -1 ? slot : null);
        configBuilder.append(2, "model", model != -1 ? model : null);
        configBuilder.appendCollection(2, "lore", lore);
        configBuilder.appendCollection(2, "flags", flags);
        String enchantmentsString = enchantments.toConfig(3, configEntry);
        if (!enchantmentsString.isEmpty()) {
            configBuilder.appendString(2, "enchantments", enchantmentsString);
        }
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
        if (config.contains("enchantments"))
            enchantments.fromConfig(config.getConfigurationSection("enchantments"));
    }

    public ItemStack build(TextReplacement... textReplacements)
    {
        return build(null, textReplacements);
    }

    public ItemStack build(@Nullable Player player, TextReplacement... textReplacements)
    {
        ItemStack itemStack = new ItemStack(getMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(getName(player, textReplacements));
        itemMeta.lore(getLore(player, textReplacements));
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

    public Component getName(@Nullable Player player, TextReplacement... textReplacements) {
        if (name.trim().isEmpty())
            return Component.empty();
        return I18n.global.getPlaceholderComponent(I18n.toLocale(player), player, name, textReplacements);
    }

    public Component getNameWithLore(@Nullable Player player, TextReplacement... textReplacements) {
        return getName(player, textReplacements).hoverEvent(HoverEvent.showText(I18n.reduceComponent(getLore(player, textReplacements))));
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

    public List<Component> getLore(@Nullable Player player, TextReplacement... textReplacements) {
        return I18n.ofComponents(lore, s -> I18n.global.getPlaceholderComponent(I18n.toLocale(player), player, s, textReplacements));
    }

    public Collection<ItemFlag> getFlags() {
        return flags;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments.getMap();
    }

}
