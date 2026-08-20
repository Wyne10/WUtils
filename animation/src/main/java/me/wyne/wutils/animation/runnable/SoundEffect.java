package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

/**
 * Plays {@code sound} for {@code audience}, following Adventure's own emitter/positioning
 * rules rather than a fixed world location (compare {@link LocalSound}).
 */
public record SoundEffect(@NotNull Audience audience, @NotNull Sound sound) implements AnimationRunnable {

    @Override
    public void run() {
        audience.playSound(sound);
    }

}
