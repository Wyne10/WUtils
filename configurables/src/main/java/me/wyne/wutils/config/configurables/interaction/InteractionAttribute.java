package me.wyne.wutils.config.configurables.interaction;

public enum InteractionAttribute {
    AUDIENCE_PLAYER("toPlayer"),
    AUDIENCE_ALL("toAll"),
    AUDIENCE_CONSOLE("toConsole"),
    AUDIENCE_PLAYERS("toPlayers"),
    AUDIENCE_PERMISSIONS("toPermissions"),
    AUDIENCE_WORLDS("toWorlds"),
    AUDIENCE_THAT_PLAYERS("toThatPlayers"),
    MESSAGE("message"),
    ACTION_BAR("actionBar"),
    SOUND("sound");

    private final String key;

    InteractionAttribute(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
