package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.title.Title;

public record TitleEffect(Audience audience, Title title) implements AnimationRunnable {

    @Override
    public void run() {
        audience.showTitle(title);
    }

}
