package me.wyne.wutils.i18n.language.component;

import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.interpretation.ComponentInterpreter;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link LocalizedComponent} resolved with PlaceholderAPI placeholders expanded for a specific
 * player.
 */
public class PlaceholderLocalizedComponent extends LocalizedComponent implements PlaceholderLocalized<Component, ComponentInterpreter> {

    @Nullable
    private final OfflinePlayer player;

    public PlaceholderLocalizedComponent(@NotNull ComponentInterpreter interpreter, @NotNull Language language, @NotNull String path, @NotNull Component component, @NotNull ComponentAudiences audiences, @Nullable OfflinePlayer player) {
        super(interpreter, language, path, component, audiences);
        this.player = player;
    }

    @Override
    public @NotNull PlaceholderLocalizedComponent replace(@NotNull ComponentReplacement... componentReplacements) {
        Component result = Component.empty().append(get());
        for (ComponentReplacement replacement : componentReplacements)
            result = replacement.replace(result);
        return new PlaceholderLocalizedComponent(getInterpreter(), getLanguage(), getPath(), result, getAudiences(), player);
    }

    @Override
    public @Nullable OfflinePlayer getOfflinePlayer() {
        return player;
    }

    @Override
    public @Nullable Player getPlayer() {
        return player != null ? player.getPlayer() : null;
    }

}
