package com.bytedesk.remote;

import java.nio.file.Path;
import java.sql.DriverManager;
import liquibase.Liquibase;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.bytedesk.remote.device.RemoteDeviceEntity;
import com.bytedesk.remote.session.RemoteSessionEntity;
import com.bytedesk.remote.invitation.RemoteInvitationEntity;
import com.bytedesk.remote.session.RemoteSessionStatusEnum;
import org.hibernate.cfg.Configuration;
import java.time.ZonedDateTime;

class RemoteSchemaTest {
    
    private static void runMigration(String databaseUrl, String changelog) throws Exception {
        Path resources = Path.of("../../starter/src/main/resources").toAbsolutePath().normalize();
        try (var connection = DriverManager.getConnection(databaseUrl, "sa", "");
             var accessor = new DirectoryResourceAccessor(resources);
             var liquibase = new Liquibase(changelog, accessor, new JdbcConnection(connection))) {
            liquibase.update(new Contexts(), new LabelExpression());
        }
    }

    @Test
    void invitationMigrationAddsIndexWhenJpaAlreadyCreatedTheTable() throws Exception {
        Path resources = Path.of("../../starter/src/main/resources").toAbsolutePath().normalize();
        try (var connection = DriverManager.getConnection("jdbc:h2:mem:existing_invitation_schema;DB_CLOSE_DELAY=-1", "sa", "");
             var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE bytedesk_desktop_invitation (expires_at TIMESTAMP)");
            try (var accessor = new DirectoryResourceAccessor(resources);
                 var liquibase = new Liquibase("db/changelog/migration/261119_add_remote_invitations.xml",
                         accessor, new JdbcConnection(connection))) {
                liquibase.update(new Contexts(), new LabelExpression());
            }
            try (var migratedConnection = DriverManager.getConnection(
                    "jdbc:h2:mem:existing_invitation_schema;DB_CLOSE_DELAY=-1", "sa", "");
                 var indexes = migratedConnection.getMetaData().getIndexInfo(null, "PUBLIC",
                         "BYTEDESK_DESKTOP_INVITATION", false, false)) {
                boolean found = false;
                while (indexes.next()) {
                    if ("IDX_REMOTE_INVITATION_EXPIRY".equalsIgnoreCase(indexes.getString("INDEX_NAME"))) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found);
            }
        }
    }

    @Test
    void invitationMigrationMatchesEntityMapping() throws Exception {
        String databaseUrl = "jdbc:h2:mem:invitation_schema;DB_CLOSE_DELAY=-1";
        runMigration(databaseUrl, "db/changelog/migration/261112_add_remote_assistance_tables.xml");
        runMigration(databaseUrl, "db/changelog/migration/261119_add_remote_invitations.xml");
        runMigration(databaseUrl, "db/changelog/migration/261120_rename_remote_assistance_tables.xml");
        try (var factory = new Configuration()
                .addAnnotatedClass(RemoteInvitationEntity.class)
                .setProperty("hibernate.connection.url", databaseUrl)
                .setProperty("hibernate.connection.username", "sa")
                .setProperty("hibernate.connection.password", "")
                .setProperty("hibernate.hbm2ddl.auto", "validate")
                .setProperty("hibernate.physical_naming_strategy",
                        "org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl")
                .buildSessionFactory()) {
            assertNotNull(factory);
        }
    }

    @Test
    void renameMigrationSkipsWhenLegacyTablesAreAbsent() throws Exception {
        String databaseUrl = "jdbc:h2:mem:rename_guard_schema;DB_CLOSE_DELAY=-1";
        runMigration(databaseUrl, "db/changelog/migration/261120_rename_remote_assistance_tables.xml");
        try (var connection = DriverManager.getConnection(databaseUrl, "sa", "");
             var statement = connection.createStatement()) {
            var result = statement.executeQuery(
                    "SELECT COUNT(*) FROM DATABASECHANGELOG WHERE ID LIKE '261120-rename-remote-%' AND EXECTYPE = 'MARK_RAN'");
            assertTrue(result.next());
            assertEquals(4, result.getInt(1));
        }
    }

    @Test
    void migrationCreatesThreeTablesAndPortableLargeSignalColumns() throws Exception {
        String databaseUrl = "jdbc:h2:mem:remote_schema;DB_CLOSE_DELAY=-1";
        runMigration(databaseUrl, "db/changelog/migration/261112_add_remote_assistance_tables.xml");
        runMigration(databaseUrl, "db/changelog/migration/261119_add_remote_invitations.xml");
        runMigration(databaseUrl, "db/changelog/migration/261120_rename_remote_assistance_tables.xml");
        try (var connection = DriverManager.getConnection(databaseUrl, "sa", "")) {
            try (var statement = connection.createStatement()) {
                var result = statement.executeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME IN " +
                        "('BYTEDESK_REMOTE_DEVICE','BYTEDESK_REMOTE_SESSION','BYTEDESK_REMOTE_AUDIT_LOG','BYTEDESK_REMOTE_INVITATION')");
                assertTrue(result.next());
                assertEquals(4, result.getInt(1));
                statement.executeUpdate("INSERT INTO bytedesk_remote_session " +
                        "(uuid, updated_at, session_status, host_signals) VALUES ('test',CURRENT_TIMESTAMP,'CONNECTING','" +
                        "x".repeat(70000) + "')");
                var signal = statement.executeQuery("SELECT LENGTH(host_signals) FROM bytedesk_remote_session");
                assertTrue(signal.next());
                assertEquals(70000, signal.getInt(1));
            }
            try (var factory = new Configuration()
                    .addAnnotatedClass(RemoteSessionEntity.class)
                    .addAnnotatedClass(RemoteDeviceEntity.class)
                    .setProperty("hibernate.connection.url", "jdbc:h2:mem:remote_schema")
                    .setProperty("hibernate.connection.username", "sa")
                    .setProperty("hibernate.connection.password", "")
                    .setProperty("hibernate.hbm2ddl.auto", "validate")
                    .setProperty("hibernate.physical_naming_strategy",
                            "org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl")
                    .buildSessionFactory();
                 var session = factory.openSession()) {
                var tx = session.beginTransaction();
                var time = ZonedDateTime.parse("2026-11-12T10:15:30+08:00");
                var entity = RemoteSessionEntity.builder().uid("orm").orgUid("org")
                        .createdAt(time).updatedAt(time).expiresAt(time)
                        .status(RemoteSessionStatusEnum.CONNECTING).hostSignals("x".repeat(70000)).build();
                var device = RemoteDeviceEntity.builder().uid("device-orm")
                        .createdAt(time).updatedAt(time).lastOnline(time).build();
                session.persist(entity);
                session.persist(device);
                tx.commit();
                session.clear();
                var stored = session.find(RemoteSessionEntity.class, entity.getId());
                assertEquals(70000, stored.getHostSignals().length());
                assertEquals(time.toInstant(), stored.getCreatedAt().toInstant());
                assertEquals(time.toInstant(), stored.getExpiresAt().toInstant());
                assertEquals(time.toInstant(), session.find(RemoteDeviceEntity.class, device.getId()).getLastOnline().toInstant());
            }
        }
    }
}
