package me.wyne.wutils.common.particle;

import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.Collection;

@SuppressWarnings("deprecation")
public class PotionParser implements StringDataParser<Potion> {

    private final static Collection<String> suggestions = Arrays.stream(PotionType.values()).map(Enum::toString).toList();

    @Override
    public Collection<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public Potion getData(String string) {
        return new Potion(PotionType.valueOf(string));
    }

    @Override
    public String toString(Object data) {
        return ((Potion)data).getType().name();
    }

}
