package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.PlaceholderAPIWrapper;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import me.wyne.wutils.i18n.language.validation.StringValidator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class LegacyInterpreter extends BaseInterpreter implements ComponentInterpreter {

    public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder().character(LegacyComponentSerializer.AMPERSAND_CHAR).hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    public static final LegacyComponentSerializer SECTION_SERIALIZER = LegacyComponentSerializer.builder().character(LegacyComponentSerializer.SECTION_CHAR).hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    protected final BaseInterpreter baseInterpreter;

    public LegacyInterpreter(StringValidator stringValidator) {
        super(stringValidator);
        this.baseInterpreter = new BaseInterpreter(stringValidator);
    }

    @Override
    public void setStringValidator(StringValidator stringValidator) {
        super.setStringValidator(stringValidator);
        baseInterpreter.setStringValidator(stringValidator);
    }

    @Override
    public String getString(Language language, String path) {
        return ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', baseInterpreter.getString(language, path));
    }

    @Override
    public String getString(Language language, String path, TextReplacement... textReplacements) {
        return I18n.applyTextReplacements(getString(language, path), textReplacements);
    }

    @Override
    public String getPlaceholderString(Language language, @Nullable Player player, String path) {
        return PlaceholderAPIWrapper.setPlaceholders(player, getString(language, path));
    }

    @Override
    public String getPlaceholderString(Language language, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return PlaceholderAPIWrapper.setPlaceholders(player, getString(language, path, textReplacements));
    }

    @Override
    public String getPlaceholderString(Language language, @Nullable OfflinePlayer player, String path) {
        return PlaceholderAPIWrapper.setPlaceholders(player, getString(language, path));
    }

    @Override
    public String getPlaceholderString(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return PlaceholderAPIWrapper.setPlaceholders(player, getString(language, path, textReplacements));
    }

    @Override
    public List<String> getStringList(Language language, String path) {
        return language.getStrings().getStringList(path).stream()
                .map(s -> getStringValidator().validateString(language.getStringMap(), s))
                .map(s -> ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', s))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<String> getStringList(Language language, String path, TextReplacement... textReplacements) {
        return language.getStrings().getStringList(path).stream()
                .map(s -> getStringValidator().validateString(language.getStringMap(), s))
                .map(s -> I18n.applyTextReplacements(s, textReplacements))
                .map(s -> ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', s))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<String> getPlaceholderStringList(Language language, @Nullable Player player, String path) {
        return getStringList(language, path).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<String> getPlaceholderStringList(Language language, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return getStringList(language, path, textReplacements).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<String> getPlaceholderStringList(Language language, @Nullable OfflinePlayer player, String path) {
        return getStringList(language, path).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<String> getPlaceholderStringList(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return getStringList(language, path, textReplacements).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Component getComponent(Language language, String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(SERIALIZER.deserialize(baseInterpreter.getString(language, path)));
    }

    @Override
    public Component getComponent(Language language, String path, TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(SERIALIZER.deserialize(baseInterpreter.getString(language, path, textReplacements)));
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable Player player, String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(SERIALIZER.deserialize(baseInterpreter.getPlaceholderString(language, player, path)));
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(SERIALIZER.deserialize(baseInterpreter.getPlaceholderString(language, player, path, textReplacements)));
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable OfflinePlayer player, String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(SERIALIZER.deserialize(baseInterpreter.getPlaceholderString(language, player, path)));
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(SERIALIZER.deserialize(baseInterpreter.getPlaceholderString(language, player, path, textReplacements)));
    }

    @Override
    public List<Component> getComponentList(Language language, String path) {
        return baseInterpreter.getStringList(language, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(SERIALIZER.deserialize(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<Component> getComponentList(Language language, String path, TextReplacement... textReplacements) {
        return baseInterpreter.getStringList(language, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(SERIALIZER.deserialize(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable Player player, String path) {
        return baseInterpreter.getPlaceholderStringList(language, player, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(SERIALIZER.deserialize(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return baseInterpreter.getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(SERIALIZER.deserialize(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable OfflinePlayer player, String path) {
        return baseInterpreter.getPlaceholderStringList(language, player, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(SERIALIZER.deserialize(s)))
                .map(Component::asComponent)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return baseInterpreter.getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(SERIALIZER.deserialize(s)))
                .map(Component::asComponent)
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
