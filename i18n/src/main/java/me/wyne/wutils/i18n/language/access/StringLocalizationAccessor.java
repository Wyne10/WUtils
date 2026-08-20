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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link LocalizationAccessor} for a path whose value is a single string/component rather than a list.
 * Every {@code *List} method here returns a singleton list wrapping the single-value result.
 */
public record StringLocalizationAccessor(@NotNull String path, @NotNull Language language, @NotNull StringInterpreter stringInterpreter,
                                         @NotNull ComponentInterpreter componentInterpreter,
                                         @NotNull ComponentAudiences audiences) implements LocalizationAccessor {

    @Override
    public @NotNull LocalizedString getString(@NotNull TextReplacement... textReplacements) {
        return ls(language, path, stringInterpreter.getString(language, path, textReplacements));
    }

    @Override
    public @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable Player player) {
        return pls(language, path, stringInterpreter.getPlaceholderString(language, player, path), player);
    }

    @Override
    public @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable Player player, @NotNull TextReplacement... textReplacements) {
        return pls(language, path, stringInterpreter.getPlaceholderString(language, player, path, textReplacements), player);
    }

    @Override
    public @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable OfflinePlayer player) {
        return pls(language, path, stringInterpreter.getPlaceholderString(language, player, path), player);
    }

    @Override
    public @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable OfflinePlayer player, @NotNull TextReplacement... textReplacements) {
        return pls(language, path, stringInterpreter.getPlaceholderString(language, player, path, textReplacements), player);
    }

    @Override
    public @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable CommandSender sender) {
        return pls(language, path, stringInterpreter.getPlaceholderString(language, I18n.toPlayer(sender), path), I18n.toPlayer(sender));
    }

    @Override
    public @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable CommandSender sender, @NotNull TextReplacement... textReplacements) {
        return pls(language, path, stringInterpreter.getPlaceholderString(language, I18n.toPlayer(sender), path, textReplacements), I18n.toPlayer(sender));
    }

    @Override
    public @NotNull List<@NotNull LocalizedString> getStringList(@NotNull TextReplacement... textReplacements) {
        return linkedListOf(getString(textReplacements));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Player player) {
        return linkedListOf(getPlaceholderString(player));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Player player, @NotNull TextReplacement... textReplacements) {
        return linkedListOf(getPlaceholderString(player, textReplacements));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable OfflinePlayer player) {
        return linkedListOf(getPlaceholderString(player));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable OfflinePlayer player, @NotNull TextReplacement... textReplacements) {
        return linkedListOf(getPlaceholderString(player, textReplacements));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable CommandSender sender) {
        return linkedListOf(getPlaceholderString(sender));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable CommandSender sender, @NotNull TextReplacement... textReplacements) {
        return linkedListOf(getPlaceholderString(sender, textReplacements));
    }

    @Override
    public @NotNull LocalizedComponent getComponent(@NotNull TextReplacement... textReplacements) {
        return lc(language, path, componentInterpreter.getComponent(language, path, textReplacements), audiences);
    }

    @Override
    public @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Player player) {
        return plc(language, path, componentInterpreter.getPlaceholderComponent(language, player, path), audiences, player);
    }

    @Override
    public @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Player player, @NotNull TextReplacement... textReplacements) {
        return plc(language, path, componentInterpreter.getPlaceholderComponent(language, player, path, textReplacements), audiences, player);
    }

    @Override
    public @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable OfflinePlayer player) {
        return plc(language, path, componentInterpreter.getPlaceholderComponent(language, player, path), audiences, player);
    }

    @Override
    public @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable OfflinePlayer player, @NotNull TextReplacement... textReplacements) {
        return plc(language, path, componentInterpreter.getPlaceholderComponent(language, player, path, textReplacements), audiences, player);
    }

    @Override
    public @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable CommandSender sender) {
        return plc(language, path, componentInterpreter.getPlaceholderComponent(language, I18n.toPlayer(sender), path), audiences, I18n.toPlayer(sender));
    }

    @Override
    public @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable CommandSender sender, @NotNull TextReplacement... textReplacements) {
        return plc(language, path, componentInterpreter.getPlaceholderComponent(language, I18n.toPlayer(sender), path, textReplacements), audiences, I18n.toPlayer(sender));
    }

    @Override
    public @NotNull List<@NotNull LocalizedComponent> getComponentList(@NotNull TextReplacement... textReplacements) {
        return linkedListOf(getComponent(textReplacements));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Player player) {
        return linkedListOf(getPlaceholderComponent(player));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Player player, @NotNull TextReplacement... textReplacements) {
        return linkedListOf(getPlaceholderComponent(player, textReplacements));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable OfflinePlayer player) {
        return linkedListOf(getPlaceholderComponent(player));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable OfflinePlayer player, @NotNull TextReplacement... textReplacements) {
        return linkedListOf(getPlaceholderComponent(player, textReplacements));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable CommandSender sender) {
        return linkedListOf(getPlaceholderComponent(sender));
    }

    @Override
    public @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable CommandSender sender, @NotNull TextReplacement... textReplacements) {
        return linkedListOf(getPlaceholderComponent(sender, textReplacements));
    }

    @Override
    public @NotNull LocalizationAccessor withLanguage(@NotNull Language language) {
        return new StringLocalizationAccessor(path, language, stringInterpreter, componentInterpreter, audiences);
    }

    private <T> @NotNull List<@NotNull T> linkedListOf(@NotNull T element) {
        return new LinkedList<>(Collections.singletonList(element));
    }

}
