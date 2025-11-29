package me.wyne.wutils.common.particle;

import java.util.ArrayList;
import java.util.Collection;

public class IntegerParser implements StringDataParser<Integer> {

    private final static Collection<String> suggestions = new ArrayList<>() { {add("<int>");} };

    @Override
    public Collection<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public Integer getData(String string) {
        return Integer.valueOf(string);
    }

    @Override
    public String toString(Object data) {
        return String.valueOf((int)data);
    }

}
