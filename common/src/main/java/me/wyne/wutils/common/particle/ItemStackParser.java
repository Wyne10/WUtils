package me.wyne.wutils.common.particle;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

public class ItemStackParser implements StringDataParser<ItemStack> {

    private final static Collection<String> suggestions = Arrays.stream(Material.values()).map(Enum::toString).toList();

    @Override
    public Collection<String> getSuggestions() {
        return suggestions;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public ItemStack getData(String string) {
        return new ItemStack(Material.matchMaterial(string));
    }

    @Override
    public String toString(Object data) {
        return ((ItemStack)data).getType().name();
    }

}
