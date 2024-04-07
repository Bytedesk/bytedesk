package com.bytedesk.socket.mqtt.model;

import java.io.Serializable;

import lombok.Data;

/**
 * 发送给MQ消息
 */
@Data
public class MqttMqMessage implements Serializable {

    private static final long serialVersionUID = -2814339696322592690L;

    private String clientId;

    private String topic;

    private int mqttQoS;

    private byte[] messageBytes;

    private boolean retain;

    private boolean dup;
}
