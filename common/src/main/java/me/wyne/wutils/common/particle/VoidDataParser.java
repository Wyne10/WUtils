package me.wyne.wutils.common.particle;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class VoidDataParser implements StringDataParser<Void> {

    @Override
    public Collection<String> getSuggestions() {
        return Collections.emptyList();
    }

    @Override
    @Nullable
    public Void getData(String string) {
        return null;
    }

}
