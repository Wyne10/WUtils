package me.wyne.wutils.config.configurables.animation;

public enum AnimationAttribute {
    TYPE("type"),
    DELAY("delay"),
    PERIOD("period"),
    DURATION("duration"),
    ANCHOR_CHARGE("anchorCharge"),
    FORCE_FIELD("forceField"),
    PLAYER_TITLE("playerTitle"),
    LOCAL_SOUND("localSound"),
    PLAYER_SOUND("playerSound"),
    WORLD_PARTICLE("worldParticle"),
    FIREWORK("firework"),
    PLAYER_MESSAGE("playerMessage"),
    GLOBAL_MESSAGE("globalMessage"),
    INTERACTION("interaction"),
    INTERACTIONS("interactions");

    private final String key;

    AnimationAttribute(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
