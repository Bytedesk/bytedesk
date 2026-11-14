package com.bytedesk.starter.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

class WorkgroupTagRoutingMigrationTest {

    private static final String CHANGELOG = "db/changelog/migration/261113_add_workgroup_tag_routing.xml";

    @Test
    void upgradePreservesLegacyRowsAndIsRepeatable() throws Exception {
        try (Connection connection = open()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE bytedesk_service_workgroup (id BIGINT PRIMARY KEY)");
                statement.execute("CREATE TABLE bytedesk_service_workgroup_settings (id BIGINT PRIMARY KEY)");
                statement.execute("CREATE TABLE bytedesk_core_thread (id BIGINT PRIMARY KEY)");
                statement.execute("INSERT INTO bytedesk_service_workgroup VALUES (1)");
                statement.execute("INSERT INTO bytedesk_service_workgroup_settings VALUES (1)");
                statement.execute("INSERT INTO bytedesk_core_thread VALUES (1)");
            }
            migrate(connection);
            migrate(connection);
            try (Statement statement = connection.createStatement();
                    ResultSet result = statement.executeQuery(
                            "SELECT tag_routing_enabled FROM bytedesk_service_workgroup_settings WHERE id=1")) {
                assertTrue(result.next());
                assertFalse(result.getBoolean(1));
                assertFalse(result.wasNull());
            }
            try (Statement statement = connection.createStatement();
                    ResultSet result = statement.executeQuery(
                            "SELECT routing_tag_snapshot,tag_routing_evaluated FROM bytedesk_core_thread WHERE id=1")) {
                assertTrue(result.next());
                assertNull(result.getString(1));
                assertNull(result.getObject(2));
            }
            try (Statement statement = connection.createStatement();
                    ResultSet result = statement.executeQuery(
                            "SELECT tag_agent_bindings FROM bytedesk_service_workgroup WHERE id=1")) {
                assertTrue(result.next());
                assertNull(result.getString(1));
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute("UPDATE bytedesk_service_workgroup SET tag_agent_bindings='{\"tag-1\":\"agent-1\"}' WHERE id=1");
                statement.execute("UPDATE bytedesk_core_thread SET routing_tag_snapshot='tag-1,tag-2',tag_routing_evaluated=true WHERE id=1");
            }
        }
    }

    @Test
    void existingColumnDoesNotPreventAddingOtherColumns() throws Exception {
        try (Connection connection = open()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE bytedesk_service_workgroup (id BIGINT,tag_agent_bindings VARCHAR(2048))");
                statement.execute("CREATE TABLE bytedesk_service_workgroup_settings (id BIGINT)");
                statement.execute("CREATE TABLE bytedesk_core_thread (id BIGINT)");
            }
            migrate(connection);
            try (ResultSet columns = connection.getMetaData().getColumns(null, null, "BYTEDESK_CORE_THREAD", "TAG_ROUTING_EVALUATED")) {
                assertTrue(columns.next());
            }
        }
    }

    @Test
    void emptyDatabaseLeavesEntityTablesToHibernateBaseline() throws Exception {
        try (Connection connection = open()) {
            migrate(connection);
            try (Statement statement = connection.createStatement();
                    ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM DATABASECHANGELOG")) {
                assertTrue(result.next());
                assertEquals(4, result.getInt(1));
            }
            try (ResultSet tables = connection.getMetaData().getTables(null, null, "BYTEDESK_SERVICE_WORKGROUP", null)) {
                assertFalse(tables.next());
            }
            try (ResultSet tables = connection.getMetaData().getTables(null, null, "BYTEDESK_CORE_THREAD", null)) {
                assertFalse(tables.next());
            }
        }
    }

    private Connection open() throws Exception {
        return DriverManager.getConnection("jdbc:h2:mem:tag-routing-" + UUID.randomUUID(), "sa", "");
    }

    private void migrate(Connection connection) throws Exception {
        try (Connection migrationConnection = DriverManager.getConnection(connection.getMetaData().getURL(), "sa", "");
                ClassLoaderResourceAccessor resources = new ClassLoaderResourceAccessor();
                Liquibase liquibase = new Liquibase(CHANGELOG, resources, new JdbcConnection(migrationConnection))) {
            liquibase.update(new Contexts(), new LabelExpression());
        }
    }
}
