package me.wyne.wutils.config.configurables.animation;

public enum AnimationAttribute {
    DELAY("delay"),
    PERIOD("period"),
    DURATION("duration"),
    ANCHOR_CHARGE("anchorCharge"),
    FORCE_FIELD("forceField"),
    PLAYER_TITLE_EFFECT("playerTitle"),
    LOCAL_SOUND_EFFECT("localSound"),
    WORLD_PARTICLE_EFFECT("worldParticle");

    private final String key;

    AnimationAttribute(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
