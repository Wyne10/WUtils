package me.wyne.wutils.jdbc;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class OrmLiteConnectionPool implements ConnectionPool<ConnectionSource> {

    private final Logger logger;

    private final String url;
    private final String username;
    private final String password;

    private final JdbcPooledConnectionSource connectionSource = new JdbcPooledConnectionSource();
    private boolean isInitialized = false;

    public OrmLiteConnectionPool(String url, String username, String password, Logger logger) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.logger = logger;
        initializeDataSource();
    }

    private void initializeDataSource() {
        connectionSource.setUrl(url);
        connectionSource.setUsername(username);
        connectionSource.setPassword(password);
        try {
            connectionSource.initialize();
            isInitialized = true;
        } catch (SQLException e) {
            logger.error("An exception occurred trying to establish connection with {}", url, e);
        }
    }

    @Override
    public boolean isActive() {
        return isInitialized;
    }

    @Override
    public @Nullable Connection getConnection() {
        throw new NotImplementedException("OrmLiteConnectionPool doesn't provide java.sql connections");
    }

    @Override
    public @Nullable ConnectionSource getSource() {
        return connectionSource;
    }

    @Override
    public void close() {
        connectionSource.closeQuietly();
    }

}
