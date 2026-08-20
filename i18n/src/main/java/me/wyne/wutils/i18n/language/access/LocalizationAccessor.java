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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Resolves a fixed {@code (language, path)} pair to {@link LocalizedString}/{@link LocalizedComponent}
 * values, on demand and without caching them itself. See {@link StringLocalizationAccessor} and
 * {@link ListLocalizationAccessor} for the two implementations: {@code I18n.accessor(...)} chooses
 * between them once per {@code (language, path)} pair, based on whether the path is a list at the time
 * of the first lookup, and caches that choice — so if the underlying language file later changes shape
 * at that path, the cached accessor kind does not follow.
 */
public interface LocalizationAccessor {
    @NotNull String path();

    @NotNull Language language();

    @NotNull StringInterpreter stringInterpreter();

    @NotNull ComponentInterpreter componentInterpreter();

    /**
     * Returns whether {@link #path()} exists in {@link #language()}.
     */
    default boolean contains() {
        return language().getStrings().contains(path());
    }

    @NotNull LocalizedString getString(@NotNull TextReplacement... textReplacements);

    @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable Player player);

    @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable Player player, @NotNull TextReplacement... textReplacements);

    @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable OfflinePlayer player);

    @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable OfflinePlayer player, @NotNull TextReplacement... textReplacements);

    @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable CommandSender sender);

    @NotNull PlaceholderLocalizedString getPlaceholderString(@Nullable CommandSender sender, @NotNull TextReplacement... textReplacements);

    @NotNull List<@NotNull LocalizedString> getStringList(@NotNull TextReplacement... textReplacements);

    @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Player player);

    @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Player player, @NotNull TextReplacement... textReplacements);

    @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable OfflinePlayer player);

    @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable OfflinePlayer player, @NotNull TextReplacement... textReplacements);

    @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable CommandSender sender);

    @NotNull List<@NotNull PlaceholderLocalizedString> getPlaceholderStringList(@Nullable CommandSender sender, @NotNull TextReplacement... textReplacements);

    @NotNull LocalizedComponent getComponent(@NotNull TextReplacement... textReplacements);

    @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Player player);

    @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Player player, @NotNull TextReplacement... textReplacements);

    @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable OfflinePlayer player);

    @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable OfflinePlayer player, @NotNull TextReplacement... textReplacements);

    @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable CommandSender sender);

    @NotNull PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable CommandSender sender, @NotNull TextReplacement... textReplacements);

    @NotNull List<@NotNull LocalizedComponent> getComponentList(@NotNull TextReplacement... textReplacements);

    @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Player player);

    @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Player player, @NotNull TextReplacement... textReplacements);

    @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable OfflinePlayer player);

    @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable OfflinePlayer player, @NotNull TextReplacement... textReplacements);

    @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable CommandSender sender);

    @NotNull List<@NotNull PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable CommandSender sender, @NotNull TextReplacement... textReplacements);

    /**
     * Returns an accessor for the same {@link #path()} against {@code language} instead.
     */
    @NotNull LocalizationAccessor withLanguage(@NotNull Language language);

    @NotNull
    default LocalizedString ls(@NotNull Language language, @NotNull String path, @NotNull String string) {
        return new LocalizedString(I18n.global.string(), language, path, string);
    }

    @NotNull
    default PlaceholderLocalizedString pls(@NotNull Language language, @NotNull String path, @NotNull String string, @Nullable OfflinePlayer player) {
        return new PlaceholderLocalizedString(I18n.global.string(), language, path, string, player);
    }

    @NotNull
    default LocalizedComponent lc(@NotNull Language language, @NotNull String path, @NotNull Component component, @NotNull ComponentAudiences audiences) {
        return new LocalizedComponent(I18n.global.component(), language, path, component, audiences);
    }

    @NotNull
    default PlaceholderLocalizedComponent plc(@NotNull Language language, @NotNull String path, @NotNull Component component, @NotNull ComponentAudiences audiences, @Nullable OfflinePlayer player) {
        return new PlaceholderLocalizedComponent(I18n.global.component(), language, path, component, audiences, player);
    }
}
