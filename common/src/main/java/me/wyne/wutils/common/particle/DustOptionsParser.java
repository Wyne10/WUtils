package me.wyne.wutils.common.particle;

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
        String[] args = string.split(":");
        Color color = Color.fromRGB(Integer.parseInt(args[0]));
        float size = Float.parseFloat(args[1]);
        return new Particle.DustOptions(color, size);
    }

}
