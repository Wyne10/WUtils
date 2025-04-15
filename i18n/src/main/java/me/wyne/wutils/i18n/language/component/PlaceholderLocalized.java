package me.wyne.wutils.i18n.language.component;

import me.wyne.wutils.i18n.language.interpretation.Interpreter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface PlaceholderLocalized<T, I extends Interpreter> extends Localized<T, I> {

    @Nullable
    OfflinePlayer getOfflinePlayer();
    @Nullable
    Player getPlayer();

}
