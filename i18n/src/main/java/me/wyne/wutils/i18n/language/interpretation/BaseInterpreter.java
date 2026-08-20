package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.PlaceholderAPIWrapper;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import me.wyne.wutils.i18n.language.validation.StringValidator;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default {@link StringInterpreter}, and the base class every {@link ComponentInterpreter}
 * implementation in this module builds on.
 *
 * <p>Single-value lookups ({@link #getString}) resolve {@code path} against
 * {@link Language#getStringMap()}; list lookups ({@link #getStringList}) resolve it against
 * {@link Language#getStrings()}. Both follow dotted nested paths.</p>
 */
public class BaseInterpreter implements StringInterpreter {

    private StringValidator stringValidator;

    public BaseInterpreter(@NotNull StringValidator stringValidator) {
        this.stringValidator = stringValidator;
    }

    @Override
    public void setStringValidator(@NotNull StringValidator stringValidator) {
        this.stringValidator = stringValidator;
    }

    @Override
    public @NotNull StringValidator getStringValidator() {
        return stringValidator;
    }

    @Override
    public @NotNull String getString(@NotNull Language language, @NotNull String path) {
        return stringValidator.validateString(language.getStringMap(), path);
    }

    @Override
    public @NotNull String getString(@NotNull Language language, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return I18n.applyTextReplacements(getString(language, path), textReplacements);
    }

    @Override
    public @NotNull String getPlaceholderString(@NotNull Language language, @Nullable Player player, @NotNull String path) {
        return PlaceholderAPIWrapper.setPlaceholders(player, getString(language, path));
    }

    @Override
    public @NotNull String getPlaceholderString(@NotNull Language language, @Nullable Player player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return PlaceholderAPIWrapper.setPlaceholders(player, getString(language, path, textReplacements));
    }

    @Override
    public @NotNull String getPlaceholderString(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path) {
        return PlaceholderAPIWrapper.setPlaceholders(player, getString(language, path));
    }

    @Override
    public @NotNull String getPlaceholderString(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return PlaceholderAPIWrapper.setPlaceholders(player, getString(language, path, textReplacements));
    }

    /**
     * Treats each element of the string list at {@code path} as a lookup path in its own right, not as
     * literal text: every element is run back through the configured {@link StringValidator} against
     * {@link Language#getStringMap()}. With the default validator (which returns the path itself when no
     * such key exists) this normally looks like a pass-through — but a list element that happens to match
     * a key name is silently replaced by that key's value instead of being kept verbatim. Since keys are
     * full dotted paths, an element such as {@code messages.welcome} is a live candidate for that.
     */
    @Override
    public @NotNull List<@NotNull String> getStringList(@NotNull Language language, @NotNull String path) {
        return language.getStrings().getStringList(path).stream()
                .map(s -> stringValidator.validateString(language.getStringMap(), s))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * @see #getStringList(Language, String)
     */
    @Override
    public @NotNull List<@NotNull String> getStringList(@NotNull Language language, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return language.getStrings().getStringList(path).stream()
                .map(s -> stringValidator.validateString(language.getStringMap(), s))
                .map(s -> I18n.applyTextReplacements(s, textReplacements))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull String> getPlaceholderStringList(@NotNull Language language, @Nullable Player player, @NotNull String path) {
        return getStringList(language, path).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull String> getPlaceholderStringList(@NotNull Language language, @Nullable Player player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return getStringList(language, path, textReplacements).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull String> getPlaceholderStringList(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path) {
        return getStringList(language, path).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull String> getPlaceholderStringList(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return getStringList(language, path, textReplacements).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .collect(Collectors.toCollection(LinkedList::new));
    }

}
