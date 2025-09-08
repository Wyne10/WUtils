package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.ConfigUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.component.LocalizedComponent;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ViewConfigurable implements CompositeConfigurable {

    private String name = "";
    private final List<String> lore = new ArrayList<>();

    public ViewConfigurable() {}

    public ViewConfigurable(ConfigurationSection section) {
        fromConfig(section);
    }

    public ViewConfigurable(String name, Collection<String> lore) {
        this.name = name;
        this.lore.addAll(lore);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.append(depth, "name", name);
        configBuilder.appendCollection(depth, "lore", lore);
        return configBuilder.build();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        ConfigurationSection section = (ConfigurationSection) configObject;
        name = section.getString("name", "");
        lore.clear();
        lore.addAll(ConfigUtils.getStringList(section, "lore"));
    }

    public String getName() {
        return name;
    }

    public LocalizedComponent getName(@Nullable Player player, TextReplacement... textReplacements) {
        return I18n.global.accessor(player, name).getPlaceholderComponent(player, textReplacements);
    }

    public List<String> getLore() {
        return lore;
    }

    public List<LocalizedComponent> getLore(@Nullable Player player, TextReplacement... textReplacements) {
        return I18n.ofComponents(lore, s -> I18n.global.accessor(player, s).getPlaceholderComponent(player, textReplacements));
    }

}
