package me.wyne.wutils.i18n.language.access;

import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.component.*;
import me.wyne.wutils.i18n.language.interpretation.ComponentInterpreter;
import me.wyne.wutils.i18n.language.interpretation.StringInterpreter;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LocalizationAccessor {
    String getPath();

    Language getLanguage();

    StringInterpreter getStringInterpreter();

    ComponentInterpreter getComponentInterpreter();

    default boolean contains() {
        return getLanguage().contains(getPath());
    }

    LocalizedString getString(TextReplacement... textReplacements);

    PlaceholderLocalizedString getPlaceholderString(@Nullable Player player);

    PlaceholderLocalizedString getPlaceholderString(@Nullable Player player, TextReplacement... textReplacements);

    PlaceholderLocalizedString getPlaceholderString(@Nullable OfflinePlayer player);

    PlaceholderLocalizedString getPlaceholderString(@Nullable OfflinePlayer player, TextReplacement... textReplacements);

    PlaceholderLocalizedString getPlaceholderString(@Nullable CommandSender sender);

    PlaceholderLocalizedString getPlaceholderString(@Nullable CommandSender sender, TextReplacement... textReplacements);

    List<LocalizedString> getStringList(TextReplacement... textReplacements);

    List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Player player);

    List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Player player, TextReplacement... textReplacements);

    List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable OfflinePlayer player);

    List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable OfflinePlayer player, TextReplacement... textReplacements);

    List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable CommandSender sender);

    List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable CommandSender sender, TextReplacement... textReplacements);

    LocalizedComponent getComponent(TextReplacement... textReplacements);

    PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Player player);

    PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Player player, TextReplacement... textReplacements);

    PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable OfflinePlayer player);

    PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable OfflinePlayer player, TextReplacement... textReplacements);

    PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable CommandSender sender);

    PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable CommandSender sender, TextReplacement... textReplacements);

    List<LocalizedComponent> getComponentList(TextReplacement... textReplacements);

    List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Player player);

    List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Player player, TextReplacement... textReplacements);

    List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable OfflinePlayer player);

    List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable OfflinePlayer player, TextReplacement... textReplacements);

    List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable CommandSender sender);

    List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable CommandSender sender, TextReplacement... textReplacements);

    default LocalizedString ls(Language language, String path, String string) {
        return new LocalizedString(I18n.global.string(), language, path, string);
    }

    default PlaceholderLocalizedString pls(Language language, String path, String string, @Nullable OfflinePlayer player) {
        return new PlaceholderLocalizedString(I18n.global.string(), language, path, string, player);
    }

    default LocalizedComponent lc(Language language, String path, Component component, ComponentAudiences audiences) {
        return new LocalizedComponent(I18n.global.component(), language, path, component, audiences);
    }

    default PlaceholderLocalizedComponent plc(Language language, String path, Component component, ComponentAudiences audiences, @Nullable OfflinePlayer player) {
        return new PlaceholderLocalizedComponent(I18n.global.component(), language, path, component, audiences, player);
    }
}
