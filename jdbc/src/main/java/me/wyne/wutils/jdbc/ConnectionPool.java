package me.wyne.wutils.jdbc;

import org.jetbrains.annotations.Nullable;

import java.sql.Connection;

public interface ConnectionPool<T> extends AutoCloseable {

    boolean isActive();

    @Nullable Connection getConnection();

    @Nullable T getSource();

}
