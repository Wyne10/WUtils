package me.wyne.wutils.i18n.language.replacement;

import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.text.Component;

public interface ComponentReplacement extends Replacement<Component> {
    default TextReplacement as() {
        return string -> I18n.global.component().toString(replace(I18n.global.component().fromString(string)));
    }
}
