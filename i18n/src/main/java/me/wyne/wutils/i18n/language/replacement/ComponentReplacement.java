package me.wyne.wutils.i18n.language.replacement;

import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.text.Component;

public interface ComponentReplacement extends Replacement<Component> {
    default TextReplacement asTextReplacement() {
        return string -> I18n.global.component().toString(replace(I18n.global.component().fromString(string)));
    }

    default ComponentReplacement then(ComponentReplacement replacement) {
        return obj -> {
            obj = this.replace(obj);
            obj = replacement.replace(obj);
            return obj;
        };
    }
}
