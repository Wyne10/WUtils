package me.wyne.wutils.i18n.language.replacement;

import net.kyori.adventure.text.Component;

public interface ComponentReplacement extends Replacement<Component> {
    default ComponentReplacement then(ComponentReplacement replacement) {
        return obj -> {
            obj = this.replace(obj);
            obj = replacement.replace(obj);
            return obj;
        };
    }
}
