package me.wyne.wutils.common.player;

import org.bukkit.entity.Player;

public final class PlayerUtils {

    /**
     * <pre>
     * One can determine how much experience has been collected to reach a level using the equations:
     *
     * ```
     * Total experience =
     * level^2 + 6 × level (at levels 0–16)
     * 2.5 × level^2 – 40.5 × level + 360 (at levels 17–31)
     * 4.5 × level^2 – 162.5 × level + 2220 (at levels 32+)
     * ```
     * </pre>
     */
    public static int levelToExp(int level) {
        if (level <= 16)
            return (level * level) + 6 * level;
        else if (level <= 31)
            return (int) (2.5 * (level * level) - 40.5 * level + 360);
        else
            return (int) (4.5 * (level * level) - 162.5 * level + 2220);
    }

    public static int expToLevel(int exp) {
        if (exp <= 0) return 0;

        // --- Range 0–16: xp = L² + 6L ---
        // Solve L² + 6L − xp = 0
        int max16 = 16 * 16 + 6 * 16; // 352
        if (exp <= max16) {
            return (int) Math.floor(
                    (-6 + Math.sqrt(36 + 4 * exp)) / 2
            );
        }

        // --- Range 17–31: xp = 2.5L² − 40.5L + 360 ---
        // Solve 2.5L² − 40.5L + (360 - xp) = 0
        int max31 = (int) (2.5 * 31 * 31 - 40.5 * 31 + 360); // 1507
        if (exp <= max31) {
            return (int) Math.floor(
                    (40.5 + Math.sqrt(40.5 * 40.5 - 10 * (360 - exp))) / 5
            );
        }

        // --- Range 32+: xp = 4.5L² − 162.5L + 2220 ---
        // Solve 4.5L² − 162.5L + (2220 - xp) = 0
        return (int) Math.floor(
                (162.5 + Math.sqrt(162.5 * 162.5 - 18 * (2220 - exp))) / 9
        );
    }

    /**
     * <pre>
     * The formulas for figuring out how many experience orbs needed to get to the next level are as follows:
     *
     * ```
     * Experience required =
     * 2 × current_level + 7 (for levels 0–15)
     * 5 × current_level – 38 (for levels 16–30)
     * 9 × current_level – 158 (for levels 31+)
     * ```
     * </pre>
     */
    public static int expToLevelUp(int currentLevel) {
        if (currentLevel <= 15)
            return 2 * currentLevel + 7;
        else if (currentLevel <= 30)
            return 5 * currentLevel - 38;
        else
            return 9 * currentLevel - 158;
    }

    public static int currentExp(Player player) {
        var currentExp = 0;
        currentExp += levelToExp(player.getLevel());
        currentExp += Math.round(expToLevelUp(player.getLevel()) * player.getExp());
        return currentExp;
    }

}
