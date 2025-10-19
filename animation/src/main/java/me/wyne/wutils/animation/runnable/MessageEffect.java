package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public record MessageEffect(Audience audience, Component message) implements AnimationRunnable {

    @Override
    public void run() {
        audience.sendMessage(message);
    }

}
