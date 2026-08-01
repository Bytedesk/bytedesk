package com.bytedesk.core.topic_subscription;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

class TopicSubscriptionRestControllerTest {

    @Test
    void queryByUserKeepsReadPermissionAndHandlesQueryUserAlias() throws NoSuchMethodException {
        Method method = TopicSubscriptionRestController.class.getDeclaredMethod(
                "queryByUser",
                TopicSubscriptionRequest.class);

        GetMapping mapping = method.getAnnotation(GetMapping.class);
        PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);

        assertThat(mapping).isNotNull();
        assertThat(mapping.value()).containsExactly("/query", "/query/user");
        assertThat(preAuthorize).isNotNull();
        assertThat(preAuthorize.value()).isEqualTo(TopicSubscriptionPermissions.HAS_TOPIC_SUBSCRIPTION_READ);
    }
}