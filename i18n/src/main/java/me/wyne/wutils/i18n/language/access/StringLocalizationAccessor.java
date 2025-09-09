package me.wyne.wutils.i18n.language.access;

import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.component.*;
import me.wyne.wutils.i18n.language.interpretation.ComponentInterpreter;
import me.wyne.wutils.i18n.language.interpretation.StringInterpreter;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StringLocalizationAccessor implements LocalizationAccessor {

    private final String path;
    private final Language language;
    private final StringInterpreter stringInterpreter;
    private final ComponentInterpreter componentInterpreter;
    private final ComponentAudiences audiences;

    public StringLocalizationAccessor(String path, Language language, StringInterpreter stringInterpreter, ComponentInterpreter componentInterpreter, ComponentAudiences audiences) {
        this.path = path;
        this.language = language;
        this.stringInterpreter = stringInterpreter;
        this.componentInterpreter = componentInterpreter;
        this.audiences = audiences;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    @Override
    public StringInterpreter getStringInterpreter() {
        return stringInterpreter;
    }

    @Override
    public ComponentInterpreter getComponentInterpreter() {
        return componentInterpreter;
    }

    @Override
    public LocalizedString getString(TextReplacement... textReplacements) {
        return ls(language, path, stringInterpreter.getString(language, path, textReplacements));
    }

    @Override
    public PlaceholderLocalizedString getPlaceholderString(@Nullable Player player) {
        return pls(language, path, stringInterpreter.getPlaceholderString(language, player, path), player);
    }

    @Override
    public PlaceholderLocalizedString getPlaceholderString(@Nullable Player player, TextReplacement... textReplacements) {
        return pls(language, path, stringInterpreter.getPlaceholderString(language, player, path, textReplacements), player);
    }

    @Override
    public PlaceholderLocalizedString getPlaceholderString(@Nullable OfflinePlayer player) {
        return pls(language, path, stringInterpreter.getPlaceholderString(language, player, path), player);
    }

    @Override
    public PlaceholderLocalizedString getPlaceholderString(@Nullable OfflinePlayer player, TextReplacement... textReplacements) {
        return pls(language, path, stringInterpreter.getPlaceholderString(language, player, path, textReplacements), player);
    }

    @Override
    public PlaceholderLocalizedString getPlaceholderString(@Nullable CommandSender sender) {
        return pls(language, path, stringInterpreter.getPlaceholderString(language, I18n.toPlayer(sender), path), I18n.toPlayer(sender));
    }

    @Override
    public PlaceholderLocalizedString getPlaceholderString(@Nullable CommandSender sender, TextReplacement... textReplacements) {
        return pls(language, path, stringInterpreter.getPlaceholderString(language, I18n.toPlayer(sender), path, textReplacements), I18n.toPlayer(sender));
    }

    @Override
    public List<LocalizedString> getStringList(TextReplacement... textReplacements) {
        return List.of(getString(textReplacements));
    }

    @Override
    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Player player) {
        return List.of(getPlaceholderString(player));
    }

    @Override
    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Player player, TextReplacement... textReplacements) {
        return List.of(getPlaceholderString(player, textReplacements));
    }

    @Override
    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable OfflinePlayer player) {
        return List.of(getPlaceholderString(player));
    }

    @Override
    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable OfflinePlayer player, TextReplacement... textReplacements) {
        return List.of(getPlaceholderString(player, textReplacements));
    }

    @Override
    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable CommandSender sender) {
        return List.of(getPlaceholderString(sender));
    }

    @Override
    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable CommandSender sender, TextReplacement... textReplacements) {
        return List.of(getPlaceholderString(sender, textReplacements));
    }

    @Override
    public LocalizedComponent getComponent(TextReplacement... textReplacements) {
        return lc(language, path, componentInterpreter.getComponent(language, path, textReplacements), audiences);
    }

    @Override
    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Player player) {
        return plc(language, path, componentInterpreter.getPlaceholderComponent(language, player, path), audiences, player);
    }

    @Override
    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Player player, TextReplacement... textReplacements) {
        return plc(language, path, componentInterpreter.getPlaceholderComponent(language, player, path, textReplacements), audiences, player);
    }

    @Override
    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable OfflinePlayer player) {
        return plc(language, path, componentInterpreter.getPlaceholderComponent(language, player, path), audiences, player);
    }

    @Override
    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable OfflinePlayer player, TextReplacement... textReplacements) {
        return plc(language, path, componentInterpreter.getPlaceholderComponent(language, player, path, textReplacements), audiences, player);
    }

    @Override
    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable CommandSender sender) {
        return plc(language, path, componentInterpreter.getPlaceholderComponent(language, I18n.toPlayer(sender), path), audiences, I18n.toPlayer(sender));
    }

    @Override
    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable CommandSender sender, TextReplacement... textReplacements) {
        return plc(language, path, componentInterpreter.getPlaceholderComponent(language, I18n.toPlayer(sender), path, textReplacements), audiences, I18n.toPlayer(sender));
    }

    @Override
    public List<LocalizedComponent> getComponentList(TextReplacement... textReplacements) {
        return List.of(getComponent(textReplacements));
    }

    @Override
    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Player player) {
        return List.of(getPlaceholderComponent(player));
    }

    @Override
    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Player player, TextReplacement... textReplacements) {
        return List.of(getPlaceholderComponent(player, textReplacements));
    }

    @Override
    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable OfflinePlayer player) {
        return List.of(getPlaceholderComponent(player));
    }

    @Override
    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable OfflinePlayer player, TextReplacement... textReplacements) {
        return List.of(getPlaceholderComponent(player, textReplacements));
    }

    @Override
    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable CommandSender sender) {
        return List.of(getPlaceholderComponent(sender));
    }

    @Override
    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable CommandSender sender, TextReplacement... textReplacements) {
        return List.of(getPlaceholderComponent(sender, textReplacements));
    }

    @Override
    public LocalizationAccessor withLanguage(Language language) {
        return new StringLocalizationAccessor(path, language, stringInterpreter, componentInterpreter, audiences);
    }

}
