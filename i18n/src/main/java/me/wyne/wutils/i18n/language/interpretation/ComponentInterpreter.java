package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ComponentInterpreter extends Interpreter {
    Component getComponent(Language language, String path);

    //Component getComponent(ILanguage language, String path, TagResolver... tagResolvers);

    Component getComponent(Language language, String path, TextReplacement... textReplacements);

    Component getPlaceholderComponent(Language language, @Nullable Player player, String path);

    //Component getPlaceholderComponent(ILanguage language, @Nullable Player player, String path, TagResolver... tagResolvers);

    Component getPlaceholderComponent(Language language, @Nullable Player player, String path, TextReplacement... textReplacements);

    Component getPlaceholderComponent(Language language, @Nullable OfflinePlayer player, String path);

    //Component getPlaceholderComponent(ILanguage language, @Nullable OfflinePlayer player, String path, TagResolver... tagResolvers);

    Component getPlaceholderComponent(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements);

    List<Component> getComponentList(Language language, String path);

    //List<Component> getComponentList(ILanguage language, String path, TagResolver... tagResolvers);

    List<Component> getComponentList(Language language, String path, TextReplacement... textReplacements);

    List<Component> getPlaceholderComponentList(Language language, @Nullable Player player, String path);

    //List<Component> getPlaceholderComponentList(ILanguage language, @Nullable Player player, String path, TagResolver... tagResolvers);

    List<Component> getPlaceholderComponentList(Language language, @Nullable Player player, String path, TextReplacement... textReplacements);

    List<Component> getPlaceholderComponentList(Language language, @Nullable OfflinePlayer player, String path);

    //List<Component> getPlaceholderComponentList(ILanguage language, @Nullable OfflinePlayer player, String path, TagResolver... tagResolvers);

    List<Component> getPlaceholderComponentList(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements);

    String toString(Component component);
}
