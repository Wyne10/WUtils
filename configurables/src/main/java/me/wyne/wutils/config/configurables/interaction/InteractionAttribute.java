package me.wyne.wutils.config.configurables.interaction;

import org.jetbrains.annotations.NotNull;

/**
 * Config keys for the audience and payload attributes an interaction section resolves against, as
 * registered in {@code InteractionConfigurable}.
 */
public enum InteractionAttribute {
    AUDIENCE_PLAYER("toPlayer"),
    AUDIENCE_ALL("toAll"),
    AUDIENCE_CONSOLE("toConsole"),
    AUDIENCE_PLAYERS("toPlayers"),
    AUDIENCE_PERMISSIONS("toPermissions"),
    AUDIENCE_WORLDS("toWorlds"),
    AUDIENCE_THAT_PLAYERS("toThatPlayers"),
    MESSAGE("message"),
    CONSOLE_COMMAND("console"),
    PLAYER_COMMAND("command"),
    ACTION_BAR("action"),
    SOUND("sound"),
    TITLE("title");

    private final String key;

    InteractionAttribute(@NotNull String key) {
        this.key = key;
    }

    public @NotNull String getKey() {
        return key;
    }
}
