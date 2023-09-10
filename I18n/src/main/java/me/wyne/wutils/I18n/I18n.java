package me.wyne.wutils.I18n;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class I18n {

    public static I18n global = new I18n();

    private FileConfiguration langFile;

    public I18n() {}

    public I18n(File langPath)
    {
        setLangPath(langPath);
    }

    public I18n(JavaPlugin plugin, FileConfiguration config)
    {
        setLangPath(getConfigLangPath(plugin, config));
    }

    public File getConfigLangPath(JavaPlugin plugin, FileConfiguration config)
    {
        return new File(plugin.getDataFolder(), "lang/" + config.getString("lang"));
    }

    public void setLangPath(File langPath)
    {
        langFile = new YamlConfiguration();
        YamlConfiguration.loadConfiguration(langPath);
    }

    public String getLocalizedString(String path)
    {
        return langFile.getString(path);
    }

    public List<String> getLocalizedStringList(String path)
    {
        return langFile.getStringList(path);
    }
}
