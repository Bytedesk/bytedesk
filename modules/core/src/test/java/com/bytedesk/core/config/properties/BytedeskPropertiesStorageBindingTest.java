package com.bytedesk.core.config.properties;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.SystemEnvironmentPropertySource;

import static org.junit.jupiter.api.Assertions.*;

class BytedeskPropertiesStorageBindingTest {

    private Map<String, Object> dockerEnvironment() {
        return new HashMap<>(Map.of(
                "BYTEDESK_STORAGE_PROVIDER", "",
                "BYTEDESK_STORAGE_ENDPOINT", "http://bytedesk-rustfs:9000",
                "BYTEDESK_STORAGE_ACCESS_KEY", "test-storage-access",
                "BYTEDESK_STORAGE_SECRET_KEY", "test-storage-secret",
                "BYTEDESK_STORAGE_BUCKET_NAME", "storage-bucket",
                "BYTEDESK_STORAGE_REGION", "storage-region",
                "BYTEDESK_STORAGE_SECURE", "true",
                "BYTEDESK_STORAGE_PUBLIC_READ", "false"));
    }

    private BytedeskProperties bind(Map<String, Object> environment) {
        var source = new SystemEnvironmentPropertySource("systemEnvironment", environment);
        return new Binder(ConfigurationPropertySources.from(source))
                .bind("bytedesk", BytedeskProperties.class).get();
    }

    @Test
    void bindsStorageAccessKeyWithoutProvider() {
        var properties = bind(Map.of("BYTEDESK_STORAGE_ACCESS_KEY", "test-storage-access"));

        assertEquals("test-storage-access", properties.getStorage().getAccessKey());
        assertFalse(properties.isS3StorageEnabled());
        assertEquals("test-storage-access", properties.resolveStorageAccessKey());
    }

    @Test
    void bindsDockerDefaultsWithStorageDisabled() {
        var properties = bind(dockerEnvironment());

        assertFalse(properties.isS3StorageEnabled());
        assertEquals("test-storage-access", properties.getStorage().getAccessKey());
        assertEquals("test-storage-secret", properties.getStorage().getSecretKey());
        assertEquals("http://bytedesk-rustfs:9000", properties.getStorage().getEndpoint());
        assertEquals("storage-bucket", properties.getStorage().getBucketName());
        assertEquals("storage-region", properties.getStorage().getRegion());
        assertTrue(properties.getStorage().getSecure());
        assertFalse(properties.getStorage().getPublicRead());
    }

    @Test
    void bindsExplicitLocalProviderWithUnusedStorageSettings() {
        var environment = dockerEnvironment();
        environment.put("BYTEDESK_STORAGE_PROVIDER", "local");

        var properties = bind(environment);
        assertFalse(properties.isS3StorageEnabled());
    }

    @Test
    void bindsS3Provider() {
        var environment = dockerEnvironment();
        environment.put("BYTEDESK_STORAGE_PROVIDER", "s3");

        var properties = bind(environment);
        assertTrue(properties.isS3StorageEnabled());
        assertEquals("test-storage-access", properties.resolveStorageAccessKey());
        assertEquals("test-storage-secret", properties.resolveStorageSecretKey());
        assertEquals("http://bytedesk-rustfs:9000", properties.resolveStorageEndpoint());
        assertEquals("storage-bucket", properties.resolveStorageBucketName());
        assertEquals("storage-region", properties.resolveStorageRegion());
        assertTrue(properties.resolveStorageSecure());
        assertFalse(properties.resolveStoragePublicRead());
    }

    @Test
    void legacyMinioSettingsCannotEnableStorageOrOverrideCredentials() {
        var environment = dockerEnvironment();
        environment.put("BYTEDESK_MINIO_ENABLED", "true");
        environment.put("BYTEDESK_MINIO_ACCESS_KEY", "test-legacy-access");
        environment.put("BYTEDESK_MINIO_SECRET_KEY", "test-legacy-secret");

        var properties = bind(environment);
        assertFalse(properties.isS3StorageEnabled());
        assertEquals("test-storage-access", properties.resolveStorageAccessKey());
        assertEquals("test-storage-secret", properties.resolveStorageSecretKey());
    }

    @Test
    void s3DoesNotFallBackToLegacySettings() {
        var properties = bind(Map.of(
                "BYTEDESK_STORAGE_PROVIDER", "s3",
                "BYTEDESK_STORAGE_ACCESS_KEY", "test-storage-access",
                "BYTEDESK_MINIO_ENDPOINT", "http://legacy-storage:9000",
                "BYTEDESK_MINIO_SECRET_KEY", "test-legacy-secret"));

        assertEquals("test-storage-access", properties.resolveStorageAccessKey());
        assertEquals("http://127.0.0.1:19000", properties.resolveStorageEndpoint());
        assertNull(properties.resolveStorageSecretKey());
        assertTrue(properties.resolveStoragePublicRead());
    }

    @Test
    void bindsDottedPropertiesAndPreservesExpiryLimits() {
        var source = new MapPropertySource("properties", Map.of(
                "bytedesk.storage.provider", "s3",
                "bytedesk.storage.access-key", "test-storage-access",
                "bytedesk.storage.public-read", "false",
                "bytedesk.storage.presigned-expiry-days", "30"));
        var properties = new Binder(ConfigurationPropertySources.from(source))
                .bind("bytedesk", BytedeskProperties.class).get();

        assertEquals("test-storage-access", properties.resolveStorageAccessKey());
        assertFalse(properties.resolveStoragePublicRead());
        assertEquals(7 * 24 * 3600, properties.getStoragePresignedExpirySeconds());
        properties.getStorage().setPresignedExpiryDays(0);
        assertEquals(24 * 3600, properties.getStoragePresignedExpirySeconds());
    }

    @Test
    void unusedUploadSelectorsAreNotBeanProperties() {
        assertNull(BeanUtils.getPropertyDescriptor(BytedeskProperties.Upload.class, "type"));
        assertNull(BeanUtils.getPropertyDescriptor(BytedeskProperties.Upload.class, "provider"));
        assertNull(BeanUtils.getPropertyDescriptor(BytedeskProperties.class, "uploadType"));
    }

    @Test
    void defaultsToLocalWithoutCredentials() {
        var properties = new BytedeskProperties();
        assertEquals("local", properties.getStorage().getProvider());
        assertFalse(properties.isS3StorageEnabled());
        assertNull(properties.resolveStorageAccessKey());
        assertNull(properties.resolveStorageSecretKey());
        assertEquals("bytedesk", properties.resolveStorageBucketName());
        assertEquals("us-east-1", properties.resolveStorageRegion());
        assertFalse(properties.resolveStorageSecure());
        assertTrue(properties.resolveStoragePublicRead());
    }
}
