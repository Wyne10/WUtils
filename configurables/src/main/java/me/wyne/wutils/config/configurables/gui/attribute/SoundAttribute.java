package me.wyne.wutils.config.configurables.gui.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.gui.ClickEventAttribute;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Plays a configured sound to the clicking player on a GUI item click.
 *
 * <p>One of three unrelated classes named {@code SoundAttribute} in this module — this one, the
 * one it extends in {@code attribute.common}, and another under {@code interaction.attribute}.
 * Import the right one. Unlike sibling GUI attributes such as {@code print}/{@code command}/
 * {@code slot}, this one delegates to the common {@link me.wyne.wutils.config.configurables.attribute.common.SoundAttribute}'s
 * composite factory, so it can be aliased with {@code attributeType}.</p>
 */
public class SoundAttribute extends me.wyne.wutils.config.configurables.attribute.common.SoundAttribute implements ClickEventAttribute {

    public SoundAttribute(@NotNull String key, @NotNull Sound value) {
        super(key, value);
    }

    public SoundAttribute(@NotNull Sound value) {
        super(GuiItemAttribute.SOUND.getKey(), value);
    }

    @Override
    public void apply(@NotNull InventoryClickEvent event) {
        event.getWhoClicked().playSound(getValue());
    }

    public static final class Factory implements AttributeFactory<SoundAttribute> {
        @Override
        public @NotNull SoundAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new SoundAttribute(key, new me.wyne.wutils.config.configurables.attribute.common.SoundAttribute.Factory().create(key, config).getValue());
        }
    }

}
