package me.wyne.wutils.config.configurables.animation;

import org.jetbrains.annotations.NotNull;

/**
 * Config keys for an animation step's type, timing and effect attributes, as registered in
 * {@code AnimationStepConfigurable}.
 */
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

    AnimationAttribute(@NotNull String key) {
        this.key = key;
    }

    public @NotNull String getKey() {
        return key;
    }
}
