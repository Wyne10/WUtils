package me.wyne.wutils.i18n.language;

import java.io.File;
import java.util.Locale;
import java.util.Map;

public interface Language {
    String getLanguageCode();

    Locale getLocale();

    File getLanguageFile();

    LanguageStrings getStrings();

    Map<String, String> getStringMap();
}
