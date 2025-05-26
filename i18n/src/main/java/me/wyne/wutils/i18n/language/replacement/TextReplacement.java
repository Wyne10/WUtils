package me.wyne.wutils.i18n.language.replacement;

import me.wyne.wutils.i18n.I18n;

public interface TextReplacement extends Replacement<String> {
    default ComponentReplacement asComponentReplacement() {
        return component -> I18n.global.component().fromString(replace(I18n.global.component().toString(component)));
    }

    default TextReplacement then(TextReplacement replacement) {
        return obj -> {
            obj = this.replace(obj);
            obj = replacement.replace(obj);
            return obj;
        };
    }
}
