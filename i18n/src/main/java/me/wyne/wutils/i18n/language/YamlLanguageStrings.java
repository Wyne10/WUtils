package me.wyne.wutils.i18n.language;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** {@link LanguageStrings} view over a Bukkit {@link ConfigurationSection}, resolving dotted nested paths. */
public record YamlLanguageStrings(@NotNull ConfigurationSection section) implements LanguageStrings {

    @Override
    public boolean contains(@NotNull String path) {
        return section.contains(path);
    }

    @Override
    public boolean isList(@NotNull String path) {
        return section.isList(path);
    }

    @Override
    public @NotNull List<@NotNull String> getStringList(@NotNull String path) {
        return section.getStringList(path);
    }

}
