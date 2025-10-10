package me.wyne.wutils.common.particle;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.Arrays;
import java.util.Collection;

public class BlockDataParser implements StringDataParser<BlockData> {

    private final static Collection<String> suggestions = Arrays.stream(Material.values())
            .filter(Material::isBlock)
            .map(Enum::toString).toList();

    @Override
    public Collection<String> getSuggestions() {
        return suggestions;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public BlockData getData(String string) {
        return Material.matchMaterial(string).createBlockData();
    }

}
