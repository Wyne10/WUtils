package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.PlaceholderAPIWrapper;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import me.wyne.wutils.i18n.language.validation.StringValidator;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BaseInterpreter implements StringInterpreter {

    protected StringValidator stringValidator;

    public BaseInterpreter(StringValidator stringValidator) {
        this.stringValidator = stringValidator;
    }

    @Override
    public void setStringValidator(StringValidator stringValidator) {
        this.stringValidator = stringValidator;
    }

    @Override
    public String getString(Language language, String path) {
        return stringValidator.validateString(language.getLanguageCode(), language.getStrings(), path);
    }

    @Override
    public String getString(Language language, String path, TextReplacement... textReplacements) {
        return applyTextReplacements(getString(language, path), textReplacements);
    }

    @Override
    public String getPlaceholderString(Language language, @Nullable Player player, String path)
    {
        return PlaceholderAPIWrapper.setPlaceholders(player, getString(language, path));
    }

    @Override
    public String getPlaceholderString(Language language, @Nullable Player player, String path, TextReplacement... textReplacements)
    {
        return PlaceholderAPIWrapper.setPlaceholders(player, getString(language, path, textReplacements));
    }

    @Override
    public String getPlaceholderString(Language language, @Nullable OfflinePlayer player, String path)
    {
        return PlaceholderAPIWrapper.setPlaceholders(player, getString(language, path));
    }

    @Override
    public String getPlaceholderString(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements)
    {
        return PlaceholderAPIWrapper.setPlaceholders(player, getString(language, path, textReplacements));
    }

    @Override
    public List<String> getStringList(Language language, String path) {
        return language.getStrings().getStringList(path).stream()
                .map(s -> stringValidator.validateString(language.getLanguageCode(), language.getStrings(), s))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<String> getStringList(Language language, String path, TextReplacement... textReplacements) {
        return language.getStrings().getStringList(path).stream()
                .map(s -> stringValidator.validateString(language.getLanguageCode(), language.getStrings(), s))
                .map(s -> applyTextReplacements(s, textReplacements))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<String> getPlaceholderStringList(Language language, @Nullable Player player, String path) {
        return getStringList(language, path).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .toList();
    }

    @Override
    public List<String> getPlaceholderStringList(Language language, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return getStringList(language, path, textReplacements).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .toList();
    }

    @Override
    public List<String> getPlaceholderStringList(Language language, @Nullable OfflinePlayer player, String path) {
        return getStringList(language, path).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .toList();
    }

    @Override
    public List<String> getPlaceholderStringList(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return getStringList(language, path, textReplacements).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .toList();
    }

}
