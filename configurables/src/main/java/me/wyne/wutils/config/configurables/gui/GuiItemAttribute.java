package me.wyne.wutils.config.configurables.gui;

public enum GuiItemAttribute {
    PRINT("print"),
    SOUND("sound"),
    SLOT("slot"),
    COMMAND("command"),
    CLICK("click");

    private final String key;

    GuiItemAttribute(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
