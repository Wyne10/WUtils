package me.wyne.wutils.structure.modifier.clipboard;

import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.structure.modifier.ClipboardModifier;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class FlipClipboardModifier extends ConfigurableAttribute<FlipSettings> implements ClipboardModifier {

    public FlipClipboardModifier(@NotNull String key, @NotNull FlipSettings value) {
        super(key, value);
    }

    public FlipClipboardModifier(@NotNull FlipSettings value) {
        super(StructureModifier.CLIPBOARD_FLIP.getKey(), value);
    }

    @Override
    public void apply(@NotNull ClipboardHolder clipboardHolder) {
        var axes = getValue().axes();
        var axis = axes.get(ThreadLocalRandom.current().nextInt(axes.size()));
        var transform = transformFor(axis);
        if (transform == null)
            return;
        clipboardHolder.setTransform(clipboardHolder.getTransform().combine(transform));
    }

    private static @Nullable AffineTransform transformFor(@NotNull FlipSettings.Axis axis) {
        return switch (axis) {
            case NONE -> null;
            case X -> new AffineTransform().scale(-1, 1, 1);
            case Y -> new AffineTransform().scale(1, -1, 1);
            case Z -> new AffineTransform().scale(1, 1, -1);
        };
    }

    public static final class Factory implements AttributeFactory<FlipClipboardModifier> {
        @Override
        public @NotNull FlipClipboardModifier create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new FlipClipboardModifier(key, FlipSettings.parse(config.getString(key, "")));
        }
    }
}
