package me.wyne.wutils.i18n.language.component;

import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.interpretation.ComponentInterpreter;
import me.wyne.wutils.i18n.language.interpretation.LegacyInterpreter;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Predicate;

public class LocalizedComponent extends BaseLocalized<Component, ComponentInterpreter> implements ComponentLike {

    private final Component component;

    public LocalizedComponent(ComponentInterpreter interpreter, Language language, String path, Component component) {
        super(interpreter, language, path);
        this.component = component;
    }

    public void sendMessage(Audience audience) {
        audience.sendMessage(component);
    }

    public void sendMessage(Player player) {
        I18n.global.audiences.player(player).sendMessage(component);
    }

    public void sendMessage(CommandSender sender) {
        I18n.global.audiences.sender(sender).sendMessage(component);
    }

    public void sendMessage(UUID playerId) {
        I18n.global.audiences.player(playerId).sendMessage(component);
    }

    public void sendMessageAll() {
        I18n.global.audiences.all().sendMessage(component);
    }

    public void sendMessage(Predicate<CommandSender> filter) {
        I18n.global.audiences.filter(filter).sendMessage(component);
    }

    public void sendMessageConsole() {
        I18n.global.audiences.console().sendMessage(component);
    }

    public void sendMessage(Key permission) {
        I18n.global.audiences.permission(permission).sendMessage(component);
    }

    public void sendMessage(String permission) {
        I18n.global.audiences.permission(permission).sendMessage(component);
    }

    public void sendMessagePlayers() {
        I18n.global.audiences.players().sendMessage(component);
    }

    public void sendMessageServer(String serverName) {
        I18n.global.audiences.server(serverName).sendMessage(component);
    }

    public void sendMessageWorld(Key worldKey) {
        I18n.global.audiences.world(worldKey).sendMessage(component);
    }

    public void sendActionBar(Player player) {
        I18n.global.audiences.player(player).sendActionBar(component);
    }

    @Override
    public Component get() {
        return component;
    }

    public String legacy() {
        return LegacyInterpreter.SERIALIZER.serialize(component);
    }

    public String gson() {
        return GsonComponentSerializer.gson().serialize(component);
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public String plain() {
        return PlainComponentSerializer.plain().serialize(component);
    }

    public String plainText() {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public String plainValid() {
        if (I18n.IS_PLAIN_TEXT_UNAVAILABLE)
            return plain();
        return plainText();
    }

    public String miniMessage() {
        return MiniMessage.miniMessage().serialize(component);
    }

    public BaseComponent[] bungee() {
        return BungeeComponentSerializer.get().serialize(component);
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
        Component result = component;
        for (ComponentReplacement replacement : componentReplacements)
            result = replacement.replace(result);
        return new LocalizedComponent(getInterpreter(), getLanguage(), getPath(), result);
    }

    @Override
    public @NotNull Component asComponent() {
        return component;
    }

}
