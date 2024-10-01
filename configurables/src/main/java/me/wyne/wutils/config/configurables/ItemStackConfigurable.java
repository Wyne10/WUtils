package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurable.Configurable;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class ItemStackConfigurable implements Configurable {

    private String name;
    private Material material;
    private int slot;
    private int model;
    private final Collection<String> lore;

    public ItemStackConfigurable(ConfigurationSection itemSection)
    {
        this.lore = new ArrayList<>();
        fromConfig(itemSection);
    }

    public ItemStackConfigurable(String name, Material material, int slot, int model, Collection<String> lore) {
        this.name = name;
        this.material = material;
        this.slot = slot;
        this.model = model;
        this.lore = lore;
    }

    @Override
    public String toConfig(ConfigEntry configEntry) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.append(1, "name", name);
        configBuilder.append(1, "material", material);
        configBuilder.append(1, "slot", slot != -1 ? slot : null);
        configBuilder.append(1, "model", model != -1 ? model : null);
        configBuilder.appendCollection(1, "lore", lore);
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
        lore.addAll(config.getStringList("lore"));
    }

    public String getName() {
        return name;
    }

    public Component getName(@Nullable Player player, TextReplacement... textReplacements) {
        if (name.trim().isEmpty())
            return Component.empty();
        return I18n.global.getLegacyPlaceholderComponent(I18n.toLocale(player), player, name, textReplacements);
    }

    public Component getNameWithLore(@Nullable Player player, TextReplacement... textReplacements) {
        return getName(player, textReplacements).hoverEvent(HoverEvent.showText(I18n.reduceComponent(getLore(player, textReplacements))));
    }

    public Component getName(@Nullable Player player, TagResolver... tagResolvers) {
        if (name.trim().isEmpty())
            return Component.empty();
        return I18n.global.getPlaceholderComponent(I18n.toLocale(player), player, name, tagResolvers);
    }

    public Component getNameWithLore(@Nullable Player player, TagResolver... tagResolvers) {
        return getName(player, tagResolvers).hoverEvent(HoverEvent.showText(I18n.reduceComponent(getLore(player, tagResolvers))));
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

    public List<Component> getLore(@Nullable Player player, TagResolver... tagResolvers) {
        return lore.stream()
                .flatMap(s -> {
                    if (I18n.global.contains(I18n.toLocale(player), s))
                        return I18n.global.getPlaceholderComponentList(I18n.toLocale(player), player, s, tagResolvers).stream();
                    else
                        return Stream.of(I18n.global.getPlaceholderComponent(I18n.toLocale(player), player, s, tagResolvers));
                })
                .toList();
    }

    public List<Component> getLore(@Nullable Player player, TextReplacement... textReplacements) {
        return lore.stream()
                .flatMap(s -> {
                    if (I18n.global.contains(I18n.toLocale(player), s))
                        return I18n.global.getLegacyPlaceholderComponentList(I18n.toLocale(player), player, s, textReplacements).stream();
                    else
                        return Stream.of(I18n.global.getLegacyPlaceholderComponent(I18n.toLocale(player), player, s, textReplacements));
                })
                .toList();
    }
}
