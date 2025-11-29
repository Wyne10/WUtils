package me.wyne.wutils.common.particle;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collection;

public class MaterialParser implements StringDataParser<Material> {

    private final static Collection<String> suggestions = Arrays.stream(Material.values()).map(Enum::toString).toList();

    @Override
    public Collection<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public Material getData(String string) {
        return Material.matchMaterial(string);
    }

    @Override
    public String toString(Object data) {
        return ((Material)data).name();
    }

}
