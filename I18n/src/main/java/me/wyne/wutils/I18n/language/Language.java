package me.wyne.wutils.i18n.language;

import me.clip.placeholderapi.PlaceholderAPI;
import me.wyne.wutils.i18n.language.validation.StringValidator;
import me.wyne.wutils.log.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.vyarus.yaml.updater.YamlUpdater;
import ru.vyarus.yaml.updater.report.UpdateReport;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class Language {

    private final String languageCode;
    private final File languageFile;
    private final FileConfiguration strings;
    private StringValidator stringValidator;

    public Language(File languageFile, StringValidator stringValidator)
    {
        this.languageCode = FilenameUtils.removeExtension(languageFile.getName());
        this.languageFile = languageFile;
        this.strings = YamlConfiguration.loadConfiguration(languageFile);
        this.stringValidator = stringValidator;
    }

    public Language(Language defaultLanguage, File languageFile, StringValidator stringValidator)
    {
        this(languageFile, stringValidator);
        mergeDefaultStrings(defaultLanguage, languageFile);
    }

    private void mergeDefaultStrings(Language defaultLanguage, File languageFile)
    {
        if (defaultLanguage.languageFile.length() == 0)
            return;
        Log.global.info("Searching for missing strings in " + languageFile.getName());
        UpdateReport report = YamlUpdater.create(languageFile, defaultLanguage.languageFile)
                .backup(false)
                .update();
        if (report.isConfigChanged())
            Log.global.info("Merged missing strings to " + languageFile.getName());
        else
            Log.global.info(languageFile.getName() + " is up to date");
    }

    public void setStringValidator(StringValidator stringValidator)
    {
        this.stringValidator = stringValidator;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getString(String path)
    {
        return stringValidator.validateString(languageCode, strings, path);
    }

    public String getPlaceholderString(@Nullable Player player, String path)
    {
        return PlaceholderAPI.setPlaceholders(player, getString(path));
    }

    public String getPlaceholderString(@Nullable OfflinePlayer player, String path)
    {
        return PlaceholderAPI.setPlaceholders(player, getString(path));
    }

    public Component getComponent(String path)
    {
        return MiniMessage.miniMessage().deserialize(getString(path));
    }

    public Component getComponent(String path, TagResolver ...tagResolvers)
    {
        return MiniMessage.miniMessage().deserialize(getString(path), tagResolvers);
    }

    public Component getPlaceholderComponent(@Nullable Player player, String path)
    {
        return MiniMessage.miniMessage().deserialize(getPlaceholderString(player, path));
    }

    public Component getPlaceholderComponent(@Nullable Player player, String path, TagResolver ...tagResolvers)
    {
        return MiniMessage.miniMessage().deserialize(getPlaceholderString(player, path), tagResolvers);
    }

    public Component getPlaceholderComponent(@Nullable OfflinePlayer player, String path)
    {
        return MiniMessage.miniMessage().deserialize(getPlaceholderString(player, path));
    }

    public Component getPlaceholderComponent(@Nullable OfflinePlayer player, String path, TagResolver ...tagResolvers)
    {
        return MiniMessage.miniMessage().deserialize(getPlaceholderString(player, path), tagResolvers);
    }

    public List<String> getStringList(String path)
    {
        return strings.getStringList(path);
    }

    public List<String> getPlaceholderStringList(@Nullable Player player, String path)
    {
        return getStringList(path).stream()
                .map(s -> PlaceholderAPI.setPlaceholders(player, s))
                .collect(Collectors.toList());
    }

    public List<String> getPlaceholderStringList(@Nullable OfflinePlayer player, String path)
    {
        return getStringList(path).stream()
                .map(s -> PlaceholderAPI.setPlaceholders(player, s))
                .collect(Collectors.toList());
    }

    public List<Component> getComponentList(String path)
    {
        return getStringList(path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s))
                .collect(Collectors.toList());
    }

    public List<Component> getComponentList(String path, TagResolver ...tagResolvers)
    {
        return getStringList(path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s, tagResolvers))
                .collect(Collectors.toList());
    }

    public List<Component> getPlaceholderComponentList(@Nullable Player player, String path)
    {
        return getPlaceholderStringList(player, path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s))
                .collect(Collectors.toList());
    }

    public List<Component> getPlaceholderComponentList(@Nullable Player player, String path, TagResolver ...tagResolvers)
    {
        return getPlaceholderStringList(player, path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s, tagResolvers))
                .collect(Collectors.toList());
    }

    public List<Component> getPlaceholderComponentList(@Nullable OfflinePlayer player, String path)
    {
        return getPlaceholderStringList(player, path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s))
                .collect(Collectors.toList());
    }

    public List<Component> getPlaceholderComponentList(@Nullable OfflinePlayer player, String path, TagResolver ...tagResolvers)
    {
        return getPlaceholderStringList(player, path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s, tagResolvers))
                .collect(Collectors.toList());
    }

}
