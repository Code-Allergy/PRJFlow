package com.cmpt370T7.PRJFlow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


// TODO better error handling, pass exception out of method calls to handle in upper body

/**
 * Maintains storage of global terms
 */
public class GlobalTermsDatabase {
    private final Logger logger;
    private final File databaseFile;

    public GlobalTermsDatabase(File databaseFile) {
        this.logger = LoggerFactory.getLogger(GlobalTermsDatabase.class);
        this.databaseFile = databaseFile;
        createDatabaseIfNotExists();

    }

    private void createDatabaseIfNotExists() {
        try (Connection conn = connect()) {
            if (conn != null) {
                String createTableSql = """
                CREATE TABLE IF NOT EXISTS terms (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    term TEXT NOT NULL UNIQUE
                );
    """;
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSql);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection connect() throws SQLException {
        String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
        return DriverManager.getConnection(url);
    }

    public void insertTerm(String term) {
        try (Connection conn = connect()) {
            if (conn != null) {
                // Ignore duplicates, but log at debug level.
                String insertSql = "INSERT OR IGNORE INTO terms (term) VALUES (?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    stmt.setString(1, term.toLowerCase());
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected == 0) {
                        logger.debug("Ignored duplicate entry for term: '{}'", term.toLowerCase());
                    } else {
                        logger.debug("Successfully inserted term: '{}'", term.toLowerCase());
                    }
                }


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeTerm(String term) {
        try (Connection conn = connect()) {
            if (conn != null) {
                String deleteSql = "DELETE FROM terms WHERE term = (?)";
                try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                    stmt.setString(1, term.toLowerCase());
                    stmt.execute();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeTerm(int id) {
        try (Connection conn = connect()) {
            if (conn != null) {
                String deleteSql = "DELETE FROM terms WHERE id = (?)";
                try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                    stmt.setInt(1, id);
                    stmt.execute();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllTerms() {
        List<String> terms = new ArrayList<>();
        try (Connection conn = connect()) {
            if (conn != null) {
                String getAllSql = "SELECT term FROM terms";
                try (Statement stmt = conn.createStatement();
                     ResultSet results = stmt.executeQuery(getAllSql)) {
                    while (results.next()) {
                        // Retrieve the term and add it to the list
                        String term = results.getString("term");
                        terms.add(term);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return terms;
    }
}
