package me.wyne.wutils.i18n.language.replacement;

import net.kyori.adventure.text.Component;

@FunctionalInterface
public interface ComponentReplacement {

    Component replace(Component component);

}
