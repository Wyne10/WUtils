package me.wyne.wutils.config.configurables.item;

import org.jetbrains.annotations.NotNull;

/**
 * Marks an attribute that is not applied automatically during
 * {@code ItemConfigurable.build}, and must instead be invoked deliberately — e.g. a GUI
 * {@code command} fired outside a click.
 */
public interface ManualAttribute {
    default void apply() {
        apply(ItemAttributeContext.EMPTY);
    }

    default void apply(@NotNull ItemAttributeContext context) {
        apply();
    }
}
