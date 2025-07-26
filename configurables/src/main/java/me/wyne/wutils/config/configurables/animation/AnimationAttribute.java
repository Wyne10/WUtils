package me.wyne.wutils.config.configurables.animation;

public enum AnimationAttribute {
    DELAY("delay"),
    PERIOD("period"),
    DURATION("duration"),
    ANCHOR_CHARGE("anchorCharge"),
    FORCE_FIELD("forceField");

    private final String key;

    AnimationAttribute(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
