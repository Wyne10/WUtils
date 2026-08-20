# Driver loading

`DriverLibrary` and `DriverShim` let a plugin register a JDBC driver with
`java.sql.DriverManager` at runtime, downloaded from Maven Central on demand, instead
of bundling the driver jar at build time. This is independent of which `ConnectionPool`
implementation is in use (see [WUtils JDBC](jdbc.md)) — a pool still needs an actual
driver class registered for its JDBC URL's scheme (`jdbc:mysql:...`, `jdbc:h2:...`,
etc.) to connect at all, regardless of which pooling library sits on top. The usual
order is: call `registerDriver()` for the database in use, then construct a
`ConnectionPool`.

## `DriverLibrary`

An enum of known drivers, each carrying hardcoded Maven coordinates (`groupId`,
`artifactId`, `version`) and the driver's fully-qualified class name
(`jdbc/src/main/java/me/wyne/wutils/jdbc/DriverLibrary.java:27-64`):

| Constant | Maven artifact | Driver class |
|---|---|---|
| `NONE` | — | No-op placeholder, see below |
| `H2_V1` | `com.h2database:h2:1.4.200` | `org.h2.Driver` |
| `H2_V2` | `com.h2database:h2:2.4.240` | `org.h2.Driver` |
| `MYSQL` | `com.mysql:mysql-connector-j:26.7.0` | `com.mysql.cj.jdbc.NonRegisteringDriver` |
| `MARIADB` | `org.mariadb.jdbc:mariadb-java-client:3.5.10` | `org.mariadb.jdbc.Driver` |
| `POSTGRESQL` | `org.postgresql:postgresql:42.7.13` | `org.postgresql.Driver` |
| `SQLITE` | `org.xerial:sqlite-jdbc:3.53.2.1` | `org.sqlite.JDBC` |

`H2_V1` and `H2_V2` are separate constants, not one constant with a configurable
version, because H2 1.x and 2.x are incompatible major versions with different driver
jars — a consumer on an old H2 1.4 data file needs `H2_V1`, not the latest.

Versions are hardcoded per constant; there is no way to request a different version of
a given driver through this enum. Bumping a version means changing WUtils and releasing
a new `jdbc` version.

### `NONE`

`NONE("", "", "", "")` is a null-object constant: its coordinate fields are empty and
meaningless, and its `registerDriver()` is a permanent no-op — the constructor sets its
private `isRegistered` flag to `true` immediately for the constant named `"NONE"`
(`DriverLibrary.java:74-76`), so the download/registration body never runs. It exists
for call sites that store a `DriverLibrary` as configuration (e.g. "which driver does
this datasource use") and want a valid enum value for "no driver needed" rather than a
special `null` case.

### `registerDriver()`

`registerDriver()` is `synchronized` and, for every constant but `NONE`
(`DriverLibrary.java:140-147`):

1. Resolves the driver jar's location as
   `libraries/<groupId-path>/<artifactId>/<version>/<artifactId>-<version>.jar`, under
   the **JVM's working directory** — the server's run directory, not any plugin's data
   folder, and not configurable. If two plugins on the same server both bundle `jdbc`
   and register the same driver, they share this one file.
2. If that file doesn't already exist, downloads it from
   `https://repo1.maven.org/maven2/<same path>` into a temporary file created alongside
   it, copies the download into that temporary file, then moves the temporary file into
   place — preferring an atomic move and falling back to a plain move if the filesystem
   doesn't support one — and removes the temporary file in a `finally` block regardless
   of outcome (`getClassLoaderURL()`, `DriverLibrary.java:103-119`, with the move itself
   in `moveIntoPlace`, `DriverLibrary.java:121-127`). An interrupted download therefore
   leaves nothing behind at the final path — only a `.part` file that gets cleaned up —
   so a later call re-downloads cleanly instead of adopting a partial file. A dedicated
   test, `DriverLibraryTest.zzLeavesNoPartialDownloadsBehind`, checks this by driving
   every constant's `registerDriver()` and then asserting no `.part` files remain under
   `libraries/`.
3. Loads the driver class from the jar through a `URLClassLoader`, instantiates it via
   its no-arg constructor, wraps it in a `DriverShim`, and registers the shim with
   `DriverManager.registerDriver()` (`DriverLibrary.java:144-146`).
4. Sets `isRegistered = true` so later calls on the same constant become a no-op.

Being `synchronized` on the enum constant means concurrent callers on the same constant
can no longer both pass the `isRegistered` check, both download, or both register — the
whole method body runs under one lock per constant.

**The `URLClassLoader` is kept open for the JVM's lifetime**, stored in the constant's
`loader` field rather than closed after use (`DriverLibrary.java:70-72`). This is
deliberate, not a leak: the registered driver keeps loading supporting classes from its
own jar lazily, well after `registerDriver()` returns, and closing the loader would
break those later loads with `NoClassDefFoundError`. Keeping it open trades a
classloader (and the jar's classes) that lives for the rest of the process for a driver
that keeps working correctly.

Downloads are not integrity-checked: there is no checksum comparison against Maven
Central, so a jar that downloads completely but is the wrong artifact, or a tampered
file at the source, is still accepted as the driver jar with no way for
`registerDriver()` to detect it. The temp-file-and-move scheme above only rules out
*truncated* downloads being mistaken for valid jars — it says nothing about a
complete-but-wrong one.

The method's signature surfaces every failure mode directly to the caller — it declares
`throws IOException, ClassNotFoundException, SQLException, NoSuchMethodException,
InvocationTargetException, InstantiationException, IllegalAccessException` — network
failures, reflection failures, and registration failures are all checked exceptions the
caller must handle.

### Sharp edge: overlapping URL schemes

Nothing stops two registered drivers from claiming the same JDBC URL scheme. `H2_V1`
and `H2_V2` both load `org.h2.Driver` and both answer for `jdbc:h2:` URLs; MariaDB's
driver has historically also accepted `jdbc:mysql:` URLs for compatibility. Once two
such drivers are registered, which one actually serves a given URL is whichever
`DriverManager` reaches first when iterating its registered drivers — not something
`DriverLibrary` controls or exposes a way to influence.
`DriverLibraryTest.zzBothH2ConstantsClaimTheSameUrlScheme` registers both H2 constants
and pins that `DriverManager.getDriver("jdbc:h2:mem:probe")` resolves to *a* driver that
accepts the URL, without asserting which constant it came from — that ambiguity is the
point of the test.

## Why `DriverShim` exists

`DriverManager` only accepts a driver from a caller if the driver class — resolved
through *that caller's* classloader — is the exact same `Class` object as the one
`DriverManager` has registered. A driver loaded through the `URLClassLoader` in
`registerDriver()` fails that check for any code outside that classloader, making it
invisible to `DriverManager.getConnection()` even though it registered without error.

`DriverShim` (`jdbc/src/main/java/me/wyne/wutils/jdbc/DriverShim.java:24-67`) works
around this: it is a `Driver` implementation loaded by the plugin's own classloader (not
the `URLClassLoader` that loaded the driver jar), so it passes the identity check.
Every `Driver` method on it just forwards to the wrapped real driver instance —
`connect` (line 33), `acceptsURL` (line 38), `getPropertyInfo` (line 43),
`getMajorVersion`/`getMinorVersion` (lines 48-54), `jdbcCompliant` (line 58), and
`getParentLogger` (line 63). It imposes no behavior of its own: nullability on its
methods mirrors whatever `DriverManager` passes through, and `DriverShimTest` verifies
this purely against a hand-written recording `Driver`, with no real driver jar or
network access involved — including that a `null` `Properties` passed to `connect`
reaches the wrapped driver unchanged.

## See also

- [WUtils JDBC](jdbc.md) — the `ConnectionPool` abstraction this driver registration is
  normally a prerequisite for.
