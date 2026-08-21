# Players

`PlayerUtils` (`common/src/main/java/me/wyne/wutils/common/player/PlayerUtils.java`) is the
entire `player/` package: vanilla experience-level/point conversions, and an offline-player
existence check.

## Experience conversions

Vanilla Minecraft's level/experience-point relationship is piecewise, split at levels 16/17 and
30/31/32. `PlayerUtils` implements both directions plus the derived helpers `common/`-wide code
relies on:

- `levelToExp(int level)`
  (`common/src/main/java/me/wyne/wutils/common/player/PlayerUtils.java:22-29`) — total accumulated
  experience points needed to reach `level` from zero.
- `expToLevel(int exp)`
  (`common/src/main/java/me/wyne/wutils/common/player/PlayerUtils.java:31-57`) — inverse of the
  above: the level reached by `exp` total points. Returns `0` for `exp <= 0`.
- `expToLevelUp(int currentLevel)`
  (`common/src/main/java/me/wyne/wutils/common/player/PlayerUtils.java:71-78`) — how many
  experience points are needed to go from `currentLevel` to `currentLevel + 1`.
- `currentExp(Player player)`
  (`common/src/main/java/me/wyne/wutils/common/player/PlayerUtils.java:81-86`) — converts
  `player`'s current level plus fractional progress (`player.getExp()`) into a total point count,
  via `levelToExp(level) + round(expToLevelUp(level) * exp)`.
- `setExp(Player player, int exp)`
  (`common/src/main/java/me/wyne/wutils/common/player/PlayerUtils.java:89-93`) — resets the player
  to level 0 and grants `exp` total points via `Player.giveExp`.
- `addExp(Player player, int exp)`
  (`common/src/main/java/me/wyne/wutils/common/player/PlayerUtils.java:96-98`) — adds `exp` on top
  of the player's current total, via `setExp(player, currentExp(player) + exp)`. Note this
  round-trips through `setExp`, so it briefly resets the player to level 0 before regranting the
  combined total.

All four formula methods are pure integer/float math with no Bukkit calls — the piecewise
boundaries and coefficients match vanilla's published level curve.

## exists

`exists(OfflinePlayer player)`
(`common/src/main/java/me/wyne/wutils/common/player/PlayerUtils.java:101-103`) returns `true` if
`player` is currently online, or has played on the server before (`hasPlayedBefore()`). This is
the same "is this a real player" check `CommandUtils.getOfflinePlayer` applies inline — see
[Commands](commands.md).
