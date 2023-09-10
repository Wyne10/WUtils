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

    public I18n(JavaPlugin plugin)
    {
        setLangPath(getConfigLangPath(plugin));
    }

    public File getConfigLangPath(JavaPlugin plugin)
    {
        return new File(plugin.getDataFolder(), "lang/" + plugin.getConfig().getString("lang"));
    }

    public void setLangPath(File langPath)
    {
        langFile = YamlConfiguration.loadConfiguration(langPath);
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
