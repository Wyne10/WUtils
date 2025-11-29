package me.wyne.wutils.common.particle;

import me.wyne.wutils.common.Args;
import org.bukkit.Color;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.Collection;

public class DustOptionsParser implements StringDataParser<Particle.DustOptions> {

    private final static Collection<String> suggestions = new ArrayList<>() { {add("<color(24bit rgb):size(float)>");} };

    @Override
    public Collection<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public Particle.DustOptions getData(String string) {
        Args args = new Args(string);
        Color color = Color.fromRGB(Integer.parseUnsignedInt(args.get(0, "0").replace("#", ""), 16));
        float size = Float.parseFloat(args.get(1, "1.0"));
        return new Particle.DustOptions(color, size);
    }

    @Override
    public String toString(Object data) {
        var dustOptions = (Particle.DustOptions)data;
        return dustOptions.getColor().asRGB() + ":" + dustOptions.getSize();
    }

}
