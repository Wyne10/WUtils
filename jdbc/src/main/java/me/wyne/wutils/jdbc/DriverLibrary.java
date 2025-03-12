package me.wyne.wutils.jdbc;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DriverLibrary {
    NONE("", "", "", ""),
    H2_V1(
      "com.h2database",
              "h2",
              "1.4.200",
            "org.h2.Driver"
    ),
    H2_V2(
      "com.h2database",
              "h2",
              "2.3.232",
            "org.h2.Driver"
    ),
    MYSQL(
      "com.mysql",
              "mysql-connector-j",
              "8.0.33",
            "com.mysql.cj.jdbc.NonRegisteringDriver"
    ),
    MARIADB(
      "org.mariadb.jdbc",
              "mariadb-java-client",
              "3.5.1",
            "org.mariadb.jdbc.Driver"
    ),
    POSTGRESQL(
      "org.postgresql",
              "postgresql",
              "42.7.4",
            "org.postgresql.Driver"
    ),
    SQLITE(
      "org.xerial",
              "sqlite-jdbc",
              "3.47.0.0",
            "org.sqlite.JDBC"
    );

    private final Path filenamePath;
    private URL mavenRepoURL;
    private final String driverClass;
    private boolean isRegistered;

    DriverLibrary(String groupId, String artifactId, String version, String driverClass) {
        if (this.name().equals("NONE"))
            isRegistered = true;

        String mavenPath = String.format("%s/%s/%s/%s-%s.jar",
                groupId.replace(".", "/"),
                artifactId,
                version,
                artifactId,
                version
        );

        this.filenamePath = Path.of("libraries/" + mavenPath);
        this.driverClass = driverClass;

        try {
            this.mavenRepoURL = new URL("https://repo1.maven.org/maven2/" + mavenPath);
        } catch (MalformedURLException e) {
            LogWrapper.exception("An exception occurred trying to format maven path to URL", e);
        }
    }

    @Nullable
    private URL getClassLoaderURL() {
        if (!Files.exists(this.filenamePath)) {
            try {
                try (InputStream in = this.mavenRepoURL.openStream()) {
                    Files.createDirectories(this.filenamePath.getParent());
                    Files.copy(in, Files.createFile(this.filenamePath), StandardCopyOption.REPLACE_EXISTING);
                }
                LogWrapper.info("Loaded '" + mavenRepoURL.toString() + "' JDBC driver");
            } catch (IOException e) {
                LogWrapper.exception("An exception occurred trying to load '" + mavenRepoURL.toString() + "' driver", e);
            }
        }

        try {
            return this.filenamePath.toUri().toURL();
        } catch (MalformedURLException e) {
            LogWrapper.exception("An exception occurred trying to format path to URL", e);
        }
        return null;
    }

    public void registerDriver() {
        if (isRegistered)
            return;

        try {
            URLClassLoader loader = new URLClassLoader(new URL[]{getClassLoaderURL()}, DriverLibrary.class.getClassLoader());
            DriverManager.registerDriver(new DriverShim((Driver) loader.loadClass(driverClass).getConstructor().newInstance()));
            isRegistered = true;
            LogWrapper.info("Registered '" + driverClass + "' JDBC driver");
        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException | ClassNotFoundException e) {
            LogWrapper.exception("An exception occurred trying to register driver '" + driverClass + "'", e);
        }
    }
}
