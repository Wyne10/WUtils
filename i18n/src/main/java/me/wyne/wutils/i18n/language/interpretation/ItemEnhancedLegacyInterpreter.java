package me.wyne.wutils.i18n.language.interpretation;

import dev.vankka.enhancedlegacytext.EnhancedLegacyText;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import me.wyne.wutils.i18n.language.validation.StringValidator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link ComponentInterpreter} backed by vankka's EnhancedLegacyText parser, wrapping every result in
 * a non-italic empty root component to strip the default item-lore italicization (see
 * {@link ItemLegacyInterpreter}). Requires the {@code enhancedlegacytext} dependency on the consumer's
 * classpath — this module only declares it {@code compileOnly}.
 *
 * <p>Note this class extends {@link LegacyInterpreter}, not {@link EnhancedLegacyInterpreter}: like its
 * non-item sibling, it has no serializer of its own, so {@link #toString} is inherited from
 * {@link LegacyInterpreter} and uses the plain legacy ({@code &}-code) format.</p>
 */
public class ItemEnhancedLegacyInterpreter extends LegacyInterpreter implements ComponentInterpreter {

    public ItemEnhancedLegacyInterpreter(@NotNull StringValidator stringValidator) {
        super(stringValidator);
    }

    @Override
    public @NotNull Component getComponent(@NotNull Language language, @NotNull String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().parse(getString(language, path)));
    }

    @Override
    public @NotNull Component getComponent(@NotNull Language language, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().parse(getString(language, path, textReplacements)));
    }

    @Override
    public @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable Player player, @NotNull String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().parse(getPlaceholderString(language, player, path)));
    }

    @Override
    public @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable Player player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().parse(getPlaceholderString(language, player, path, textReplacements)));
    }

    @Override
    public @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().parse(getPlaceholderString(language, player, path)));
    }

    @Override
    public @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().parse(getPlaceholderString(language, player, path, textReplacements)));
    }

    @Override
    public @NotNull List<@NotNull Component> getComponentList(@NotNull Language language, @NotNull String path) {
        return getStringList(language, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().parse(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getComponentList(@NotNull Language language, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return getStringList(language, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().parse(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable Player player, @NotNull String path) {
        return getPlaceholderStringList(language, player, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().parse(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable Player player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().parse(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path) {
        return getPlaceholderStringList(language, player, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().parse(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(EnhancedLegacyText.get().parse(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull Component fromString(@NotNull String string) {
        return EnhancedLegacyText.get().parse(string);
    }

}
