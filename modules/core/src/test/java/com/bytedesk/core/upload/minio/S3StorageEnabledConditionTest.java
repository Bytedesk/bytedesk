package com.bytedesk.core.upload.minio;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class S3StorageEnabledConditionTest {

    @Test
    void onlyS3ProviderEnablesStorage() {
        var environment = new MockEnvironment();
        var context = mock(ConditionContext.class);
        when(context.getEnvironment()).thenReturn(environment);
        var condition = new S3StorageEnabledCondition();

        assertFalse(condition.matches(context, null));
        environment.setProperty("bytedesk.minio.enabled", "true");
        assertFalse(condition.matches(context, null));
        environment.setProperty("bytedesk.storage.provider", "local");
        assertFalse(condition.matches(context, null));
        environment.setProperty("bytedesk.storage.provider", "S3");
        assertTrue(condition.matches(context, null));
    }
}
