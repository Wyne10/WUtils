package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import me.wyne.wutils.i18n.language.validation.StringValidator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link ComponentInterpreter} backed by Adventure's legacy ({@code &}-code) serializer.
 */
public class LegacyInterpreter extends BaseInterpreter implements ComponentInterpreter {

    /** Legacy serializer using the {@code &} formatting character. */
    public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder().character(LegacyComponentSerializer.AMPERSAND_CHAR).hexColors().build();
    /** Legacy serializer using the {@code §} (section) formatting character. */
    public static final LegacyComponentSerializer SECTION_SERIALIZER = LegacyComponentSerializer.builder().character(LegacyComponentSerializer.SECTION_CHAR).hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    public LegacyInterpreter(@NotNull StringValidator stringValidator) {
        super(stringValidator);
    }

    @Override
    public @NotNull Component getComponent(@NotNull Language language, @NotNull String path) {
        return SERIALIZER.deserialize(getString(language, path));
    }

    @Override
    public @NotNull Component getComponent(@NotNull Language language, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return SERIALIZER.deserialize(getString(language, path, textReplacements));
    }

    @Override
    public @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable Player player, @NotNull String path) {
        return SERIALIZER.deserialize(getPlaceholderString(language, player, path));
    }

    @Override
    public @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable Player player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return SERIALIZER.deserialize(getPlaceholderString(language, player, path, textReplacements));
    }

    @Override
    public @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path) {
        return SERIALIZER.deserialize(getPlaceholderString(language, player, path));
    }

    @Override
    public @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return SERIALIZER.deserialize(getPlaceholderString(language, player, path, textReplacements));
    }

    @Override
    public @NotNull List<@NotNull Component> getComponentList(@NotNull Language language, @NotNull String path) {
        return getStringList(language, path).stream()
                .map(SERIALIZER::deserialize)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getComponentList(@NotNull Language language, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return getStringList(language, path, textReplacements).stream()
                .map(SERIALIZER::deserialize)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable Player player, @NotNull String path) {
        return getPlaceholderStringList(language, player, path).stream()
                .map(SERIALIZER::deserialize)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable Player player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(SERIALIZER::deserialize)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path) {
        return getPlaceholderStringList(language, player, path).stream()
                .map(SERIALIZER::deserialize)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path, @NotNull TextReplacement... textReplacements) {
        return getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(SERIALIZER::deserialize)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public @NotNull String toString(@NotNull Component component) {
        return SERIALIZER.serialize(component);
    }

    @Override
    public @NotNull Component fromString(@NotNull String string) {
        return SERIALIZER.deserialize(string);
    }
}
