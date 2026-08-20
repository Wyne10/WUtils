package me.wyne.wutils.config.configurables.gui;

import org.jetbrains.annotations.NotNull;

/**
 * Config keys for the GUI item attribute vocabulary that {@code GuiConfigurable} adds on top of
 * {@code ItemConfigurable}.
 *
 * <p>{@link #CLICK} is defined but deliberately never registered in {@code GuiConfigurable}'s
 * attribute map — a {@code GuiAction} lambda cannot come from YAML. Attach a
 * {@code GuiActionAttribute} through an accessor instead.</p>
 */
public enum GuiItemAttribute {
    PRINT("print"),
    SOUND("sound"),
    SLOT("slot"),
    COMMAND("command"),
    CLICK("click");

    private final String key;

    GuiItemAttribute(@NotNull String key) {
        this.key = key;
    }

    public @NotNull String getKey() {
        return key;
    }
}
