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
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class InteractionConfigurable extends AttributeConfigurable {

    public final static AttributeMap INTERACTION_ATTRIBUTE_MAP = new AttributeMap(new LinkedHashMap<>());

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
        // TODO Title
        // TODO Boss Bar
    }

    public InteractionConfigurable() {
        super(new ImmutableAttributeContainer(INTERACTION_ATTRIBUTE_MAP));
    }

    public InteractionConfigurable(ConfigurationSection section) {
        super(new ImmutableAttributeContainer(INTERACTION_ATTRIBUTE_MAP), section);
    }

    public InteractionConfigurable(AttributeContainer attributeContainer) {
        super(attributeContainer);
    }

    public InteractionConfigurable(AttributeContainer attributeContainer, ConfigurationSection section) {
        super(attributeContainer, section);
    }

    public void send(CommandSender sender, InteractionAttributeContext context) {
        var audiences = getAttributeContainer().getSet(InteractionAudienceAttribute.class)
                .stream().map(attribute -> attribute.get(sender))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (audiences.isEmpty())
            audiences.add(new PlayerAudience().get(sender));
        var audience = Audience.audience(audiences);
        getAttributeContainer().getSet(ContextInteractionAttribute.class)
                .forEach(attribute -> attribute.send(audience, sender, context));
    }

    public void send(CommandSender sender, @Nullable OfflinePlayer placeholderTarget, TextReplacement... textReplacements) {
        var context = new InteractionAttributeContext(placeholderTarget, textReplacements, new ComponentReplacement[]{});
        send(sender, context);
    }
    public void send(CommandSender sender, TextReplacement... textReplacements) {
        send(sender, I18n.toOfflinePlayer(sender), textReplacements);
    }

    public void sendComponent(CommandSender sender, @Nullable OfflinePlayer placeholderTarget, ComponentReplacement... componentReplacements) {
        var context = new InteractionAttributeContext(placeholderTarget, new TextReplacement[]{}, componentReplacements);
        send(sender, context);
    }
    public void sendComponent(CommandSender sender, ComponentReplacement... componentReplacements) {
        sendComponent(sender, I18n.toOfflinePlayer(sender), componentReplacements);
    }

    public static AttributeContainerBuilder builder() {
        return new InteractionConfigurable().getAttributeContainer().toBuilder();
    }

}
