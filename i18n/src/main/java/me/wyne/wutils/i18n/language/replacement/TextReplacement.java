package me.wyne.wutils.i18n.language.replacement;

public interface TextReplacement extends Replacement<String> {
    default TextReplacement then(TextReplacement replacement) {
        return obj -> {
            obj = this.replace(obj);
            obj = replacement.replace(obj);
            return obj;
        };
    }
}
