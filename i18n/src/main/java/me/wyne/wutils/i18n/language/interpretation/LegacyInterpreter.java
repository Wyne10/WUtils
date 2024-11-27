package me.wyne.wutils.i18n.language.interpretation;

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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LegacyInterpreter extends BaseInterpreter implements ComponentInterpreter {

    public LegacyInterpreter(StringValidator stringValidator) {
        super(stringValidator);
    }

    @Override
    public String getString(Language language, String path) {
        return ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', super.getString(language, path));
    }

    @Override
    public String getString(Language language, String path, TextReplacement... textReplacements) {
        return applyTextReplacements(getString(language, path), textReplacements);
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
                .map(s -> stringValidator.validateString(language.getLanguageCode(), language.getStrings(), s))
                .map(s -> ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', s))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<String> getStringList(Language language, String path, TextReplacement... textReplacements) {
        return language.getStrings().getStringList(path).stream()
                .map(s -> stringValidator.validateString(language.getLanguageCode(), language.getStrings(), s))
                .map(s -> applyTextReplacements(s, textReplacements))
                .map(s -> ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', s))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<String> getPlaceholderStringList(Language language, @Nullable Player player, String path) {
        return getStringList(language, path).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .toList();
    }

    @Override
    public List<String> getPlaceholderStringList(Language language, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return getStringList(language, path, textReplacements).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .toList();
    }

    @Override
    public List<String> getPlaceholderStringList(Language language, @Nullable OfflinePlayer player, String path) {
        return getStringList(language, path).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .toList();
    }

    @Override
    public List<String> getPlaceholderStringList(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return getStringList(language, path, textReplacements).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .toList();
    }

    @Override
    public Component getComponent(Language language, String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(super.getString(language, path)));
    }

    @Override
    public Component getComponent(Language language, String path, TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(super.getString(language, path, textReplacements)));
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable Player player, String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(super.getPlaceholderString(language, player, path)));
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(super.getPlaceholderString(language, player, path, textReplacements)));
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable OfflinePlayer player, String path) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(super.getPlaceholderString(language, player, path)));
    }

    @Override
    public Component getPlaceholderComponent(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(super.getPlaceholderString(language, player, path, textReplacements)));
    }

    @Override
    public List<Component> getComponentList(Language language, String path) {
        return super.getStringList(language, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(s)))
                .map(Component::asComponent)
                .toList();
    }

    @Override
    public List<Component> getComponentList(Language language, String path, TextReplacement... textReplacements) {
        return super.getStringList(language, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(s)))
                .map(Component::asComponent)
                .toList();
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable Player player, String path) {
        return super.getPlaceholderStringList(language, player, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(s)))
                .map(Component::asComponent)
                .toList();
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return super.getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(s)))
                .map(Component::asComponent)
                .toList();
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable OfflinePlayer player, String path) {
        return super.getPlaceholderStringList(language, player, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(s)))
                .map(Component::asComponent)
                .toList();
    }

    @Override
    public List<Component> getPlaceholderComponentList(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return super.getPlaceholderStringList(language, player, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(s)))
                .map(Component::asComponent)
                .toList();
    }

}
