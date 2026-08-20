package me.wyne.wutils.structure.modifier.clipboard;

import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.structure.modifier.ClipboardModifier;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class RotateClipboardModifier extends ConfigurableAttribute<RotateSettings> implements ClipboardModifier {

    public RotateClipboardModifier(@NotNull String key, @NotNull RotateSettings value) {
        super(key, value);
    }

    public RotateClipboardModifier(@NotNull RotateSettings value) {
        super(StructureModifier.CLIPBOARD_ROTATE.getKey(), value);
    }

    @Override
    public void apply(@NotNull ClipboardHolder clipboardHolder) {
        var angles = getValue().angles();
        int angle = angles.get(ThreadLocalRandom.current().nextInt(angles.size()));
        if (angle == 0)
            return;
        clipboardHolder.setTransform(clipboardHolder.getTransform().combine(new AffineTransform().rotateY(angle)));
    }

    public static final class Factory implements AttributeFactory<RotateClipboardModifier> {
        @Override
        public @NotNull RotateClipboardModifier create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new RotateClipboardModifier(key, RotateSettings.parse(config.getString(key, "")));
        }
    }
}
