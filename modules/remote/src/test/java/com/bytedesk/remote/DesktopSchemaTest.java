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
import com.bytedesk.remote.audit.DesktopAuditLogEntity;
import com.bytedesk.remote.device.DesktopDeviceEntity;
import com.bytedesk.remote.session.DesktopSessionEntity;
import com.bytedesk.remote.session.DesktopSessionStatusEnum;
import org.hibernate.cfg.Configuration;
import java.time.ZonedDateTime;

class DesktopSchemaTest {
    @Test
    void migrationCreatesThreeTablesAndPortableLargeSignalColumns() throws Exception {
        Path resources = Path.of("../../starter/src/main/resources").toAbsolutePath().normalize();
        try (var connection = DriverManager.getConnection("jdbc:h2:mem:remote_schema;DB_CLOSE_DELAY=-1", "sa", "");
             var accessor = new DirectoryResourceAccessor(resources);
             var liquibase = new Liquibase("db/changelog/migration/261112_add_remote_assistance_tables.xml",
                     accessor, new JdbcConnection(connection))) {
            liquibase.update(new Contexts(), new LabelExpression());
            try (var statement = connection.createStatement()) {
                var result = statement.executeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME IN " +
                        "('BYTEDESK_DESKTOP_DEVICE','BYTEDESK_DESKTOP_SESSION','BYTEDESK_DESKTOP_AUDIT_LOG')");
                assertTrue(result.next());
                assertEquals(3, result.getInt(1));
                statement.executeUpdate("INSERT INTO bytedesk_desktop_session " +
                        "(uuid, updated_at, session_status, host_signals) VALUES ('test',CURRENT_TIMESTAMP,'CONNECTING','" +
                        "x".repeat(70000) + "')");
                var signal = statement.executeQuery("SELECT LENGTH(host_signals) FROM bytedesk_desktop_session");
                assertTrue(signal.next());
                assertEquals(70000, signal.getInt(1));
            }
            try (var factory = new Configuration()
                    .addAnnotatedClass(DesktopSessionEntity.class)
                    .addAnnotatedClass(DesktopDeviceEntity.class)
                    .addAnnotatedClass(DesktopAuditLogEntity.class)
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
                var entity = DesktopSessionEntity.builder().uid("orm").orgUid("org")
                        .createdAt(time).updatedAt(time).expiresAt(time)
                        .status(DesktopSessionStatusEnum.CONNECTING).hostSignals("x".repeat(70000)).build();
                var device = DesktopDeviceEntity.builder().uid("device-orm")
                        .createdAt(time).updatedAt(time).lastOnline(time).build();
                var audit = DesktopAuditLogEntity.builder().uid("audit-orm").sessionUid("orm")
                        .createdAt(time).updatedAt(time).eventType("INVITED").build();
                session.persist(entity);
                session.persist(device);
                session.persist(audit);
                tx.commit();
                session.clear();
                var stored = session.find(DesktopSessionEntity.class, entity.getId());
                assertEquals(70000, stored.getHostSignals().length());
                assertEquals(time.toInstant(), stored.getCreatedAt().toInstant());
                assertEquals(time.toInstant(), stored.getExpiresAt().toInstant());
                assertEquals(time.toInstant(), session.find(DesktopDeviceEntity.class, device.getId()).getLastOnline().toInstant());
                assertEquals(time.toInstant(), session.find(DesktopAuditLogEntity.class, audit.getId()).getCreatedAt().toInstant());
            }
        }
    }
}
