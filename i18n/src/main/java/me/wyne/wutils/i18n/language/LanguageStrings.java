package me.wyne.wutils.i18n.language;

import java.util.List;

public interface LanguageStrings {

    boolean contains(String path);

    boolean isList(String path);

    List<String> getStringList(String path);

}
