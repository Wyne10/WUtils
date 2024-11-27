package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface StringInterpreter extends Interpreter {
    String getString(Language language, String path);

    String getString(Language language, String path, TextReplacement... textReplacements);

    String getPlaceholderString(Language language, @Nullable Player player, String path);

    String getPlaceholderString(Language language, @Nullable Player player, String path, TextReplacement... textReplacements);

    String getPlaceholderString(Language language, @Nullable OfflinePlayer player, String path);

    String getPlaceholderString(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements);

    List<String> getStringList(Language language, String path);

    List<String> getStringList(Language language, String path, TextReplacement... textReplacements);

    List<String> getPlaceholderStringList(Language language, @Nullable Player player, String path);

    List<String> getPlaceholderStringList(Language language, @Nullable Player player, String path, TextReplacement... textReplacements);

    List<String> getPlaceholderStringList(Language language, @Nullable OfflinePlayer player, String path);

    List<String> getPlaceholderStringList(Language language, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements);


}
