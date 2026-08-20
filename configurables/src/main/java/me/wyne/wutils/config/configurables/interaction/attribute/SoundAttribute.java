package me.wyne.wutils.config.configurables.interaction.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.interaction.ContextInteractionAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttributeContext;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code sound} payload — plays the configured sound to the audience.
 *
 * <p>A thin subclass of the shared {@link me.wyne.wutils.config.configurables.attribute.common.SoundAttribute},
 * so it accepts both the string and section config forms and, because its factory dispatches through
 * a {@code CompositeAttributeFactory}, can be aliased with {@code attributeType}.</p>
 */
public class SoundAttribute extends me.wyne.wutils.config.configurables.attribute.common.SoundAttribute implements ContextInteractionAttribute {

    public SoundAttribute(@NotNull String key, @NotNull Sound value) {
        super(key, value);
    }

    public SoundAttribute(@NotNull Sound value) {
        super(InteractionAttribute.SOUND.getKey(), value);
    }

    @Override
    public void send(@NotNull Audience audience, @NotNull CommandSender sender, @NotNull InteractionAttributeContext context) {
        audience.playSound(getValue());
    }

    public static final class Factory implements AttributeFactory<SoundAttribute> {
        @Override
        public @NotNull SoundAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new SoundAttribute(key, new me.wyne.wutils.config.configurables.attribute.common.SoundAttribute.Factory().create(key, config).getValue());
        }
    }

}
