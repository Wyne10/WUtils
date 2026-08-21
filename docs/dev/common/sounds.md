# Sounds, Randomness and Placeholders

Three unrelated one- or two-method packages, merged onto a single page rather than left as stubs:
`sound/` (`SoundUtils`), `random/` (`RandomUtils`), and `placeholder/` (`PAPIUtils`).

## SoundUtils

`SoundUtils` (`common/src/main/java/me/wyne/wutils/common/sound/SoundUtils.java`) converts a
legacy `org.bukkit.Sound` enum constant into an Adventure `net.kyori.adventure.sound.Sound`, for
APIs (like Adventure's `Audience.playSound`) that expect the latter. Five overloads, all
delegating to the five-argument form
(`common/src/main/java/me/wyne/wutils/common/sound/SoundUtils.java:11-33`):

- `getSound(Sound)` — `Sound.Source.MASTER`, volume `1f`, pitch `1f`.
- `getSound(Sound, Sound.Source)` — volume `1f`, pitch `1f`.
- `getSound(Sound, float volume)` — `Sound.Source.MASTER`, pitch `1f`.
- `getSound(Sound, float volume, float pitch)` — `Sound.Source.MASTER`.
- `getSound(Sound, Sound.Source, float volume, float pitch)` — the full form; builds the
  Adventure `Sound` from the legacy sound's key string.

## RandomUtils.weightedRandom

`weightedRandom(Map<K, V extends Number> map)`
(`common/src/main/java/me/wyne/wutils/common/random/RandomUtils.java:17-37`) picks a random entry
from `map`, where each value is that entry's selection weight. Entries with a non-positive weight
are excluded from selection (their weight does not count toward the total, and they cannot be
picked) but are not removed from consideration otherwise.

**Returns `null`** if `map` is empty, or if every weight in it is zero or negative (total weight
`<= 0`) — callers must check for `null` rather than assuming an entry is always returned.
Otherwise it draws uniformly from `[0, totalWeight)` via `ThreadLocalRandom` and walks the map's
entries to find the one the draw landed in.

## PAPIUtils

`PAPIUtils` (`common/src/main/java/me/wyne/wutils/common/placeholder/PAPIUtils.java`) has one
method: `getPlaceholder(String identifier, String params)`
(`common/src/main/java/me/wyne/wutils/common/placeholder/PAPIUtils.java:13-15`), which joins its
two arguments as `identifier + "_" + params` — the body of a PlaceholderAPI placeholder, without
the surrounding `%...%` delimiters.

**Only meaningful with PlaceholderAPI.** This method itself has no PlaceholderAPI dependency (it's
plain string concatenation), but its purpose only makes sense in the context of registering a
PlaceholderAPI expansion, which requires PlaceholderAPI as a `compileOnly` dependency the consumer
must supply. See the [module overview](common.md) for the full list of optional integrations.
