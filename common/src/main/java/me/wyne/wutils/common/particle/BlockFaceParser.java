package me.wyne.wutils.common.particle;

import org.bukkit.block.BlockFace;

import java.util.Arrays;
import java.util.Collection;

public class BlockFaceParser implements StringDataParser<BlockFace> {

    private final static Collection<String> suggestions = Arrays.stream(BlockFace.values()).map(Enum::toString).toList();

    @Override
    public Collection<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public BlockFace getData(String string) {
        return BlockFace.valueOf(string);
    }

}
