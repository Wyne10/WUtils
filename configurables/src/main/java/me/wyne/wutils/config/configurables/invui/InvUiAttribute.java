package me.wyne.wutils.config.configurables.invui;

import org.jetbrains.annotations.NotNull;

/**
 * Config keys for the InvUI item attribute vocabulary that {@code InvUiItemConfigurable} adds on
 * top of {@code ItemConfigurable}.
 */
public enum InvUiAttribute {
    KEY("key");

    private final String key;

    InvUiAttribute(@NotNull String key) {
        this.key = key;
    }

    public @NotNull String getKey() {
        return key;
    }
}
