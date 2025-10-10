package me.wyne.wutils.common.particle;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface StringDataParser<T> {

     Collection<String> getSuggestions();
     @Nullable T getData(String string);

}
