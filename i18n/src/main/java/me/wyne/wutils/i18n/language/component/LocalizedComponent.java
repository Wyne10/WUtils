package me.wyne.wutils.i18n.language.component;

import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.interpretation.ComponentInterpreter;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * A localized {@link Component} resolved via a {@link ComponentInterpreter}, with convenience methods
 * to send it through a {@link ComponentAudiences} and to serialize it in other text formats.
 */
public class LocalizedComponent extends BaseLocalized<Component, ComponentInterpreter> implements ComponentLike {

    private final Component component;
    private final ComponentAudiences audiences;

    public LocalizedComponent(@NotNull ComponentInterpreter interpreter, @NotNull Language language, @NotNull String path, @NotNull Component component, @NotNull ComponentAudiences audiences) {
        super(interpreter, language, path);
        this.component = component;
        this.audiences = audiences;
    }

    public void sendMessage(@NotNull Audience audience) {
        audience.sendMessage(component);
    }

    public void sendMessage(@NotNull Player player) {
        audiences.player(player).sendMessage(component);
    }

    public void sendMessage(@NotNull CommandSender sender) {
        audiences.sender(sender).sendMessage(component);
    }

    /**
     * Sends this message only if {@code sender} is a {@link Player}; otherwise does nothing.
     */
    public void sendMessagePlayer(@NotNull CommandSender sender) {
        if (sender instanceof Player)
            sendMessage(sender);
    }

    public void sendMessage(@NotNull UUID playerId) {
        audiences.player(playerId).sendMessage(component);
    }

    public void sendMessageAll() {
        audiences.all().sendMessage(component);
    }

    public void sendMessage(@NotNull Predicate<CommandSender> filter) {
        audiences.filter(filter).sendMessage(component);
    }

    public void sendMessageConsole() {
        audiences.console().sendMessage(component);
    }

    public void sendMessage(@NotNull Key permission) {
        audiences.permission(permission).sendMessage(component);
    }

    public void sendMessage(@NotNull String permission) {
        audiences.permission(permission).sendMessage(component);
    }

    public void sendMessagePlayers() {
        audiences.players().sendMessage(component);
    }

    public void sendMessageServer(@NotNull String serverName) {
        audiences.server(serverName).sendMessage(component);
    }

    public void sendMessageWorld(@NotNull Key worldKey) {
        audiences.world(worldKey).sendMessage(component);
    }

    public void sendActionBar(@NotNull Player player) {
        audiences.player(player).sendActionBar(component);
    }

    @Override
    public @NotNull Component get() {
        return component;
    }

    public @NotNull ComponentAudiences getAudiences() {
        return audiences;
    }

    public @NotNull String legacy() {
        return I18n.serializeLegacy(component);
    }

    public @NotNull String legacySection() {
        return I18n.serializeLegacySection(component);
    }

    public @NotNull String gson() {
        return I18n.serializeGson(component);
    }

    public @NotNull String plain() {
        return I18n.serializePlain(component);
    }

    public @NotNull String plainText() {
        return I18n.serializePlainText(component);
    }

    public @NotNull String miniMessage() {
        return I18n.serializeMiniMessage(component);
    }

    public @NotNull BaseComponent[] bungee() {
        return I18n.serializeBungee(component);
    }

    @SuppressWarnings("UnstableApiUsage")
    public @NotNull Object minecraft() {
        return MinecraftComponentSerializer.get().serialize(component);
    }

    @Override
    public @NotNull String toString() {
        return getInterpreter().toString(component);
    }

    /**
     * Returns a new {@code LocalizedComponent} with every replacement applied to this component.
     */
    public @NotNull LocalizedComponent replace(@NotNull ComponentReplacement... componentReplacements) {
        Component result = Component.empty().append(component);
        for (ComponentReplacement replacement : componentReplacements)
            result = replacement.replace(result);
        return new LocalizedComponent(getInterpreter(), getLanguage(), getPath(), result, audiences);
    }

    @Override
    public @NotNull Component asComponent() {
        return component;
    }

    public @NotNull Map<@NotNull String, @NotNull String> styleMap(@NotNull String key) {
        return I18n.styleMap(getInterpreter(), component, key);
    }

    /**
     * Returns the value under {@code key} that equals {@code value} — see
     * {@link I18n#style(ComponentInterpreter, Component, String, String)} for how {@code null} is
     * decided.
     */
    public @Nullable String style(@NotNull String key, @NotNull String value) {
        return I18n.style(getInterpreter(), component, key, value);
    }

}
