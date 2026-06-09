package me.wyne.wutils.config.configurables.invui;

public enum InvUiAttribute {
    KEY("key");

    private final String key;

    InvUiAttribute(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
