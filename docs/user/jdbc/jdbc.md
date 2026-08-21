# Databases

`wutils-jdbc` gives you a pooled database connection without tying you to one pooling
library, plus a way to pull an actual JDBC driver jar onto the classpath at runtime
instead of bundling one yourself. Reach for it when your plugin needs to talk to MySQL,
MariaDB, PostgreSQL, SQLite or H2 and you don't want to hand-roll connection pooling or
ship driver jars in your own repo.

It does not do anything ORM-shaped on its own — it hands you either a raw
`java.sql.Connection` or an ORMLite `ConnectionSource`, and you take it from there.

## Adding it

```kotlin
dependencies {
    implementation("io.github.wyne10:wutils-jdbc:2.0.0")
}
```

`wutils-jdbc` has no dependency on any other WUtils module and no dependency on
Bukkit/Paper — it's plain Java and works outside a plugin context too.

You also need the pooling library your chosen implementation uses, as your own
dependency — WUtils declares both `compileOnly`, so neither ships inside
`wutils-jdbc`'s jar:

| Dependency | Needed by |
|---|---|
| `com.zaxxer:HikariCP:7.1.0` | `HikariConnectionPool`, `HikariOrmLiteConnectionPool` |
| `com.j256.ormlite:ormlite-jdbc:6.1` | `OrmLiteConnectionPool`, `HikariOrmLiteConnectionPool` |

Pick one row if you only need one implementation, or both if you're using
`HikariOrmLiteConnectionPool`. See [Getting Started](../getting-started.md#the-compileonly-model--read-this-before-you-deploy)
for what happens if you forget.

## Getting a connection pool

Three implementations of `ConnectionPool<T>`, all constructed the same way — a URL,
username and password, with an optional trailing configurator:

```java
ConnectionPool<HikariDataSource> pool = new HikariConnectionPool(
        "jdbc:mysql://localhost:3306/mydb", "user", "pass");

try (Connection connection = pool.getConnection()) {
    // use the connection
}

pool.close();
```

| Class | `getSource()` returns | `getConnection()` | Needs |
|---|---|---|---|
| `HikariConnectionPool` | `HikariDataSource` | Works | HikariCP |
| `OrmLiteConnectionPool` | ORMLite `ConnectionSource` | **Always throws** `UnsupportedOperationException` | ORMLite |
| `HikariOrmLiteConnectionPool` | ORMLite `ConnectionSource`, backed by Hikari | Works | HikariCP + ORMLite |

- Pick **`HikariConnectionPool`** if you just want raw `java.sql.Connection`s.
- Pick **`OrmLiteConnectionPool`** if you work entirely through ORMLite's DAO layer and
  never touch a raw `Connection`.
- Pick **`HikariOrmLiteConnectionPool`** if you want ORMLite's DAO layer *and* Hikari's
  pooling underneath.

Every implementation is `AutoCloseable`, so a try-with-resources works for the pool
itself, not just individual connections.

### Configuring pool settings

The plain constructor is shorthand for the same constructor with a no-op configurator.
Pass your own to reach settings the constructor doesn't expose — pool size, timeouts,
`autoCommit`, and so on:

```java
ConnectionPool<HikariDataSource> pool = new HikariConnectionPool(
        "jdbc:mysql://localhost:3306/mydb", "user", "pass",
        dataSource -> {
            dataSource.setMaximumPoolSize(10);
            dataSource.setAutoCommit(false);
        });
```

`HikariConnectionPool`'s constructors never throw a checked exception. Both
ORMLite-backed classes declare `throws SQLException`, since building their
`ConnectionSource` can fail.

**The configurator sees the concrete underlying object, not what `getSource()`
returns.** For `OrmLiteConnectionPool` the configurator receives
`JdbcPooledConnectionSource` (a superset of the `ConnectionSource` `getSource()` gives
you back); for `HikariOrmLiteConnectionPool` it receives the `HikariDataSource` being
pooled underneath — not an ORMLite type at all. Reach for the configurator when you need
a setting `getSource()`'s type can't expose; reach for `getSource()` for everything else.

### Sharp edge: `OrmLiteConnectionPool.getConnection()` always throws

Calling `getConnection()` on `OrmLiteConnectionPool` throws
`UnsupportedOperationException` — not `SQLException` — every single time, regardless of
pool state. ORMLite manages connections internally through its own `ConnectionSource`
and never exposes a raw `Connection`, so this method only exists to satisfy the
`ConnectionPool<T>` interface. If you're coding against `ConnectionPool` generically
rather than against `OrmLiteConnectionPool` directly, a `catch (SQLException e)` around
`getConnection()` will **not** catch this. Use `getSource()` instead, or pick
`HikariConnectionPool`/`HikariOrmLiteConnectionPool` if you actually need a raw
`Connection`.

### Sharp edge: once a Hikari-backed pool starts, its config is sealed

`HikariOrmLiteConnectionPool` opens a connection while building its `ConnectionSource`
(ORMLite needs one to detect the database type), which starts the underlying Hikari pool
immediately — so by the time the constructor returns, calling
`initializeDataSource(configurator)` again always throws `IllegalStateException`. The
constructor's configurator argument is your only usable hook for this class.

`HikariConnectionPool` connects lazily, so `initializeDataSource(configurator)` keeps
working after construction, right up until the first `getConnection()` call actually
hands out a connection. `OrmLiteConnectionPool` doesn't sit on Hikari at all, so this
sealing behavior doesn't apply to it — its `initializeDataSource` can be called again
freely.

## Downloading a driver at runtime

Rather than shading a JDBC driver jar into your plugin, `DriverLibrary` can download one
from Maven Central at runtime and register it with `java.sql.DriverManager`:

```java
DriverLibrary.MYSQL.registerDriver();

ConnectionPool<HikariDataSource> pool = new HikariConnectionPool(
        "jdbc:mysql://localhost:3306/mydb", "user", "pass");
```

Register the driver **before** constructing a pool against a URL that needs it —
whichever pooling library you use still needs an actual driver class registered for the
URL's scheme (`jdbc:mysql:`, `jdbc:h2:`, ...) to connect at all.

| Constant | Driver |
|---|---|
| `NONE` | No-op placeholder — use as a config default meaning "no driver needed" |
| `H2_V1` | H2 1.4.x (for old H2 1.x data files) |
| `H2_V2` | H2 2.x |
| `MYSQL` | MySQL |
| `MARIADB` | MariaDB |
| `POSTGRESQL` | PostgreSQL |
| `SQLITE` | SQLite |

`registerDriver()` is a no-op after the first successful call on a given constant, and
safe to call from multiple threads — it's synchronized per constant. It downloads to
`libraries/<groupId>/<artifactId>/<version>/` under the **JVM's working directory** (the
server's run directory, not your plugin's data folder, and not configurable). If two
plugins on the same server both register the same driver, they share that one file.

`registerDriver()` declares a wide `throws` clause — `IOException`,
`ClassNotFoundException`, `SQLException`, `NoSuchMethodException`,
`InvocationTargetException`, `InstantiationException`, `IllegalAccessException` — because
network, reflection and registration failures are all surfaced to you as checked
exceptions rather than being wrapped.

### Sharp edge: no checksum verification

Downloads aren't checked against Maven Central beyond completing successfully. There's no
checksum comparison, so a jar that downloads in full but is the wrong artifact — or a
tampered file at the source — is accepted as the driver with no way for
`registerDriver()` to detect it. An *interrupted* download is handled safely (it's
downloaded to a temp file and only moved into place on success, so a later call
re-downloads cleanly instead of adopting a partial jar) — but that only rules out
truncated files, not wrong-but-complete ones.

### Sharp edge: two drivers can claim the same URL scheme

Nothing stops you from registering two drivers that both answer for the same JDBC URL
scheme. `H2_V1` and `H2_V2` both load `org.h2.Driver` and both claim `jdbc:h2:`; MariaDB's
driver has historically also accepted `jdbc:mysql:` URLs for compatibility. If you
register both, which one actually serves a connection is whichever `DriverManager`
happens to reach first while iterating its registered drivers — not something
`DriverLibrary` controls or lets you influence. Register only the driver(s) you actually
need for the database you're targeting.

## See also

- [Getting Started](../getting-started.md) — the `compileOnly` model in general, and how
  shading applies to this module.
- [JSON Storage](../json/json.md) — the other plain-Java, no-Bukkit module, if you need
  simpler file-backed persistence instead of a database.
