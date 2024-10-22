package com.cmpt370T7.PRJFlow;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class GlobalTermsDatabaseTest {
    private GlobalTermsDatabase database;
    private File dbFile;

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        dbFile = new File(tempDir, "test-terms.db");
        database = new GlobalTermsDatabase(dbFile);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Close any database connections and delete the test database file
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath())) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS terms");
            }
        }
        if (dbFile.exists()) {
            assertThat(dbFile.delete()).isTrue();
        }
    }

    @Test
    void test_database_creation() {
        assertThat(dbFile).exists();
        assertThat(isTableCreated()).isTrue();
    }

    @Test
    void test_insert_term() {
        // Given
        String testTerm = "test term";

        // When
        database.insertTerm(testTerm);

        // Then
        List<String> terms = database.getAllTerms();
        assertThat(terms)
                .isNotEmpty()
                .hasSize(1)
                .contains(new String[]{testTerm.toLowerCase()});
    }

    @Test
    void test_insert_duplicate_term() {
        // Given
        String testTerm = "duplicate term";

        // When
        database.insertTerm(testTerm);
        database.insertTerm(testTerm);

        // Then
        List<String> terms = database.getAllTerms();
        assertThat(terms)
                .isNotEmpty()
                .hasSize(1)
                .containsOnlyOnce(new String[]{testTerm.toLowerCase()});
    }

    @Test
    void test_insert_multiple_terms() {
        // Given
        String[] testTerms = {"term1", "term2", "term3"};

        // When
        for (String term : testTerms) {
            database.insertTerm(term);
        }

        // Then
        List<String> terms = database.getAllTerms();
        assertThat(terms)
                .hasSize(3)
                .containsExactlyInAnyOrder("term1", "term2", "term3");
    }

    @Test
    void test_remove_term_by_string() {
        // Given
        String testTerm = "term to remove";
        database.insertTerm(testTerm);

        // When
        database.removeTerm(testTerm);

        // Then
        List<String> terms = database.getAllTerms();
        assertThat(terms).isEmpty();
    }

    @Test
    void test_remove_term_by_id() throws Exception {
        // Given
        String testTerm = "term to remove";
        database.insertTerm(testTerm);
        int termId = getTermId(testTerm);

        // When
        database.removeTerm(termId);

        // Then
        List<String> terms = database.getAllTerms();
        assertThat(terms).isEmpty();
    }

    @Test
    void test_get_all_terms_empty_database() {
        // When
        List<String> terms = database.getAllTerms();

        // Then
        assertThat(terms).isEmpty();
    }

    @Test
    void test_term_case_insensitivity() {
        // Given
        String upperCaseTerm = "TEST";
        String lowerCaseTerm = "test";

        // When
        database.insertTerm(upperCaseTerm);
        database.insertTerm(lowerCaseTerm);

        // Then
        List<String> terms = database.getAllTerms();
        assertThat(terms)
                .hasSize(1)
                .containsOnly(lowerCaseTerm);
    }

    /// Private method to check if the terms table exists
    private boolean isTableCreated() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath())) {
            try (ResultSet rs = conn.getMetaData().getTables(null, null, "terms", null)) {
                return rs.next();
            }
        } catch (Exception e) {
            return false;
        }
    }

    /// Helper method to get term ID from the database
    private int getTermId(String term) throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath())) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT id FROM terms WHERE term = '" + term.toLowerCase() + "'");
                if (rs.next()) {
                    return rs.getInt("id");
                }
                throw new RuntimeException("Term not found: " + term);
            }
        }
    }
}
