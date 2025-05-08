package me.wyne.wutils.i18n.language.replacement;

import me.wyne.wutils.i18n.I18n;

public interface TextReplacement extends Replacement<String> {
    default ComponentReplacement as() {
        return component -> I18n.global.component().fromString(replace(I18n.global.component().toString(component)));
    }
}
