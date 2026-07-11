package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import me.wyne.wutils.i18n.language.validation.StringValidator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class LegacyInterpreter extends BaseInterpreter implements ComponentInterpreter {

    public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder().character(LegacyComponentSerializer.AMPERSAND_CHAR).hexColors().build();
    public static final LegacyComponentSerializer SECTION_SERIALIZER = LegacyComponentSerializer.builder().character(LegacyComponentSerializer.SECTION_CHAR).hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    public LegacyInterpreter(StringValidator stringValidator) {
        super(stringValidator);
    }

    @Override
    public Component getComponent(Language language, String path) {
        return SERIALIZER.deserialize(getString(language, path));
    }

    @Override
    public Component getComponent(Language language, String path, TextReplacement... textReplacements) {
        return SERIALIZER.deserialize(getString(language, path, textReplacements));
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable Player player, String path) {
        return SERIALIZER.deserialize(getPlaceholderString(language, player, path));
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return SERIALIZER.deserialize(getPlaceholderString(language, player, path, textReplacements));
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable OfflinePlayer player, String path) {
        return SERIALIZER.deserialize(getPlaceholderString(language, player, path));
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return SERIALIZER.deserialize(getPlaceholderString(language, player, path, textReplacements));
    }

    @Override
    public List<Component> getComponentList(Language language, String path) {
        return getStringList(language, path).stream()
                .map(SERIALIZER::deserialize)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<Component> getComponentList(Language language, String path, TextReplacement... textReplacements) {
        return getStringList(language, path, textReplacements).stream()
                .map(SERIALIZER::deserialize)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable Player player, String path) {
        return getPlaceholderStringList(language, player, path).stream()
                .map(SERIALIZER::deserialize)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(SERIALIZER::deserialize)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable OfflinePlayer player, String path) {
        return getPlaceholderStringList(language, player, path).stream()
                .map(SERIALIZER::deserialize)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(SERIALIZER::deserialize)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public String toString(Component component) {
        return SERIALIZER.serialize(component);
    }

    @Override
    public Component fromString(String string) {
        return SERIALIZER.deserialize(string);
    }
}
