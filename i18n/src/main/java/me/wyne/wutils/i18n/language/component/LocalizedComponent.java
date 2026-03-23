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

import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class LocalizedComponent extends BaseLocalized<Component, ComponentInterpreter> implements ComponentLike {

    private final Component component;
    private final ComponentAudiences audiences;

    public LocalizedComponent(ComponentInterpreter interpreter, Language language, String path, Component component, ComponentAudiences audiences) {
        super(interpreter, language, path);
        this.component = component;
        this.audiences = audiences;
    }

    public void sendMessage(Audience audience) {
        audience.sendMessage(component);
    }

    public void sendMessage(Player player) {
        audiences.player(player).sendMessage(component);
    }

    public void sendMessage(CommandSender sender) {
        audiences.sender(sender).sendMessage(component);
    }

    public void sendMessagePlayer(CommandSender sender) {
        if (sender instanceof Player)
            sendMessage(sender);
    }

    public void sendMessage(UUID playerId) {
        audiences.player(playerId).sendMessage(component);
    }

    public void sendMessageAll() {
        audiences.all().sendMessage(component);
    }

    public void sendMessage(Predicate<CommandSender> filter) {
        audiences.filter(filter).sendMessage(component);
    }

    public void sendMessageConsole() {
        audiences.console().sendMessage(component);
    }

    public void sendMessage(Key permission) {
        audiences.permission(permission).sendMessage(component);
    }

    public void sendMessage(String permission) {
        audiences.permission(permission).sendMessage(component);
    }

    public void sendMessagePlayers() {
        audiences.players().sendMessage(component);
    }

    public void sendMessageServer(String serverName) {
        audiences.server(serverName).sendMessage(component);
    }

    public void sendMessageWorld(Key worldKey) {
        audiences.world(worldKey).sendMessage(component);
    }

    public void sendActionBar(Player player) {
        audiences.player(player).sendActionBar(component);
    }

    @Override
    public Component get() {
        return component;
    }

    public ComponentAudiences getAudiences() {
        return audiences;
    }

    public String legacy() {
        return I18n.serializeLegacy(component);
    }

    public String legacySection() {
        return I18n.serializeLegacySection(component);
    }

    public String gson() {
        return I18n.serializeGson(component);
    }

    public String plain() {
        return I18n.serializePlain(component);
    }

    public String plainText() {
        return I18n.serializePlainText(component);
    }

    public String miniMessage() {
        return I18n.serializeMiniMessage(component);
    }

    public BaseComponent[] bungee() {
        return I18n.serializeBungee(component);
    }

    @SuppressWarnings("UnstableApiUsage")
    public Object minecraft() {
        return MinecraftComponentSerializer.get().serialize(component);
    }

    @Override
    public String toString() {
        return getInterpreter().toString(component);
    }

    public LocalizedComponent replace(ComponentReplacement... componentReplacements) {
        Component result = Component.empty().append(component);
        for (ComponentReplacement replacement : componentReplacements)
            result = replacement.replace(result);
        return new LocalizedComponent(getInterpreter(), getLanguage(), getPath(), result, audiences);
    }

    @Override
    public @NotNull Component asComponent() {
        return component;
    }

    public Map<String, String> styleMap(String key) {
        return I18n.styleMap(getInterpreter(), component, key);
    }

    public String style(String key, String value) {
        return I18n.style(getInterpreter(), component, key, value);
    }

}
