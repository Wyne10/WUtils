package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;

public record SoundEffect(Audience audience, Sound sound) implements AnimationRunnable {

    @Override
    public void run() {
        audience.playSound(sound);
    }

}
