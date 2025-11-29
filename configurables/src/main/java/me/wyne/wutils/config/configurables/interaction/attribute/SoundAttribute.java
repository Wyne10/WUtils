package me.wyne.wutils.config.configurables.interaction.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.interaction.ContextInteractionAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttributeContext;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class SoundAttribute extends me.wyne.wutils.config.configurables.attribute.common.SoundAttribute implements ContextInteractionAttribute {

    public SoundAttribute(String key, Sound value) {
        super(key, value);
    }

    public SoundAttribute(Sound value) {
        super(InteractionAttribute.SOUND.getKey(), value);
    }

    @Override
    public void send(Audience audience, CommandSender sender, InteractionAttributeContext context) {
        audience.playSound(getValue());
    }

    public static final class Factory implements AttributeFactory<SoundAttribute> {
        @Override
        public SoundAttribute create(String key, ConfigurationSection config) {
            return new SoundAttribute(key, new me.wyne.wutils.config.configurables.attribute.common.SoundAttribute.Factory().create(key, config).getValue());
        }
    }

}
