package me.wyne.wutils.i18n.language.component;

import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.interpretation.ComponentInterpreter;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PlaceholderLocalizedComponent extends LocalizedComponent implements PlaceholderLocalized<Component, ComponentInterpreter> {

    @Nullable
    private final OfflinePlayer player;

    public PlaceholderLocalizedComponent(ComponentInterpreter interpreter, Language language, String path, Component component, ComponentAudiences audiences, @Nullable OfflinePlayer player) {
        super(interpreter, language, path, component, audiences);
        this.player = player;
    }

    @Override
    public PlaceholderLocalizedComponent replace(ComponentReplacement... componentReplacements) {
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
