package me.wyne.wutils.common.strings;

import org.jetbrains.annotations.NotNull;

public final class StringUtils {

    private static final int[] ROMAN_VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final String[] ROMAN_NUMERALS = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    /**
     * Converts {@code number} to Roman numerals, e.g. {@code 4} → {@code "IV"}, {@code 1994} →
     * {@code "MCMXCIV"}. Numbers above 3999 are written with repeated {@code M}s.
     *
     * @return an empty string if {@code number} is zero or negative
     */
    public static @NotNull String toRoman(int number) {
        var result = new StringBuilder();
        for (int i = 0; i < ROMAN_VALUES.length; i++) {
            while (number >= ROMAN_VALUES[i]) {
                number -= ROMAN_VALUES[i];
                result.append(ROMAN_NUMERALS[i]);
            }
        }
        return result.toString();
    }

}
