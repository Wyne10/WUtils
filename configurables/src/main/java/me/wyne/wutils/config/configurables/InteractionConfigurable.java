package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.*;
import me.wyne.wutils.config.configurables.interaction.*;
import me.wyne.wutils.config.configurables.interaction.attribute.*;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import net.kyori.adventure.audience.Audience;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 * A single reaction to something happening — one or more {@link ContextInteractionAttribute}s (a
 * message, a sound, a command, ...) fired at an {@link Audience} resolved from the configured
 * {@link InteractionAudienceAttribute}s.
 *
 * <p>{@link #getAudience} unions every declared audience attribute with no de-duplication, and falls
 * back to a {@link PlayerAudience} wrapping the sender <em>only when no audience attribute is
 * declared at all</em> — configuring any audience key (e.g. {@code audience-all}) silently drops the
 * sender from the recipients unless it is also declared.</p>
 */
public class InteractionConfigurable extends AttributeConfigurable {

    public final static AttributeMap INTERACTION_ATTRIBUTE_MAP = new AttributeMap();

    static {
        INTERACTION_ATTRIBUTE_MAP.put(InteractionAttribute.AUDIENCE_PLAYER.getKey(), new PlayerAudience.Factory());
        INTERACTION_ATTRIBUTE_MAP.put(InteractionAttribute.AUDIENCE_ALL.getKey(), new AllAudience.Factory());
        INTERACTION_ATTRIBUTE_MAP.put(InteractionAttribute.AUDIENCE_CONSOLE.getKey(), new ConsoleAudience.Factory());
        INTERACTION_ATTRIBUTE_MAP.put(InteractionAttribute.AUDIENCE_PLAYERS.getKey(), new PlayersAudience.Factory());
        INTERACTION_ATTRIBUTE_MAP.put(InteractionAttribute.AUDIENCE_PERMISSIONS.getKey(), new PermissionAudience.Factory());
        INTERACTION_ATTRIBUTE_MAP.put(InteractionAttribute.AUDIENCE_WORLDS.getKey(), new WorldAudience.Factory());
        INTERACTION_ATTRIBUTE_MAP.put(InteractionAttribute.AUDIENCE_THAT_PLAYERS.getKey(), new ThatPlayersAudience.Factory());
        INTERACTION_ATTRIBUTE_MAP.put(InteractionAttribute.MESSAGE.getKey(), new MessageAttribute.Factory());
        INTERACTION_ATTRIBUTE_MAP.put(InteractionAttribute.ACTION_BAR.getKey(), new ActionBarAttribute.Factory());
        INTERACTION_ATTRIBUTE_MAP.put(InteractionAttribute.SOUND.getKey(), new SoundAttribute.Factory());
        INTERACTION_ATTRIBUTE_MAP.put(InteractionAttribute.CONSOLE_COMMAND.getKey(), new ConsoleCommandAttribute.Factory());
        INTERACTION_ATTRIBUTE_MAP.put(InteractionAttribute.PLAYER_COMMAND.getKey(), new PlayerCommandAttribute.Factory());
        INTERACTION_ATTRIBUTE_MAP.put(InteractionAttribute.TITLE.getKey(), new TitleAttribute.Factory());
        // TODO Boss Bar
    }

    public InteractionConfigurable() {
        super(new ImmutableAttributeContainer(INTERACTION_ATTRIBUTE_MAP));
    }

    public InteractionConfigurable(@NotNull ConfigurationSection section) {
        super(new ImmutableAttributeContainer(INTERACTION_ATTRIBUTE_MAP), section);
    }

    public InteractionConfigurable(@NotNull AttributeContainer attributeContainer) {
        super(attributeContainer);
    }

    public InteractionConfigurable(@NotNull AttributeContainer attributeContainer, @NotNull ConfigurationSection section) {
        super(attributeContainer, section);
    }

    public void send(@NotNull CommandSender sender, @NotNull InteractionAttributeContext context) {
        var audience = getAudience(sender);
        getAttributeContainer().getSet(ContextInteractionAttribute.class)
                .forEach(attribute -> attribute.send(audience, sender, context));
    }

    public void send(@NotNull CommandSender sender, @Nullable OfflinePlayer placeholderTarget, @NotNull TextReplacement... textReplacements) {
        var context = new InteractionAttributeContext(placeholderTarget, textReplacements, new ComponentReplacement[]{});
        send(sender, context);
    }
    public void send(@NotNull CommandSender sender, @NotNull TextReplacement... textReplacements) {
        send(sender, I18n.toOfflinePlayer(sender), textReplacements);
    }

    public void sendComponent(@NotNull CommandSender sender, @Nullable OfflinePlayer placeholderTarget, @NotNull ComponentReplacement... componentReplacements) {
        var context = new InteractionAttributeContext(placeholderTarget, new TextReplacement[]{}, componentReplacements);
        send(sender, context);
    }

    public void sendComponent(@NotNull CommandSender sender, @NotNull ComponentReplacement... componentReplacements) {
        sendComponent(sender, I18n.toOfflinePlayer(sender), componentReplacements);
    }

    public @NotNull Audience getAudience(@NotNull CommandSender sender) {
        var audiences = getAttributeContainer().getSet(InteractionAudienceAttribute.class)
                .stream().map(attribute -> attribute.get(sender))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (audiences.isEmpty())
            audiences.add(new PlayerAudience().get(sender));
        return Audience.audience(audiences);
    }

    public static @NotNull AttributeContainerBuilder builder() {
        return new InteractionConfigurable().getAttributeContainer().toBuilder();
    }

}
