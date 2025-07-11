package me.wyne.wutils.config.configurables.item;

public interface ManualAttribute {
    default void apply() {
        apply(ItemAttributeContext.EMPTY);
    }

    default void apply(ItemAttributeContext context) {
        apply();
    }
}
