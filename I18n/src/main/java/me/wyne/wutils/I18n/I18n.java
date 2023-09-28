package me.wyne.wutils.i18n;

import me.wyne.wutils.log.Log;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class I18n {
    public static I18n global = new I18n();

    private final Map<String, FileConfiguration> lang = new HashMap<>();
    private FileConfiguration defaultLang;

    public I18n() {}

    public I18n(File langPath)
    {
        setDefaultLangPath(langPath);
    }

    public I18n(JavaPlugin plugin)
    {
        setDefaultLangPath(getDefaultLangPath(plugin));
        loadLang(plugin);
    }

    private void loadLang(JavaPlugin plugin)
    {
        for (File file : new File(plugin.getDataFolder(), "lang").listFiles())
        {
            lang.put(FilenameUtils.removeExtension(file.getName()), YamlConfiguration.loadConfiguration(file));
            Log.global.info("Loaded " + FilenameUtils.removeExtension(file.getName()) + " language");
        }
    }

    public File getDefaultLangPath(JavaPlugin plugin)
    {
        if (!plugin.getConfig().contains("lang"))
        {
            Log.global.warn("Plugin config doesn't contain default language path");
            return null;
        }

        return new File(plugin.getDataFolder(), "lang/" + plugin.getConfig().getString("lang"));
    }

    public void setDefaultLangPath(File langPath)
    {
        if (langPath == null || !langPath.isFile())
        {
            Log.global.warn("Couldn't set default language to " + langPath.getName());
            return;
        }

        defaultLang = YamlConfiguration.loadConfiguration(langPath);
        Log.global.info("Default language is set to " + langPath.getName());
    }

    public String getLocalizedString(String path)
    {
        if (!defaultLang.contains(path))
            Log.global.warn("String at path '" + path + "' not found (Default language)");

        return defaultLang.getString(path);
    }

    public String getLocalizedString(Player player, String path)
    {
        if (player.locale().getLanguage().isEmpty())
            return getLocalizedString(path);
        if (!lang.containsKey(player.locale().getLanguage()))
            return getLocalizedString(path);

        if (!lang.get(player.locale().getLanguage()).contains(path))
            Log.global.warn("String at path '" + path + "' not found (" + player.locale().getLanguage() + " language)");

        return lang.get(player.locale().getLanguage()).getString(path);
    }

    public List<String> getLocalizedStringList(String path)
    {
        if (!defaultLang.contains(path))
            Log.global.warn("String list at path '" + path + "' not found (Default language)");

        return defaultLang.getStringList(path);
    }

    public List<String> getLocalizedStringList(Player player, String path)
    {
        if (player.locale().getLanguage().isEmpty())
            return getLocalizedStringList(path);
        if (!lang.containsKey(player.locale().getLanguage()))
            return getLocalizedStringList(path);

        if (!lang.get(player.locale().getLanguage()).contains(path))
            Log.global.warn("String list at path '" + path + "' not found (" + player.locale().getLanguage() + " language)");

        return lang.get(player.locale().getLanguage()).getStringList(path);
    }
}
