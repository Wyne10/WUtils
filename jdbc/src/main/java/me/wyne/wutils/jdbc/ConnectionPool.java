package me.wyne.wutils.jdbc;

import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionPool<T> extends AutoCloseable {

    boolean isActive();

    Connection getConnection() throws SQLException;

    @Nullable T getSource();

}
