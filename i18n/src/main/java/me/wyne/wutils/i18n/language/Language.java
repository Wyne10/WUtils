package me.wyne.wutils.i18n.language;

import me.wyne.wutils.i18n.LogWrapper;
import me.wyne.wutils.i18n.PlaceholderAPIWrapper;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import me.wyne.wutils.i18n.language.validation.StringValidator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.vyarus.yaml.updater.YamlUpdater;
import ru.vyarus.yaml.updater.report.UpdateReport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Language {

    private final LogWrapper log;

    private final String languageCode;
    private final File languageFile;
    private final FileConfiguration strings;
    private StringValidator stringValidator;

    public Language(File languageFile, StringValidator stringValidator, LogWrapper log)
    {
        this.log = log;
        this.languageCode = FilenameUtils.removeExtension(languageFile.getName());
        this.languageFile = languageFile;
        this.strings = YamlConfiguration.loadConfiguration(languageFile);
        this.stringValidator = stringValidator;
    }

    public Language(Language defaultLanguage, File languageFile, StringValidator stringValidator, LogWrapper log)
    {
        this.log = log;
        mergeDefaultStrings(defaultLanguage, languageFile);
        this.languageCode = FilenameUtils.removeExtension(languageFile.getName());
        this.languageFile = languageFile;
        this.strings = YamlConfiguration.loadConfiguration(languageFile);
        this.stringValidator = stringValidator;
    }

    private void mergeDefaultStrings(Language defaultLanguage, File languageFile)
    {
        if (defaultLanguage.languageFile.length() == 0)
            return;
        log.info("Searching for missing strings in " + languageFile.getName());
        UpdateReport report = YamlUpdater.create(languageFile, defaultLanguage.languageFile)
                .backup(false)
                .update();
        if (report.isConfigChanged())
            log.info("Merged missing strings to " + languageFile.getName());
        else
            log.info(languageFile.getName() + " is up to date");
    }

    public void setStringValidator(StringValidator stringValidator)
    {
        this.stringValidator = stringValidator;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    private String applyTextReplacements(String string, TextReplacement ...textReplacements)
    {
        String result = string;
        for (TextReplacement replacement : textReplacements)
            result = replacement.replace(result);
        return result;
    }

    public boolean contains(String path)
    {
        return strings.contains(path);
    }

    public String getString(String path)
    {
        return stringValidator.validateString(languageCode, strings, path);
    }

    public String getString(String path, TextReplacement... textReplacements)
    {
        return applyTextReplacements(getString(path), textReplacements);
    }
    
    public String getPlaceholderString(@Nullable Player player, String path)
    {
        return PlaceholderAPIWrapper.setPlaceholders(player, getString(path));
    }
    
    public String getPlaceholderString(@Nullable Player player, String path, TextReplacement... textReplacements)
    {
        return PlaceholderAPIWrapper.setPlaceholders(player, getString(path, textReplacements));
    }

    public String getPlaceholderString(@Nullable OfflinePlayer player, String path)
    {
        return PlaceholderAPIWrapper.setPlaceholders(player, getString(path));
    }

    public String getPlaceholderString(@Nullable OfflinePlayer player, String path, TextReplacement... textReplacements)
    {
        return PlaceholderAPIWrapper.setPlaceholders(player, getString(path, textReplacements));
    }

    public String getLegacyString(String path)
    {
        return ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', getString(path));
    }
    
    public String getLegacyString(String path, TextReplacement ...textReplacements)
    {
        return applyTextReplacements(getLegacyString(path), textReplacements);
    }

    public String getLegacyPlaceholderString(@Nullable Player player, String path)
    {
        return PlaceholderAPIWrapper.setPlaceholders(player, getLegacyString(path));
    }

    public String getLegacyPlaceholderString(@Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return PlaceholderAPIWrapper.setPlaceholders(player, getLegacyString(path, textReplacements));
    }

    public String getLegacyPlaceholderString(@Nullable OfflinePlayer player, String path)
    {
        return PlaceholderAPIWrapper.setPlaceholders(player, getLegacyString(path));
    }

    public String getLegacyPlaceholderString(@Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return PlaceholderAPIWrapper.setPlaceholders(player, getLegacyString(path, textReplacements));
    }

    public Component getComponent(String path)
    {
        return MiniMessage.miniMessage().deserialize(getString(path));
    }

    public Component getComponent(String path, TagResolver ...tagResolvers)
    {
        return MiniMessage.miniMessage().deserialize(getString(path), tagResolvers);
    }

    public Component getComponent(String path, TextReplacement ...textReplacements)
    {
        return MiniMessage.miniMessage().deserialize(getString(path, textReplacements));
    }
    
    public Component getPlaceholderComponent(@Nullable Player player, String path)
    {
        return MiniMessage.miniMessage().deserialize(getPlaceholderString(player, path));
    }

    public Component getPlaceholderComponent(@Nullable Player player, String path, TagResolver ...tagResolvers)
    {
        return MiniMessage.miniMessage().deserialize(getPlaceholderString(player, path), tagResolvers);
    }
    
    public Component getPlaceholderComponent(@Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return MiniMessage.miniMessage().deserialize(getPlaceholderString(player, path, textReplacements));
    }

    public Component getPlaceholderComponent(@Nullable OfflinePlayer player, String path)
    {
        return MiniMessage.miniMessage().deserialize(getPlaceholderString(player, path));
    }

    public Component getPlaceholderComponent(@Nullable OfflinePlayer player, String path, TagResolver ...tagResolvers)
    {
        return MiniMessage.miniMessage().deserialize(getPlaceholderString(player, path), tagResolvers);
    }
    
    public Component getPlaceholderComponent(@Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return MiniMessage.miniMessage().deserialize(getPlaceholderString(player, path, textReplacements));
    }

    public Component getLegacyComponent(String path)
    {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(getString(path)));
    }

    public Component getLegacyComponent(String path, TextReplacement ...textReplacements)
    {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(getString(path, textReplacements)));
    }

    public Component getLegacyPlaceholderComponent(@Nullable Player player, String path)
    {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(getPlaceholderString(player, path)));
    }

    public Component getLegacyPlaceholderComponent(@Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(getPlaceholderString(player, path, textReplacements)));
    }

    public Component getLegacyPlaceholderComponent(@Nullable OfflinePlayer player, String path)
    {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(getPlaceholderString(player, path)));
    }

    public Component getLegacyPlaceholderComponent(@Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(getPlaceholderString(player, path, textReplacements)));
    } 

    public List<String> getStringList(String path)
    {
        return strings.getStringList(path).stream()
                .map(s -> stringValidator.validateString(languageCode, strings, s))
                .collect(Collectors.toCollection(ArrayList::new));
    }
    
    public List<String> getStringList(String path, TextReplacement ...textReplacements)
    {
        return strings.getStringList(path).stream()
                .map(s -> stringValidator.validateString(languageCode, strings, s))
                .map(s -> applyTextReplacements(s, textReplacements))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<String> getPlaceholderStringList(@Nullable Player player, String path)
    {
        return getStringList(path).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .toList();
    }
    
    public List<String> getPlaceholderStringList(@Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return getStringList(path, textReplacements).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .toList();
    }

    public List<String> getPlaceholderStringList(@Nullable OfflinePlayer player, String path)
    {
        return getStringList(path).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .toList();
    }

    public List<String> getPlaceholderStringList(@Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return getStringList(path, textReplacements).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .toList();
    }

    public List<String> getLegacyStringList(String path)
    {
        return strings.getStringList(path).stream()
                .map(s -> stringValidator.validateString(languageCode, strings, s))
                .map(s -> ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', s))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<String> getLegacyStringList(String path, TextReplacement ...textReplacements)
    {
        return strings.getStringList(path).stream()
                .map(s -> stringValidator.validateString(languageCode, strings, s))
                .map(s -> applyTextReplacements(s, textReplacements))
                .map(s -> ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', s))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<String> getLegacyPlaceholderStringList(@Nullable Player player, String path)
    {
        return getLegacyStringList(path).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .toList();
    }

    public List<String> getLegacyPlaceholderStringList(@Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return getLegacyStringList(path, textReplacements).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .toList();
    }

    public List<String> getLegacyPlaceholderStringList(@Nullable OfflinePlayer player, String path)
    {
        return getLegacyStringList(path).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .toList();
    }

    public List<String> getLegacyPlaceholderStringList(@Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return getLegacyStringList(path, textReplacements).stream()
                .map(s -> PlaceholderAPIWrapper.setPlaceholders(player, s))
                .toList();
    } 

    public List<Component> getComponentList(String path)
    {
        return getStringList(path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s))
                .toList();
    }

    public List<Component> getComponentList(String path, TagResolver ...tagResolvers)
    {
        return getStringList(path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s, tagResolvers))
                .toList();
    }

    public List<Component> getComponentList(String path, TextReplacement ...textReplacements)
    {
        return getStringList(path, textReplacements).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s))
                .toList();
    }

    public List<Component> getPlaceholderComponentList(@Nullable Player player, String path)
    {
        return getPlaceholderStringList(player, path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s))
                .toList();
    }

    public List<Component> getPlaceholderComponentList(@Nullable Player player, String path, TagResolver ...tagResolvers)
    {
        return getPlaceholderStringList(player, path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s, tagResolvers))
                .toList();
    }

    public List<Component> getPlaceholderComponentList(@Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return getPlaceholderStringList(player, path, textReplacements).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s))
                .toList();
    }

    public List<Component> getPlaceholderComponentList(@Nullable OfflinePlayer player, String path)
    {
        return getPlaceholderStringList(player, path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s))
                .toList();
    }

    public List<Component> getPlaceholderComponentList(@Nullable OfflinePlayer player, String path, TagResolver ...tagResolvers)
    {
        return getPlaceholderStringList(player, path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s, tagResolvers))
                .toList();
    }

    public List<Component> getPlaceholderComponentList(@Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return getPlaceholderStringList(player, path, textReplacements).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s))
                .toList();
    }

    public List<Component> getLegacyComponentList(String path)
    {
        return getStringList(path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(s)))
                .map(Component::asComponent)
                .toList();
    }

    public List<Component> getLegacyComponentList(String path, TextReplacement ...textReplacements)
    {
        return getStringList(path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(s)))
                .map(Component::asComponent)
                .toList();
    }

    public List<Component> getLegacyPlaceholderComponentList(@Nullable Player player, String path)
    {
        return getPlaceholderStringList(player, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(s)))
                .map(Component::asComponent)
                .toList();
    }

    public List<Component> getLegacyPlaceholderComponentList(@Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return getPlaceholderStringList(player, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(s)))
                .map(Component::asComponent)
                .toList();
    }

    public List<Component> getLegacyPlaceholderComponentList(@Nullable OfflinePlayer player, String path)
    {
        return getPlaceholderStringList(player, path).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(s)))
                .map(Component::asComponent)
                .toList();
    }

    public List<Component> getLegacyPlaceholderComponentList(@Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return getPlaceholderStringList(player, path, textReplacements).stream()
                .map(s -> Component.empty().decoration(TextDecoration.ITALIC, false).append(LegacyComponentSerializer.legacyAmpersand().deserialize(s)))
                .map(Component::asComponent)
                .toList();
    }

}
