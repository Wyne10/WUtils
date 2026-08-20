package me.wyne.wutils.jdbc;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstraction over a pooled JDBC connection or ORM connection source.
 *
 * <p>Implementations wrap a specific backing library (HikariCP, ORMLite) and expose it
 * uniformly. Not every implementation is able to hand out a raw {@link Connection}; check
 * the implementing class's documentation before calling {@link #getConnection()}.
 */
public interface ConnectionPool<T> extends AutoCloseable {

    /**
     * Returns whether the underlying pool or connection source is currently running.
     */
    boolean isActive();

    /**
     * Obtains a connection from the pool.
     *
     * <p>Not every implementation supports this; see the implementing class's documentation.
     */
    @NotNull Connection getConnection() throws SQLException;

    /**
     * Returns the pool's underlying data source or connection source. The concrete type
     * depends on the implementation (e.g. {@code HikariDataSource}, ORMLite's
     * {@code ConnectionSource}).
     */
    @NotNull T getSource();

}
