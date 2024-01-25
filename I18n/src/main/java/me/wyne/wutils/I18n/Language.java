package me.wyne.wutils.i18n;

import me.clip.placeholderapi.PlaceholderAPI;
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

    private final FileConfiguration strings;

    public Language(File stringsFile)
    {
        strings = YamlConfiguration.loadConfiguration(stringsFile);
        Log.global.info("Loaded " + FilenameUtils.removeExtension(strings.getName()) + " language");
    }

    public Language(Language defaultLanguage, File stringsFile)
    {
        this(stringsFile);
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
                strings.set(key, defaultLanguage.strings.getString(key));
                result = true;
            }
        }

        if (result)
        {
            try {
                strings.save(stringsFile);
            } catch (IOException e) {
                Log.global.error("An exception occurred while trying to save " + stringsFile.getName());
            }
            Log.global.info("Merged missing strings to " + stringsFile.getName());
        }

    }

    public String getString(String path)
    {
        return strings.getString(path);
    }

    public String getPlaceholderString(Player player, String path)
    {
        return PlaceholderAPI.setPlaceholders(player, getString(path));
    }

    public Component getComponent(String path)
    {
        return MiniMessage.miniMessage().deserialize(getString(path));
    }

    public Component getComponent(String path, TagResolver ...tagResolver)
    {
        return MiniMessage.miniMessage().deserialize(getString(path), tagResolver);
    }

    public Component getPlaceholderComponent(Player player, String path)
    {
        return MiniMessage.miniMessage().deserialize(getPlaceholderString(player, path));
    }

    public Component getPlaceholderComponent(Player player, String path, TagResolver ...tagResolver)
    {
        return MiniMessage.miniMessage().deserialize(getPlaceholderString(player, path), tagResolver);
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

    public List<Component> getComponentList(String path, TagResolver ...tagResolver)
    {
        return getStringList(path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s, tagResolver))
                .collect(Collectors.toList());
    }

    public List<Component> getPlaceholderComponentList(Player player, String path)
    {
        return getPlaceholderStringList(player, path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s))
                .collect(Collectors.toList());
    }

    public List<Component> getPlaceholderComponentList(Player player, String path, TagResolver ...tagResolver)
    {
        return getPlaceholderStringList(player, path).stream()
                .map(s -> MiniMessage.miniMessage().deserialize(s, tagResolver))
                .collect(Collectors.toList());
    }

}
