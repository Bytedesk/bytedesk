package com.bytedesk.socket.mqtt.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * PUBLISH重发消息存储
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class MqttDupPublishMessage implements Serializable {

    private static final long serialVersionUID = -8112511377194421600L;

    private String clientId;

    private String topic;

    private int mqttQoS;

    private int messageId;
    
    private byte[] messageBytes;
}
