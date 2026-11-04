package com.bytedesk.starter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import static org.junit.jupiter.api.Assertions.*;

class StorageConfigImportTest {

    @TempDir
    Path directory;

    @Test
    void classpathProfilesImportMergedUploadResources() throws IOException {
        for (String profile : new String[] {"local", "open", "noai"}) {
            String imports = importsFor(profile);
            assertTrue(imports.contains("classpath:properties/" + profile + "/upload.properties"));
            assertFalse(imports.contains("storage.properties"));
            assertFalse(imports.contains("minio.properties"));
            var properties = new Properties();
            try (var input = getClass().getResourceAsStream("/properties/" + profile + "/upload.properties")) {
                assertNotNull(input);
                properties.load(input);
            }
            assertEquals("local", properties.getProperty("bytedesk.storage.provider"));
            assertEquals("bytedesk", properties.getProperty("bytedesk.storage.bucket-name"));
            assertTrue(properties.stringPropertyNames().stream().noneMatch(key -> key.startsWith("bytedesk.minio.")));
        }
    }

    @Test
    void allProfileTemplatesOmitUnusedUploadSelectors() throws IOException {
        for (String profile : new String[] {"local", "open", "noai", "prod", "kingbase"}) {
            Path file = Path.of("src/main/resources", profile.equals("kingbase")
                    ? "application-kingbase.properties" : "properties/" + profile + "/upload.properties");
            var properties = new Properties();
            try (var input = Files.newInputStream(file)) {
                properties.load(input);
            }
            assertNull(properties.getProperty("bytedesk.upload.type"), profile);
            assertNull(properties.getProperty("bytedesk.upload.provider"), profile);
            assertEquals("local", properties.getProperty("bytedesk.storage.provider"), profile);
        }
    }

    @Test
    void prodIgnoresLegacyFiles() throws IOException {
        Files.writeString(directory.resolve("minio.properties"),
                "bytedesk.minio.enabled=true\nbytedesk.minio.bucket-name=legacy-bucket\n");
        Files.writeString(directory.resolve("storage.properties"), "bytedesk.storage.provider=s3\n");

        var environment = loadProdImports();
        assertNull(environment.getProperty("bytedesk.storage.provider"));
        assertNull(environment.getProperty("bytedesk.minio.enabled"));
        assertFalse(importsFor("prod").contains("minio.properties"));
        assertFalse(importsFor("prod").contains("storage.properties"));
    }

    @Test
    void prodSupportsMergedUploadFile() throws IOException {
        Files.writeString(directory.resolve("upload.properties"),
                "bytedesk.upload.dir=uploads\nbytedesk.storage.provider=s3\n");

        var environment = loadProdImports();
        assertEquals("uploads", environment.getProperty("bytedesk.upload.dir"));
        assertEquals("s3", environment.getProperty("bytedesk.storage.provider"));
    }

    @Test
    void prodUploadIsOnlyStorageSource() throws IOException {
        Files.writeString(directory.resolve("minio.properties"),
                "bytedesk.minio.enabled=true\nbytedesk.minio.bucket-name=legacy-bucket\n");
        Files.writeString(directory.resolve("storage.properties"),
                "bytedesk.minio.enabled=true\nbytedesk.minio.bucket-name=storage-bucket\n");
        Files.writeString(directory.resolve("upload.properties"), "bytedesk.storage.provider=local\n");

        var environment = loadProdImports();
        assertEquals("local", environment.getProperty("bytedesk.storage.provider"));
        assertNull(environment.getProperty("bytedesk.minio.enabled"));
        assertNull(environment.getProperty("bytedesk.minio.bucket-name"));
    }

    @Test
    void prodStorageFilesRemainOptional() throws IOException {
        assertNull(loadProdImports().getProperty("bytedesk.storage.provider"));
    }

    private String importsFor(String profile) throws IOException {
        var properties = new Properties();
        // Production configuration is deliberately excluded from the packaged classpath.
        try (var input = Files.newInputStream(Path.of("src/main/resources/application-" + profile + ".properties"))) {
            properties.load(input);
        }
        return properties.getProperty("spring.config.import");
    }

    private StandardEnvironment loadProdImports() throws IOException {
        String imports = importsFor("prod").replace("file:./config/prod/", directory.toUri().toString());
        Path application = directory.resolve("application.properties");
        Files.writeString(application, "spring.config.import=" + imports + "\n");
        var environment = new StandardEnvironment();
        environment.getPropertySources().remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
        environment.getPropertySources().remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
        environment.getPropertySources().addFirst(new MapPropertySource("test",
                Map.of("spring.config.location", application.toUri().toString())));
        ConfigDataEnvironmentPostProcessor.applyTo(environment);
        return environment;
    }
}
