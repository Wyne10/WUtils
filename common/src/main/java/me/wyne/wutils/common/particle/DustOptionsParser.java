package me.wyne.wutils.common.particle;

import me.wyne.wutils.common.Args;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Parses {@link Particle.DustOptions} from a {@code "<color>:<size>"} token (colon- or
 * whitespace-separated, via {@link Args}). Color is a 24-bit RGB hex value ({@code #} optional,
 * defaults to {@code 0} i.e. black); size is a float (defaults to {@code 1.0}). Either part may
 * be omitted.
 */
public class DustOptionsParser implements StringDataParser<Particle.DustOptions> {

    private final static Collection<String> suggestions = new ArrayList<>() { {add("<color(24bit rgb):size(float)>");} };

    @Override
    public @NotNull Collection<@NotNull String> getSuggestions() {
        return suggestions;
    }

    /** @throws NumberFormatException if a present color or size part is not validly formatted */
    @Override
    public @NotNull Particle.DustOptions getData(@NotNull String string) {
        Args args = new Args(string);
        Color color = Color.fromRGB(Integer.parseUnsignedInt(args.get(0, "0").replace("#", ""), 16));
        float size = Float.parseFloat(args.get(1, "1.0"));
        return new Particle.DustOptions(color, size);
    }

    @Override
    public @NotNull String toString(@Nullable Object data) {
        var dustOptions = (Particle.DustOptions)data;
        return dustOptions.getColor().asRGB() + ":" + dustOptions.getSize();
    }

}
