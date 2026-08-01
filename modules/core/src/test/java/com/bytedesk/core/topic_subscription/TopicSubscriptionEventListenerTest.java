package com.bytedesk.core.topic_subscription;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.quartz.event.QuartzFiveSecondEvent;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.socket.mqtt.service.MqttConnectionService;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic_subscription.event.TopicSubscriptionCreateEvent;

@ExtendWith(MockitoExtension.class)
class TopicSubscriptionEventListenerTest {

    @Mock
    private TopicSubscriptionRestService topicSubscriptionRestService;

    @Mock
    private TopicSubscriptionCacheService topicSubscriptionCacheService;

    @Mock
    private ThreadRestService threadRestService;

    @Mock
    private MqttConnectionService mqttConnectionService;

    private TopicSubscriptionEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new TopicSubscriptionEventListener(
                topicSubscriptionRestService,
                topicSubscriptionCacheService,
                threadRestService,
                mqttConnectionService);
    }

        @Test
        void onTopicSubscriptionCreateEventQueuesSubscriptionRequest() {
                TopicSubscriptionEntity entity = TopicSubscriptionEntity.builder()
                                .topic("org/workgroup/test")
                                .userUid("user-1")
                                .build();

                listener.onTopicSubscriptionCreateEvent(new TopicSubscriptionCreateEvent(entity));

                verify(topicSubscriptionCacheService).pushRequest(any(TopicSubscriptionRequest.class));
        }

        @Test
        void onTopicSubscriptionCreateEventIgnoresPersistedSubscriptions() {
                TopicSubscriptionEntity entity = TopicSubscriptionEntity.builder()
                                .topic("org/workgroup/test")
                                .userUid("user-1")
                                .build();
                entity.setUid("sub-1");

                listener.onTopicSubscriptionCreateEvent(new TopicSubscriptionCreateEvent(entity));

                org.mockito.Mockito.verifyNoInteractions(topicSubscriptionCacheService);
        }

    @Test
    void onQuartzFiveSecondEventCreatesQueuedSubscriptionsAndDrainsLegacyClientIds() {
                TopicSubscriptionRequest request = TopicSubscriptionRequest.builder()
                .topic("org/workgroup/test")
                .userUid("user-1")
                .build();
        when(topicSubscriptionCacheService.getTopicRequestList())
                .thenReturn(List.of(JSON.toJSONString(request)));
        when(topicSubscriptionCacheService.getClientIdList())
                .thenReturn(List.of("user-1/client/device-1"));

        listener.onQuartzFiveSecondEvent(new QuartzFiveSecondEvent(this));

		        verify(topicSubscriptionRestService).createSystemTopicSubscription(any(TopicSubscriptionRequest.class));
        verify(topicSubscriptionCacheService).getClientIdList();
    }

    @Test
    void onQuartzOneMinEventPushesConnectedClientIdsToCache() {
        when(mqttConnectionService.getConnectedClientIds())
                .thenReturn(Set.of("user-1/client/device-1", "user-2/client/device-2"));

        listener.onQuartzOneMinEvent(new QuartzOneMinEvent(this));

        verify(topicSubscriptionCacheService).pushClientId("user-1/client/device-1");
        verify(topicSubscriptionCacheService).pushClientId("user-2/client/device-2");
    }
}