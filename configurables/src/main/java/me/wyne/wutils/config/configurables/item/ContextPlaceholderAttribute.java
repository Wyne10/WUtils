package me.wyne.wutils.config.configurables.item;

import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;

public interface ContextPlaceholderAttribute {
    void apply(TextReplacement... replacements);

    void apply(ComponentReplacement... replacements);
}
