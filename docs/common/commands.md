# Commands

`CommandUtils` (`common/src/main/java/me/wyne/wutils/common/command/CommandUtils.java`) is the
entire `command/` package: two [CommandAPI](https://commandapi.jorel.dev/) player arguments that
**strip out target selectors**, plus a resolver for the offline one.

## Why it exists

CommandAPI's player arguments are built on Minecraft's entity-selector syntax, so their tab
completion offers `@a`, `@s`, `@p`, `@r` and `@e` alongside real player names. For a command that
means "pick a player", those selectors are noise at best and a source of surprising behaviour at
worst — `@a` is not a single player, and a command author who did not plan for selectors gets them
anyway.

Both helpers replace the suggestion provider with a plain list of player names, so the selectors
never appear in completion. `offlinePlayer` goes further and abandons the entity-selector argument
type entirely.

**Requires CommandAPI.** WUtils declares CommandAPI `compileOnly` — it is not bundled. A consumer
that calls into `CommandUtils` without adding CommandAPI to their own runtime classpath (or
shading it) will fail at class-load time. See the [module overview](common.md) for the full list
of optional third-party dependencies.

## onlinePlayer

`onlinePlayer(String nodeName)`
(`common/src/main/java/me/wyne/wutils/common/command/CommandUtils.java:27-31`) keeps CommandAPI's
`EntitySelectorArgument.OnePlayer` — so the argument still resolves and validates to a real online
`Player` server-side — but calls `replaceSuggestions` with the current online player names.

Note precisely what that changes: it replaces the *suggestions*, not the parser. Selectors stop
being offered in tab completion, which is what makes the argument read as "a player name". The
underlying argument type is unchanged, so a selector typed out by hand is still parsed by
CommandAPI.

## offlinePlayer / getOfflinePlayer

`offlinePlayer(String nodeName)`
(`common/src/main/java/me/wyne/wutils/common/command/CommandUtils.java:38-42`) takes the stronger
route: it is a plain `StringArgument`, not an entity selector at all, with suggestions listing
every known offline player name. Selector syntax has no meaning here — `@a` is just a string that
will fail to resolve.

The trade-off is that nothing validates the input for you. CommandAPI hands over whatever was
typed, which is why the resolver below exists.

To resolve that string, call `getOfflinePlayer(CommandArguments args, String nodeName)`
(`common/src/main/java/me/wyne/wutils/common/command/CommandUtils.java:51-60`). It looks the
argument's value up as a Bukkit player name and returns:

- `null` if the named argument is absent from `args`,
- `null` if the name doesn't resolve to a known UUID (`Bukkit.getPlayerUniqueId`),
- `null` if the resulting `OfflinePlayer` is not currently online and has never played on this
  server (`hasPlayedBefore()` is `false`),
- otherwise the resolved `OfflinePlayer`.

Pair this with [`PlayerUtils.exists`](players.md) if you need the same "is this a real player"
check outside of a command context.
