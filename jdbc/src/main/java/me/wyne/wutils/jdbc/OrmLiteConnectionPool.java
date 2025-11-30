package me.wyne.wutils.jdbc;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

public class OrmLiteConnectionPool implements ConnectionPool<ConnectionSource> {

    private final String url;
    private final String username;
    private final String password;

    private final JdbcPooledConnectionSource connectionSource = new JdbcPooledConnectionSource();
    private boolean isInitialized = false;

    public OrmLiteConnectionPool(String url, String username, String password) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;
        initializeDataSource();
    }

    private void initializeDataSource() throws SQLException{
        connectionSource.setUrl(url);
        connectionSource.setUsername(username);
        connectionSource.setPassword(password);
        connectionSource.initialize();
        isInitialized = true;
    }

    @Override
    public boolean isActive() {
        return isInitialized;
    }

    @Override
    public @Nullable Connection getConnection() {
        throw new NullPointerException("OrmLiteConnectionPool doesn't provide java.sql connections");
    }

    @Override
    public @Nullable ConnectionSource getSource() {
        return connectionSource;
    }

    @Override
    public void close() throws Exception {
        connectionSource.close();
    }

}
