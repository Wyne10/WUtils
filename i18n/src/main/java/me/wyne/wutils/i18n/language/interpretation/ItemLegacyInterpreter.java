package me.wyne.wutils.i18n.language.interpretation;

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
 * {@link ComponentInterpreter} backed by {@link LegacyInterpreter#SERIALIZER}, wrapping every result
 * in a non-italic empty root component. Vanilla clients render item lore italic by default; wrapping
 * strips that default while still letting the source string opt back into italics explicitly.
 */
public class ItemLegacyInterpreter extends BaseInterpreter implements ComponentInterpreter {

    public ItemLegacyInterpreter(@NotNull StringValidator stringValidator) {
        super(stringValidator);
    }

    @Override
    public @NotNull Component getComponent(@NotNull Language language, @NotNull String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyInterpreter.SERIALIZER.deserialize(getString(language, path)));
    }

    @Override
    public @NotNull Component getComponent(@NotNull Language language, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyInterpreter.SERIALIZER.deserialize(getString(language, path, textReplacements)));
    }

    @Override
    public @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable Player player, @NotNull String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyInterpreter.SERIALIZER.deserialize(getPlaceholderString(language, player, path)));
    }

    @Override
    public @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable Player player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyInterpreter.SERIALIZER.deserialize(getPlaceholderString(language, player, path, textReplacements)));
    }

    @Override
    public @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyInterpreter.SERIALIZER.deserialize(getPlaceholderString(language, player, path)));
    }

    @Override
    public @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyInterpreter.SERIALIZER.deserialize(getPlaceholderString(language, player, path, textReplacements)));
    }

    @Override
    public @NotNull List<@NotNull Component> getComponentList(@NotNull Language language, @NotNull String path) {
        return getStringList(language, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyInterpreter.SERIALIZER.deserialize(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getComponentList(@NotNull Language language, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return getStringList(language, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyInterpreter.SERIALIZER.deserialize(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable Player player, @NotNull String path) {
        return getPlaceholderStringList(language, player, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyInterpreter.SERIALIZER.deserialize(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable Player player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyInterpreter.SERIALIZER.deserialize(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path) {
        return getPlaceholderStringList(language, player, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyInterpreter.SERIALIZER.deserialize(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyInterpreter.SERIALIZER.deserialize(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull String toString(@NotNull Component component) {
        return LegacyInterpreter.SERIALIZER.serialize(component);
    }

    @Override
    public @NotNull Component fromString(@NotNull String string) {
        return LegacyInterpreter.SERIALIZER.deserialize(string);
    }
}
