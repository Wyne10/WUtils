package me.wyne.wutils.i18n;

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

    public I18n(File langPath) throws IllegalArgumentException
    {
        setDefaultLangPath(langPath);
    }

    public I18n(JavaPlugin plugin) throws IllegalArgumentException, NullPointerException
    {
        setDefaultLangPath(getDefaultLangPath(plugin));
        loadLang(plugin);
    }

    private void loadLang(JavaPlugin plugin) throws IllegalArgumentException
    {
        for (File file : new File(plugin.getDataFolder(), "lang").listFiles())
        {
            lang.put(FilenameUtils.removeExtension(file.getName()), YamlConfiguration.loadConfiguration(file));
        }
    }

    public File getDefaultLangPath(JavaPlugin plugin) throws NullPointerException
    {
        return new File(plugin.getDataFolder(), "lang/" + plugin.getConfig().getString("lang"));
    }

    public void setDefaultLangPath(File langPath) throws IllegalArgumentException
    {
        defaultLang = YamlConfiguration.loadConfiguration(langPath);
    }

    public String getLocalizedString(String path)
    {
        return defaultLang.getString(path);
    }

    public String getLocalizedString(Player player, String path)
    {
        if (player.locale().getLanguage().isEmpty())
            return getLocalizedString(path);
        if (!lang.containsKey(player.locale().getLanguage()))
            return getLocalizedString(path);

        return lang.get(player.locale().getLanguage()).getString(path);
    }

    public List<String> getLocalizedStringList(String path)
    {
        return defaultLang.getStringList(path);
    }

    public List<String> getLocalizedStringList(Player player, String path)
    {
        if (player.locale().getLanguage().isEmpty())
            return getLocalizedStringList(path);
        if (!lang.containsKey(player.locale().getLanguage()))
            return getLocalizedStringList(path);

        return lang.get(player.locale().getLanguage()).getStringList(path);
    }
}
