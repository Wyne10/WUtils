package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Sends {@code message} to {@code audience}.
 */
public record MessageEffect(@NotNull Audience audience, @NotNull Component message) implements AnimationRunnable {

    @Override
    public void run() {
        audience.sendMessage(message);
    }

}
