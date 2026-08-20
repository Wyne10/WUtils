package me.wyne.wutils.jdbc;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Downloads real driver jars from Maven Central, so it is tagged {@code network} and
 * excluded from the default build. Run with {@code -PnetworkTests}.
 */
@Tag("network")
@TestMethodOrder(MethodOrderer.MethodName.class)
class DriverLibraryTest {

    /** A URL each driver should recognise, without needing a server to be reachable. */
    private static String probeUrl(DriverLibrary library) {
        return switch (library) {
            case H2_V1, H2_V2 -> "jdbc:h2:mem:probe";
            case MYSQL -> "jdbc:mysql://localhost:3306/probe";
            case MARIADB -> "jdbc:mariadb://localhost:3306/probe";
            case POSTGRESQL -> "jdbc:postgresql://localhost:5432/probe";
            case SQLITE -> "jdbc:sqlite::memory:";
            case NONE -> throw new IllegalArgumentException("NONE has no URL");
        };
    }

    /**
     * Asserts a shim registered by {@link DriverLibrary} answers for {@code url}. Checking
     * every registered driver rather than {@link DriverManager#getDriver} keeps this honest
     * when a driver is also on the test classpath and auto-registers itself — H2 is, because
     * the pool tests need it.
     */
    private static void assertShimAcceptsUrl(DriverLibrary library, String url) {
        boolean accepted = DriverManager.drivers()
                .filter(DriverShim.class::isInstance)
                .anyMatch(driver -> {
                    try {
                        return driver.acceptsURL(url);
                    } catch (SQLException e) {
                        return false;
                    }
                });
        assertTrue(accepted, library + " registered no DriverShim accepting " + url);
    }

    @ParameterizedTest
    @EnumSource(value = DriverLibrary.class, names = "NONE", mode = EnumSource.Mode.EXCLUDE)
    void downloadsAndRegistersEveryDriver(DriverLibrary library) throws Exception {
        library.registerDriver();
        assertShimAcceptsUrl(library, probeUrl(library));
    }

    @ParameterizedTest
    @EnumSource(value = DriverLibrary.class, names = "NONE", mode = EnumSource.Mode.EXCLUDE)
    void registeringTwiceIsANoOp(DriverLibrary library) {
        assertDoesNotThrow(library::registerDriver);
        assertDoesNotThrow(library::registerDriver);
    }

    @Test
    void noneRegistersNothing() {
        assertDoesNotThrow(DriverLibrary.NONE::registerDriver);
    }

    /** Regression: a partial download must never be left behind for a later call to trust. */
    @Test
    void zzLeavesNoPartialDownloadsBehind() throws Exception {
        for (DriverLibrary library : DriverLibrary.values()) {
            if (library != DriverLibrary.NONE)
                library.registerDriver();
        }
        Path libraries = Path.of("libraries");
        assertTrue(Files.isDirectory(libraries), "no jars were downloaded to " + libraries.toAbsolutePath());
        try (Stream<Path> tree = Files.walk(libraries)) {
            List<Path> leftovers = tree.filter(p -> p.getFileName().toString().endsWith(".part")).toList();
            assertTrue(leftovers.isEmpty(), "temporary download files left behind: " + leftovers);
        }
        try (Stream<Path> tree = Files.walk(libraries)) {
            assertTrue(tree.anyMatch(p -> p.getFileName().toString().endsWith(".jar")),
                    "expected at least one downloaded jar under " + libraries.toAbsolutePath());
        }
    }

    /** SQLite is embedded and its URL scheme is claimed by no other constant. */
    @Test
    void zzSqliteConnectsEndToEnd() throws Exception {
        DriverLibrary.SQLITE.registerDriver();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT 1")) {
            assertTrue(result.next());
            assertEquals(1, result.getInt(1));
        }
    }

    /**
     * Both H2 constants load {@code org.h2.Driver} and answer for {@code jdbc:h2:}, so once
     * both are registered which one serves a URL is whichever DriverManager reaches first.
     */
    @Test
    void zzBothH2ConstantsClaimTheSameUrlScheme() throws Exception {
        DriverLibrary.H2_V1.registerDriver();
        DriverLibrary.H2_V2.registerDriver();
        assertTrue(DriverManager.getDriver("jdbc:h2:mem:probe").acceptsURL("jdbc:h2:mem:probe"));
    }
}
