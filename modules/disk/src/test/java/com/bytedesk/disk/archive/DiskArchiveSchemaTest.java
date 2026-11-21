package com.bytedesk.disk.archive;

import java.nio.file.Path;
import java.sql.DriverManager;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiskArchiveSchemaTest {
    @Test
    void liquibaseSchemaMatchesHibernateAndRequestKeyIsUnique() throws Exception {
        Path resources = Path.of("../../starter/src/main/resources").toAbsolutePath().normalize();
        try (var connection = DriverManager.getConnection("jdbc:h2:mem:archive-schema;DB_CLOSE_DELAY=-1", "sa", "");
                var accessor = new DirectoryResourceAccessor(resources);
                var liquibase = new Liquibase("db/changelog/migration/261119_add_disk_archive_file_table.xml",
                        accessor, new JdbcConnection(connection))) {
            liquibase.update(new Contexts(), new LabelExpression());
            try (var factory = new Configuration()
                    .addAnnotatedClass(DiskArchiveFileEntity.class)
                    .setProperty("hibernate.connection.url", "jdbc:h2:mem:archive-schema")
                    .setProperty("hibernate.connection.username", "sa")
                    .setProperty("hibernate.connection.password", "")
                    .setProperty("hibernate.hbm2ddl.auto", "validate")
                    .setProperty("hibernate.physical_naming_strategy",
                            "org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl")
                    .buildSessionFactory()) {
                assertNotNull(factory);
            }
            try (var statement = connection.createStatement()) {
                statement.executeUpdate("""
                    INSERT INTO bytedesk_disk_archive_file
                    (uuid,org_uid,user_uid,module,original_name,content_type,file_size,storage_backend,
                     storage_key,upload_state,upload_request_id,request_key)
                    VALUES ('one','org','user','CONTRACT','a.pdf','application/pdf',10,'PRIVATE_LOCAL',
                     'k1','READY','r1','rk1')
                    """);
                assertThrows(java.sql.SQLException.class, () -> statement.executeUpdate("""
                    INSERT INTO bytedesk_disk_archive_file
                    (uuid,org_uid,user_uid,module,original_name,content_type,file_size,storage_backend,
                     storage_key,upload_state,upload_request_id,request_key)
                    VALUES ('two','org','user','CONTRACT','b.pdf','application/pdf',10,'PRIVATE_LOCAL',
                     'k2','READY','r1','rk1')
                    """));
                // Same number may appear on multiple files; only request keys are unique.
                statement.executeUpdate("""
                    INSERT INTO bytedesk_disk_archive_file
                    (uuid,org_uid,user_uid,module,original_name,content_type,file_size,storage_backend,
                     storage_key,upload_state,upload_request_id,request_key,contract_number)
                    VALUES ('three','org','user2','CONTRACT','c.pdf','application/pdf',10,'PRIVATE_LOCAL',
                     'k3','READY','r2','rk2','HT-2026-001')
                    """);
                statement.executeUpdate("""
                    INSERT INTO bytedesk_disk_archive_file
                    (uuid,org_uid,user_uid,module,original_name,content_type,file_size,storage_backend,
                     storage_key,upload_state,upload_request_id,request_key,contract_number)
                    VALUES ('four','org','user3','CONTRACT','scan.pdf','application/pdf',10,'PRIVATE_LOCAL',
                     'k4','READY','r3','rk3','HT-2026-001')
                    """);
            }
            liquibase.update(new Contexts(), new LabelExpression());
        }
    }
}
