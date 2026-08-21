# WUtils JDBC

`jdbc` is a small abstraction over JDBC connection pooling, plus a way to pull an
actual JDBC driver onto the classpath at runtime instead of bundling one. It has two
independent halves: the `ConnectionPool` interface and its three implementations (this
page), and runtime driver download/registration via `DriverLibrary`/`DriverShim` (see
[Driver loading](driver-loading.md)).

- Directory: `jdbc/`
- Gradle project: `:WUtils-jdbc`
- Maven artifact: `io.github.wyne10:wutils-jdbc`
- Version: `2.0.0`
- Root package: `me.wyne.wutils.jdbc`

Source of these facts: `jdbc/build.gradle`.

## Dependencies

| Dependency | Scope | Notes |
|---|---|---|
| `com.zaxxer:HikariCP:7.1.0` | `compileOnly` | `HikariConnectionPool`, `HikariOrmLiteConnectionPool` |
| `com.j256.ormlite:ormlite-jdbc:6.1` | `compileOnly` | `OrmLiteConnectionPool`, `HikariOrmLiteConnectionPool` |

Both are `compileOnly` — the consumer must put the jar(s) their chosen implementation
needs on the runtime classpath themselves; neither is bundled. `HikariConnectionPool`
only needs HikariCP; `OrmLiteConnectionPool` only needs ORMLite;
`HikariOrmLiteConnectionPool` needs both.

`jdbc` has **no dependency on any other WUtils module** and, like `json`, **no
dependency on Bukkit/Paper** — it is plain Java and works in any JVM context.

## Package inventory

The whole module is one package, `me.wyne.wutils.jdbc`, six classes:

| Class | Role |
|---|---|
| `ConnectionPool<T>` | The abstraction: `isActive()`, `getConnection()`, `getSource()`, `AutoCloseable`. `jdbc/src/main/java/me/wyne/wutils/jdbc/ConnectionPool.java` |
| `HikariConnectionPool` | Raw HikariCP pool. `jdbc/src/main/java/me/wyne/wutils/jdbc/HikariConnectionPool.java` |
| `OrmLiteConnectionPool` | ORMLite `ConnectionSource` on its own built-in pool. `jdbc/src/main/java/me/wyne/wutils/jdbc/OrmLiteConnectionPool.java` |
| `HikariOrmLiteConnectionPool` | ORMLite `ConnectionSource` backed by a Hikari pool. `jdbc/src/main/java/me/wyne/wutils/jdbc/HikariOrmLiteConnectionPool.java` |
| `DriverLibrary` | Enum of known drivers; downloads and registers them. See [Driver loading](driver-loading.md). |
| `DriverShim` | `Driver` delegate used to make a runtime-loaded driver visible to `DriverManager`. See [Driver loading](driver-loading.md). |

## The `ConnectionPool<T>` abstraction

`ConnectionPool<T>` (`jdbc/src/main/java/me/wyne/wutils/jdbc/ConnectionPool.java`)
is a small, uniform wrapper over a pooling library, extending `AutoCloseable` so every
implementation works in try-with-resources:

- **`isActive()`** — whether the underlying pool or connection source is
  currently running.
- **`getConnection()`** — `@NotNull Connection`, throws `SQLException`. Not
  every implementation supports this; the interface JavaDoc says to check the
  implementing class first, and `OrmLiteConnectionPool` is the case where that matters
  (see below).
- **`getSource()`** — `@NotNull T`, the typed escape hatch to whatever the
  implementation wraps underneath. `getSource()` is `@NotNull` on the interface: every
  implementation always has a source to return, so callers never need a null check
  before using it. `T` is what distinguishes the three implementations: a raw data
  source, an ORMLite connection source, or the same connection source bridged onto
  Hikari.

## Choosing an implementation

| Class | `getSource()` returns | `getConnection()` | Requires |
|---|---|---|---|
| `HikariConnectionPool` | `HikariDataSource` | Delegates to Hikari | HikariCP |
| `OrmLiteConnectionPool` | ORMLite `ConnectionSource` | **Always throws** `UnsupportedOperationException` | ORMLite |
| `HikariOrmLiteConnectionPool` | ORMLite `ConnectionSource` (backed by Hikari) | Delegates to Hikari | HikariCP + ORMLite |

- **`HikariConnectionPool`** (`jdbc/src/main/java/me/wyne/wutils/jdbc/HikariConnectionPool.java`)
  is a plain HikariCP pool for code that wants raw `java.sql.Connection`s and nothing
  else.
- **`OrmLiteConnectionPool`** (`jdbc/src/main/java/me/wyne/wutils/jdbc/OrmLiteConnectionPool.java`)
  wraps ORMLite's own `JdbcPooledConnectionSource` — use it when the caller works
  entirely through ORMLite's DAO layer against the `ConnectionSource` and never needs a
  raw `Connection`.
- **`HikariOrmLiteConnectionPool`** (`jdbc/src/main/java/me/wyne/wutils/jdbc/HikariOrmLiteConnectionPool.java`)
  is for callers who want ORMLite's `ConnectionSource` API *and* Hikari's pooling
  underneath, bridging a `HikariDataSource` into ORMLite's `DataSourceConnectionSource`
  (line 67). It also supports `getConnection()`, unlike the plain ORMLite
  implementation, because the connections still ultimately come from Hikari.

Every implementation exposes the same four public entry points: two constructors — a
plain one taking `(url, username, password)`, and one additionally taking a
configurator — and two `initializeDataSource` overloads with the same shape, described
next. `HikariConnectionPool`'s constructors never throw a checked exception; the two
ORMLite-backed classes both declare `throws SQLException`, since building their
`ConnectionSource` can fail.

## Configuring the underlying data source

Each plain constructor delegates to the configurator constructor with a no-op
configurator; each no-arg `initializeDataSource()` delegates to the
`initializeDataSource(Consumer)` overload the same way. So there is really one
constructor and one `initializeDataSource` method per class, and the simpler overloads
just supply "do nothing further."

`initializeDataSource(Consumer)` applies the URL and credentials the instance was built
with, then hands the underlying object to the configurator for anything the constructor
doesn't expose — pool sizing, timeouts, `autoCommit`, and so on.

**The configurator's type is the concrete object being configured, not the interface's
`T`.** For the two ORMLite-backed classes, those are different types:

| Class | Configurator type | Same as `getSource()`'s `T`? |
|---|---|---|
| `HikariConnectionPool` | `Consumer<HikariDataSource>` | Yes |
| `OrmLiteConnectionPool` | `Consumer<JdbcPooledConnectionSource>` | No — `getSource()` returns `ConnectionSource` |
| `HikariOrmLiteConnectionPool` | `Consumer<HikariDataSource>` | No — `getSource()` returns `ConnectionSource` |

This is the single most confusing thing about the API: `getSource()` always returns
what the interface promises (a `ConnectionSource` for both ORMLite classes), but the
configurator sees the richer, concrete object underneath — `JdbcPooledConnectionSource`
for `OrmLiteConnectionPool` (a `ConnectionSource` subtype with setters `getSource()`'s
return type doesn't expose), and the `HikariDataSource` being pooled for
`HikariOrmLiteConnectionPool` (not an ORMLite type at all — that class's own
`getSource()` returns the `ConnectionSource` built *over* it). Reach for the
configurator when you need to set something `getSource()`'s type can't reach; reach for
`getSource()` for everything else.

## Ordering and sealing

The configurator always runs **before** the object it configures is put to use, so the
caller's settings take effect:

- `OrmLiteConnectionPool` runs it before calling `connectionSource.initialize()`
  (`jdbc/src/main/java/me/wyne/wutils/jdbc/OrmLiteConnectionPool.java:64-70`).
- `HikariOrmLiteConnectionPool` runs it before building the `DataSourceConnectionSource`
  over the data source
  (`jdbc/src/main/java/me/wyne/wutils/jdbc/HikariOrmLiteConnectionPool.java:62-67`).

But building that `ConnectionSource` starts the underlying Hikari pool, and HikariCP
seals a data source's configuration once it has started — any further attempt to change
it throws `IllegalStateException`. This has different practical consequences per class:

- **`HikariOrmLiteConnectionPool`** — constructing the `ConnectionSource` opens a
  connection (ORMLite needs one to detect the database type), which starts the Hikari
  pool immediately. So the pool is already sealed by the time construction returns, and
  calling the `initializeDataSource(Consumer)` **method** afterward always throws
  `IllegalStateException`. The constructor's configurator overload is the only usable
  hook for this class.
- **`HikariConnectionPool`** — HikariCP connects lazily here, so the pool doesn't start
  until the first connection is actually handed out. The `initializeDataSource(Consumer)`
  method keeps working after construction, right up until that first `getConnection()`
  call.
- **`OrmLiteConnectionPool`** doesn't sit on Hikari at all, so this sealing behavior
  doesn't apply to it; its own `initializeDataSource` re-runs `initialize()` each time
  and is not similarly restricted.

## Sharp edge: `OrmLiteConnectionPool.getConnection()`

`OrmLiteConnectionPool.getConnection()`
(`jdbc/src/main/java/me/wyne/wutils/jdbc/OrmLiteConnectionPool.java:82-85`) **always
throws `UnsupportedOperationException`** — not `SQLException`, despite the interface
method declaring `throws SQLException` and despite the name suggesting a normal failure
mode. ORMLite manages `Connection` instances internally through its own
`ConnectionSource` rather than exposing them, so this method has nothing to return; it
exists only because the class implements `ConnectionPool<ConnectionSource>`. A caller
coding directly against the `ConnectionPool` interface — rather than against
`OrmLiteConnectionPool` specifically — needs to know this before calling
`getConnection()`, since a `catch (SQLException e)` around it will not catch anything:
use `getSource()` instead, or pick `HikariConnectionPool` /
`HikariOrmLiteConnectionPool` if a raw `Connection` is actually needed.

## Tests

`jdbc` is the only module with a test suite:
`jdbc/src/test/java/me/wyne/wutils/jdbc/`, five classes plus the shared `H2Support`
helper (fresh in-memory H2 URLs, one per test).

- **`HikariConnectionPoolTest`**, **`OrmLiteConnectionPoolTest`**,
  **`HikariOrmLiteConnectionPoolTest`** exercise each pool implementation against
  in-memory H2 — connection handling, `getSource()`, `isActive()`, the `autoCommit`
  default, configurator ordering, and the sealing behavior described above.
- **`DriverShimTest`** exercises `DriverShim` against a hand-written recording `Driver`,
  with no real driver or network involved.
- **`DriverLibraryTest`** is tagged `network`: it genuinely downloads driver jars from
  Maven Central, including a test pinning the overlapping-URL-scheme behavior described
  in [Driver loading](driver-loading.md).

Run with `./gradlew :WUtils-jdbc:test` — this excludes the `network`-tagged tests, so it
needs no network access and no running database server. Add
`-PnetworkTests` to include `DriverLibraryTest` as well
(`jdbc/build.gradle`). Either way, the test task's working directory is set to
`build/test-work`, so anything `DriverLibrary` downloads during a test run lands there
instead of creating a `libraries/` directory in the project root.

## See also

- [Driver loading](driver-loading.md) — `DriverLibrary` and `DriverShim`, and how
  registering a driver relates to building a pool against it.
- [WUtils Json](../json/json.md) — the other plain-Java, no-Bukkit module, for
  comparison.
