package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;

/**
 * Shows {@code title} to {@code audience}.
 */
public record TitleEffect(@NotNull Audience audience, @NotNull Title title) implements AnimationRunnable {

    @Override
    public void run() {
        audience.showTitle(title);
    }

}
