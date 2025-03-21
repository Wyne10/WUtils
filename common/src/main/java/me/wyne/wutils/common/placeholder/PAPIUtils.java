package me.wyne.wutils.common.placeholder;

public final class PAPIUtils {

    public static String getPlaceholder(String identifier, String params) {
        return identifier + "_" + params;
    }

}
