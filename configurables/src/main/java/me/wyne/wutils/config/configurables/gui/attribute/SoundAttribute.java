package me.wyne.wutils.config.configurables.gui.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.gui.ClickEventAttribute;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SoundAttribute extends me.wyne.wutils.config.configurables.attribute.common.SoundAttribute implements ClickEventAttribute {

    public SoundAttribute(String key, Sound value) {
        super(key, value);
    }

    public SoundAttribute(Sound value) {
        super(GuiItemAttribute.SOUND.getKey(), value);
    }

    @Override
    public void apply(InventoryClickEvent event) {
        event.getWhoClicked().playSound(getValue());
    }

    public static final class Factory implements AttributeFactory<SoundAttribute> {
        @Override
        public SoundAttribute create(String key, ConfigurationSection config) {
            return new SoundAttribute(key, new me.wyne.wutils.config.configurables.attribute.common.SoundAttribute.Factory().create(key, config).getValue());
        }
    }

}
