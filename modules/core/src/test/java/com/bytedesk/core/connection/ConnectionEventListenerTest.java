package com.bytedesk.core.connection;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bytedesk.core.socket.mqtt.event.MqttConnectedEvent;
import com.bytedesk.core.socket.mqtt.event.MqttDisconnectedEvent;

@ExtendWith(MockitoExtension.class)
class ConnectionEventListenerTest {

    @Mock
    private ConnectionRestService connectionRestService;

    private ConnectionEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new ConnectionEventListener(connectionRestService);
    }

    @Test
    void duplicateConnectedEventsAreDeduplicated() {
        String clientId = "user/FLUTTER_IOS/device";
        listener.onMqttConnectedEvent(new MqttConnectedEvent(this, clientId));
        listener.onMqttConnectedEvent(new MqttConnectedEvent(this, clientId));

        verifyConnected(clientId, 1);
    }

    @Test
    void rapidReconnectAfterDisconnectIsNotDeduplicated() {
        String clientId = "user/FLUTTER_IOS/device";
        listener.onMqttConnectedEvent(new MqttConnectedEvent(this, clientId));
        listener.onMqttDisconnectedEvent(new MqttDisconnectedEvent(this, clientId));
        listener.onMqttConnectedEvent(new MqttConnectedEvent(this, clientId));

        verify(connectionRestService).markDisconnected(clientId);
        verifyConnected(clientId, 2);
    }

    @Test
    void disconnectOnlyResetsDeduplicationForThatDevice() {
        String iosClient = "user/FLUTTER_IOS/ios";
        String androidClient = "user/FLUTTER_ANDROID/android";
        listener.onMqttConnectedEvent(new MqttConnectedEvent(this, iosClient));
        listener.onMqttConnectedEvent(new MqttConnectedEvent(this, androidClient));
        listener.onMqttDisconnectedEvent(new MqttDisconnectedEvent(this, iosClient));
        listener.onMqttConnectedEvent(new MqttConnectedEvent(this, androidClient));

        verifyConnected(androidClient, 1);
    }

    private void verifyConnected(String clientId, int count) {
        String[] parts = clientId.split("/");
        verify(connectionRestService, times(count)).markConnected(
                parts[0], null, clientId, parts[2],
                ConnectionProtocalEnum.MQTT.name(), null, null, null, 90);
    }
}
