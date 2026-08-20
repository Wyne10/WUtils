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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link LocalizationAccessor} for a path whose value is a list. Every single-value method here
 * reduces the list result via {@link I18n#reduceString} / {@link I18n#reduceComponent}, joining
 * multiple entries into one value.
 */
public record ListLocalizationAccessor(@NotNull String path, @NotNull Language language, @NotNull StringInterpreter stringInterpreter,
                                       @NotNull ComponentInterpreter componentInterpreter,
                                       @NotNull ComponentAudiences audiences) implements LocalizationAccessor {

    @Override
    public @NotNull LocalizedString getString(@NotNull TextReplacement... textReplacements) {
        return ls(language, path, I18n.reduceString(getStringList(textReplacements)));
    }

    @Override
    public @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable Player player) {
        return pls(language, path, I18n.reduceString(getPlaceholderStringList(player)), player);
    }

    @Override
    public @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable Player player, @NotNull TextReplacement... textReplacements) {
        return pls(language, path, I18n.reduceString(getPlaceholderStringList(player, textReplacements)), player);
    }

    @Override
    public @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable OfflinePlayer player) {
        return pls(language, path, I18n.reduceString(getPlaceholderStringList(player)), player);
    }

    @Override
    public @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable OfflinePlayer player, @NotNull TextReplacement... textReplacements) {
        return pls(language, path, I18n.reduceString(getPlaceholderStringList(player, textReplacements)), player);
    }

    @Override
    public @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable CommandSender sender) {
        return pls(language, path, I18n.reduceString(getPlaceholderStringList(sender)), I18n.toPlayer(sender));
    }

    @Override
    public @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable CommandSender sender, @NotNull TextReplacement... textReplacements) {
        return pls(language, path, I18n.reduceString(getPlaceholderStringList(sender, textReplacements)), I18n.toPlayer(sender));
    }

    @Override
    public @NotNull List<@NotNull LocalizedString> getStringList(@NotNull TextReplacement... textReplacements) {
        return stringInterpreter.getStringList(language, path, textReplacements).stream().map(string -> ls(language, path, string)).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Player player) {
        return stringInterpreter.getPlaceholderStringList(language, player, path).stream().map(string -> pls(language, path, string, player)).collect(Collectors.toCollection(LinkedList::new));
    }


    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Player player, @NotNull TextReplacement... textReplacements) {
        return stringInterpreter.getPlaceholderStringList(language, player, path, textReplacements).stream().map(string -> pls(language, path, string, player)).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable OfflinePlayer player) {
        return stringInterpreter.getPlaceholderStringList(language, player, path).stream().map(string -> pls(language, path, string, player)).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable OfflinePlayer player, @NotNull TextReplacement... textReplacements) {
        return stringInterpreter.getPlaceholderStringList(language, player, path, textReplacements).stream().map(string -> pls(language, path, string, player)).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable CommandSender sender) {
        return stringInterpreter.getPlaceholderStringList(language, I18n.toPlayer(sender), path).stream().map(string -> pls(language, path, string, I18n.toPlayer(sender))).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable CommandSender sender, @NotNull TextReplacement... textReplacements) {
        return stringInterpreter.getPlaceholderStringList(language, I18n.toPlayer(sender), path, textReplacements).stream().map(string -> pls(language, path, string, I18n.toPlayer(sender))).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull LocalizedComponent getComponent(@NotNull TextReplacement... textReplacements) {
        return lc(language, path, I18n.reduceComponent(getComponentList(textReplacements)), audiences);
    }

    @Override
    public @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Player player) {
        return plc(language, path, I18n.reduceComponent(getPlaceholderComponentList(player)), audiences, player);
    }

    @Override
    public @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Player player, @NotNull TextReplacement... textReplacements) {
        return plc(language, path, I18n.reduceComponent(getPlaceholderComponentList(player, textReplacements)), audiences, player);
    }

    @Override
    public @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable OfflinePlayer player) {
        return plc(language, path, I18n.reduceComponent(getPlaceholderComponentList(player)), audiences, player);
    }

    @Override
    public @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable OfflinePlayer player, @NotNull TextReplacement... textReplacements) {
        return plc(language, path, I18n.reduceComponent(getPlaceholderComponentList(player, textReplacements)), audiences, player);
    }

    @Override
    public @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable CommandSender sender) {
        return plc(language, path, I18n.reduceComponent(getPlaceholderComponentList(sender)), audiences, I18n.toPlayer(sender));
    }

    @Override
    public @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable CommandSender sender, @NotNull TextReplacement... textReplacements) {
        return plc(language, path, I18n.reduceComponent(getPlaceholderComponentList(sender, textReplacements)), audiences, I18n.toPlayer(sender));
    }

    @Override
    public @NotNull List<@NotNull LocalizedComponent> getComponentList(@NotNull TextReplacement... textReplacements) {
        return componentInterpreter.getComponentList(language, path, textReplacements).stream().map(component -> lc(language, path, component, audiences)).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Player player) {
        return componentInterpreter.getPlaceholderComponentList(language, player, path).stream().map(component -> plc(language, path, component, audiences, player)).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Player player, @NotNull TextReplacement... textReplacements) {
        return componentInterpreter.getPlaceholderComponentList(language, player, path, textReplacements).stream().map(component -> plc(language, path, component, audiences, player)).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable OfflinePlayer player) {
        return componentInterpreter.getPlaceholderComponentList(language, player, path).stream().map(component -> plc(language, path, component, audiences, player)).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable OfflinePlayer player, @NotNull TextReplacement... textReplacements) {
        return componentInterpreter.getPlaceholderComponentList(language, player, path, textReplacements).stream().map(component -> plc(language, path, component, audiences, player)).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable CommandSender sender) {
        return componentInterpreter.getPlaceholderComponentList(language, I18n.toPlayer(sender), path).stream().map(component -> plc(language, path, component, audiences, I18n.toPlayer(sender))).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable CommandSender sender, @NotNull TextReplacement... textReplacements) {
        return componentInterpreter.getPlaceholderComponentList(language, I18n.toPlayer(sender), path, textReplacements).stream().map(component -> plc(language, path, component, audiences, I18n.toPlayer(sender))).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull LocalizationAccessor withLanguage(@NotNull Language language) {
        return new ListLocalizationAccessor(path, language, stringInterpreter, componentInterpreter, audiences);
    }

}
