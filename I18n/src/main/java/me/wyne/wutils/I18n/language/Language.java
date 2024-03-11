package me.wyne.wutils.i18n.language;

import me.clip.placeholderapi.PlaceholderAPI;
import me.wyne.wutils.i18n.language.validation.StringValidator;
import me.wyne.wutils.log.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Language {

    private final String languageCode;
    private final FileConfiguration strings;
    private StringValidator stringValidator;

    public Language(File stringsFile, StringValidator stringValidator)
    {
        this.languageCode = FilenameUtils.removeExtension(stringsFile.getName());
        this.strings = YamlConfiguration.loadConfiguration(stringsFile);
        this.stringValidator = stringValidator;
    }

    public Language(Language defaultLanguage, File stringsFile, StringValidator stringValidator)
    {
        this(stringsFile, stringValidator);
        mergeDefaultStrings(defaultLanguage, stringsFile);
    }

    private void mergeDefaultStrings(Language defaultLanguage, File stringsFile)
    {
        Log.global.info("Searching for missing strings in " + stringsFile.getName());
        boolean result = false;

        for (String key : defaultLanguage.strings.getKeys(false))
        {
            if (!strings.contains(key))
            {
                if (defaultLanguage.strings.isString(key))
                    strings.set(key, defaultLanguage.strings.getString(key));
                else if (defaultLanguage.strings.isList(key))
                    strings.set(key, defaultLanguage.strings.getStringList(key));
                result = true;
            }
        }

        if (result)
        {
            try {
                strings.save(stringsFile);
            } catch (IOException e) {
                Log.global.exception("An exception occurred while trying to save " + stringsFile.getName(), e);
            }
            Log.global.info("Merged missing strings to " + stringsFile.getName());
        }

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

    public String getPlaceholderString(Player player, String path)
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

    public Component getPlaceholderComponent(Player player, String path)
    {
        return MiniMessage.miniMessage().deserialize(getPlaceholderString(player, path));
    }

    public Component getPlaceholderComponent(Player player, String path, TagResolver ...tagResolvers)
    {
        return MiniMessage.miniMessage().deserialize(getPlaceholderString(player, path), tagResolvers);
    }


    public List<String> getStringList(String path)
    {
        return strings.getStringList(path);
    }

    public List<String> getPlaceholderStringList(Player player, String path)
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

    public List<Component> getPlaceholderComponentList(Player player, String path)
    {
        return getPlaceholderStringList(player, path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s))
                .collect(Collectors.toList());
    }

    public List<Component> getPlaceholderComponentList(Player player, String path, TagResolver ...tagResolvers)
    {
        return getPlaceholderStringList(player, path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s, tagResolvers))
                .collect(Collectors.toList());
    }

}
