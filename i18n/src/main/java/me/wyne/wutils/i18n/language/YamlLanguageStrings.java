package me.wyne.wutils.i18n.language;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public record YamlLanguageStrings(ConfigurationSection section) implements LanguageStrings {

    @Override
    public boolean contains(String path) {
        return section.contains(path);
    }

    @Override
    public boolean isList(String path) {
        return section.isList(path);
    }

    @Override
    public List<String> getStringList(String path) {
        return section.getStringList(path);
    }

}
