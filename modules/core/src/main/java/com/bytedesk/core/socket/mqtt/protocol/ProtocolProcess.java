package com.bytedesk.core.socket.mqtt.protocol;

import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.socket.mqtt.service.MqttAuthService;
import com.bytedesk.core.socket.mqtt.service.MqttConnectionService;
import com.bytedesk.core.socket.mqtt.service.MqttMessageIdService;
import com.bytedesk.core.socket.mqtt.service.MqttEventPublisher;
import com.bytedesk.core.socket.mqtt.service.MqttSessionService;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Component
public class ProtocolProcess {

    @Autowired
    private MqttSessionService mqttSessionStoreService;
    
    @Autowired
    private MqttAuthService mqttAuthService;

    @Autowired
    private MqttMessageIdService mqttMessageIdService;

    @Autowired
    private MqttEventPublisher mqService;

    @Autowired
    private IMessageSendService messageSendService;

    @Autowired
    private MqttConnectionService mqttConnectionService;

    private Connect connect;

    private Subscribe subscribe;

    private UnSubscribe unSubscribe;

    private Publish publish;

    private DisConnect disConnect;

    private PingReq pingReq;

    private PubRel pubRel;

    private PubAck pubAck;

    private PubRec pubRec;

    private PubComp pubComp;

    public Connect connect() {
        if (connect == null) {
            connect = new Connect(mqttAuthService, mqttSessionStoreService, mqService);
        }
        return connect;
    }

    public Subscribe subscribe() {
        if (subscribe == null) {
            subscribe = new Subscribe(mqService);
        }
        return subscribe;
    }

    public UnSubscribe unSubscribe() {
        if (unSubscribe == null) {
            unSubscribe = new UnSubscribe(mqService);
        }
        return unSubscribe;
    }

    public Publish publish() {
        if (publish == null) {
            publish = new Publish(messageSendService);
        }
        return publish;
    }

    public DisConnect disConnect() {
        if (disConnect == null) {
            disConnect = new DisConnect(mqttSessionStoreService, mqService);
        }
        return disConnect;
    }

    public PingReq pingReq() {
        if (pingReq == null) {
            pingReq = new PingReq(mqttConnectionService);
        }
        return pingReq;
    }

    public PubRel pubRel() {
        if (pubRel == null) {
            pubRel = new PubRel();
        }
        return pubRel;
    }

    public PubAck pubAck() {
        if (pubAck == null) {
            pubAck = new PubAck();
        }
        return pubAck;
    }

    public PubRec pubRec() {
        if (pubRec == null) {
            pubRec = new PubRec();
        }
        return pubRec;
    }

    public PubComp pubComp() {
        if (pubComp == null) {
            pubComp = new PubComp();
        }
        return pubComp;
    }

}
