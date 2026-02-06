package me.wyne.wutils.i18n.language.component;

import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.interpretation.StringInterpreter;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PlaceholderLocalizedString extends LocalizedString implements PlaceholderLocalized<String, StringInterpreter> {

    @Nullable
    private final OfflinePlayer player;

    public PlaceholderLocalizedString(StringInterpreter interpreter, Language language, String path, String string, @Nullable OfflinePlayer player) {
        super(interpreter, language, path, string);
        this.player = player;
    }

    @Override
    public PlaceholderLocalizedString replace(TextReplacement... textReplacements) {
        return new PlaceholderLocalizedString(getInterpreter(), getLanguage(), getPath(), I18n.applyTextReplacements(get(), textReplacements), player);
    }

    @Override
    public @Nullable OfflinePlayer getOfflinePlayer() {
        return player;
    }

    @Override
    public @Nullable Player getPlayer() {
        return player != null ? player.getPlayer() : null;
    }

}
