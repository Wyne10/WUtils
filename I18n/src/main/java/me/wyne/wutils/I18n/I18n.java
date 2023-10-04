package me.wyne.wutils.i18n;

import me.clip.placeholderapi.PlaceholderAPI;
import me.wyne.wutils.log.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
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
        if (defaultLang == null)
            Log.global.warn("Default language isn't set");
        if (!defaultLang.contains(path))
            Log.global.warn("String at path '" + path + "' not found (default language)");

        return defaultLang.getString(path);
    }

    public String getLocalizedString(@Nullable Player player, String path)
    {
        if (player == null)
            return getLocalizedString(path);
        if (player.locale().getLanguage().isEmpty())
            return getLocalizedString(path);
        if (!lang.containsKey(player.locale().getLanguage()))
            return getLocalizedString(path);

        if (!lang.get(player.locale().getLanguage()).contains(path))
            Log.global.warn("String at path '" + path + "' not found (" + player.locale().getLanguage() + " language)");

        return lang.get(player.locale().getLanguage()).getString(path);
    }

    public String getLocalizedString(CommandSender commandSender, String path)
    {
        Player player = null;

        if (commandSender instanceof Player)
            player = (Player)commandSender;

        return getLocalizedString(player, path);
    }
    public String getLocalizedPlaceholderString(Player player, String path)
    {
        return PlaceholderAPI.setPlaceholders(player, getLocalizedString(player, path));
    }

    public String getLocalizedPlaceholderString(CommandSender commandSender, String path)
    {
        Player player = null;

        if (commandSender instanceof Player)
            player = (Player)commandSender;

        if (player != null)
            return PlaceholderAPI.setPlaceholders(player, getLocalizedString(player, path));
        else
            return getLocalizedString(path);
    }

    public String getLocalizedPlaceholderString(OfflinePlayer placeholderPlayer, @Nullable Player langPlayer, String path)
    {
        return PlaceholderAPI.setPlaceholders(placeholderPlayer, getLocalizedString(langPlayer, path));
    }

    public String getLocalizedPlaceholderString(OfflinePlayer placeholderPlayer, CommandSender langCommandSender, String path)
    {
        return PlaceholderAPI.setPlaceholders(placeholderPlayer, getLocalizedString(langCommandSender, path));
    }

    public Component getLocalizedComponent(String path)
    {
        return MiniMessage.miniMessage().deserialize(getLocalizedString(path));
    }

    public Component getLocalizedComponent(Player player, String path)
    {
        return MiniMessage.miniMessage().deserialize(getLocalizedString(player, path));
    }

    public Component getLocalizedComponent(CommandSender commandSender, String path)
    {
        return MiniMessage.miniMessage().deserialize(getLocalizedString(commandSender, path));
    }

    public Component getLocalizedPlaceholderComponent(Player player, String path)
    {
        return MiniMessage.miniMessage().deserialize(getLocalizedPlaceholderString(player, path));
    }

    public Component getLocalizedPlaceholderComponent(CommandSender commandSender, String path)
    {
        return MiniMessage.miniMessage().deserialize(getLocalizedPlaceholderString(commandSender, path));
    }

    public Component getLocalizedPlaceholderComponent(OfflinePlayer placeholderPlayer, @Nullable Player langPlayer, String path)
    {
        return MiniMessage.miniMessage().deserialize(getLocalizedPlaceholderString(placeholderPlayer, langPlayer, path));
    }

    public Component getLocalizedPlaceholderComponent(OfflinePlayer placeholderPlayer, CommandSender langCommandSender, String path)
    {
        return MiniMessage.miniMessage().deserialize(getLocalizedPlaceholderString(placeholderPlayer, langCommandSender, path));
    }

    public List<String> getLocalizedStringList(String path)
    {
        if (defaultLang == null)
            Log.global.warn("Default language isn't set");
        if (!defaultLang.contains(path))
            Log.global.warn("String list at path '" + path + "' not found (default language)");

        return defaultLang.getStringList(path);
    }

    public List<String> getLocalizedStringList(@Nullable Player player, String path)
    {
        if (player == null)
            return getLocalizedStringList(path);
        if (player.locale().getLanguage().isEmpty())
            return getLocalizedStringList(path);
        if (!lang.containsKey(player.locale().getLanguage()))
            return getLocalizedStringList(path);

        if (!lang.get(player.locale().getLanguage()).contains(path))
            Log.global.warn("String list at path '" + path + "' not found (" + player.locale().getLanguage() + " language)");

        return lang.get(player.locale().getLanguage()).getStringList(path);
    }

    public List<String> getLocalizedStringList(CommandSender commandSender, String path)
    {
        Player player = null;

        if (commandSender instanceof Player)
            player = (Player)commandSender;

        return getLocalizedStringList(player, path);
    }

    public List<String> getLocalizedPlaceholderStringList(Player player, String path)
    {
        List<String> placeholderStringList = new ArrayList<>();

        getLocalizedStringList(player, path).forEach((string) -> placeholderStringList.add(PlaceholderAPI.setPlaceholders(player, string)));

        return placeholderStringList;
    }

    public List<String> getLocalizedPlaceholderStringList(CommandSender commandSender, String path)
    {
        Player player;

        if (commandSender instanceof Player)
            player = (Player)commandSender;
        else {
            player = null;
        }

        List<String> placeholderStringList = new ArrayList<>();

        if (player != null)
            getLocalizedStringList(player, path).forEach((string) -> placeholderStringList.add(PlaceholderAPI.setPlaceholders(player, string)));
        else
            return getLocalizedStringList(path);

        return placeholderStringList;
    }

    public List<String> getLocalizedPlaceholderStringList(OfflinePlayer placeholderPlayer, @Nullable Player langPlayer, String path)
    {
        List<String> placeholderStringList = new ArrayList<>();

        getLocalizedStringList(langPlayer, path).forEach((string) -> placeholderStringList.add(PlaceholderAPI.setPlaceholders(placeholderPlayer, string)));

        return placeholderStringList;
    }

    public List<String> getLocalizedPlaceholderStringList(OfflinePlayer placeholderPlayer, CommandSender langCommandSender, String path)
    {
        List<String> placeholderStringList = new ArrayList<>();

        getLocalizedStringList(langCommandSender, path).forEach((string) -> placeholderStringList.add(PlaceholderAPI.setPlaceholders(placeholderPlayer, string)));

        return placeholderStringList;
    }

    public List<Component> getLocalizedComponentList(String path)
    {
        List<Component> componentList = new ArrayList<>();

        getLocalizedStringList(path).forEach((string) -> componentList.add(MiniMessage.miniMessage().deserialize(string)));

        return componentList;
    }

    public List<Component> getLocalizedComponentList(Player player, String path)
    {
        List<Component> componentList = new ArrayList<>();

        getLocalizedStringList(player, path).forEach((string) -> componentList.add(MiniMessage.miniMessage().deserialize(string)));

        return componentList;
    }

    public List<Component> getLocalizedComponentList(CommandSender commandSender, String path)
    {
        List<Component> componentList = new ArrayList<>();

        getLocalizedStringList(commandSender, path).forEach((string) -> componentList.add(MiniMessage.miniMessage().deserialize(string)));

        return componentList;
    }

    public List<Component> getLocalizedPlaceholderComponentList(Player player, String path)
    {
        List<Component> componentList = new ArrayList<>();

        getLocalizedPlaceholderStringList(player, path).forEach((string) -> componentList.add(MiniMessage.miniMessage().deserialize(string)));

        return componentList;
    }

    public List<Component> getLocalizedPlaceholderComponentList(CommandSender commandSender, String path)
    {
        List<Component> componentList = new ArrayList<>();

        getLocalizedPlaceholderStringList(commandSender, path).forEach((string) -> componentList.add(MiniMessage.miniMessage().deserialize(string)));

        return componentList;
    }

    public List<Component> getLocalizedPlaceholderComponentList(OfflinePlayer placeholderPlayer, @Nullable Player langPlayer, String path)
    {
        List<Component> componentList = new ArrayList<>();

        getLocalizedPlaceholderStringList(placeholderPlayer, langPlayer, path).forEach((string) -> componentList.add(MiniMessage.miniMessage().deserialize(string)));

        return componentList;
    }

    public List<Component> getLocalizedPlaceholderComponentList(OfflinePlayer placeholderPlayer, CommandSender langCommandSender, String path)
    {
        List<Component> componentList = new ArrayList<>();

        getLocalizedPlaceholderStringList(placeholderPlayer, langCommandSender, path).forEach((string) -> componentList.add(MiniMessage.miniMessage().deserialize(string)));

        return componentList;
    }
}
