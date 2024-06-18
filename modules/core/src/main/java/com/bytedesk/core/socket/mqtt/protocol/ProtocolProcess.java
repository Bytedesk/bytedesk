package com.bytedesk.core.socket.mqtt.protocol;

import com.bytedesk.core.event.BytedeskEventPublisher;
import com.bytedesk.core.socket.mqtt.service.*;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 协议处理
 *
 * @author jackning
 */
@Data
@Component
public class ProtocolProcess {

    @Autowired
    private MqttSessionService mqttSessionStoreService;

    // @Autowired
    // private MqttSubscribeService mqttSubscribeStoreService;

    @Autowired
    private MqttAuthService mqttAuthService;

    // @Autowired
    // private RedisBlockService redisBlockService;

    // @Autowired
    // private RedisAutoReplyService redisAutoReplyService;

    @Autowired
    private MqttMessageIdService mqttMessageIdService;

    // @Autowired
    // private MqttRetainMessageStoreService mqttRetainMessageStoreService;

    // @Autowired
    // private MqttDupPublishMessageStoreService mqttDupPublishMessageStoreService;

    // @Autowired
    // private MqttDupPubRelMessageStoreService mqttDupPubRelMessageStoreService;

    // @Autowired
    // private MqttClientIdService mqttClientIdStoreService;

    // @Autowired
    // private TopicService topicService;

    @Autowired
    private BytedeskEventPublisher bytedeskEventPublisher;

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
            connect = new Connect(
                    mqttAuthService,
                    mqttSessionStoreService,
                    // mqttDupPublishMessageStoreService,
                    // mqttDupPubRelMessageStoreService,
                    // mqttSubscribeStoreService,
                    // mqttClientIdStoreService,
                    bytedeskEventPublisher
            // topicService
            );
        }
        return connect;
    }

    public Subscribe subscribe() {
        if (subscribe == null) {
            subscribe = new Subscribe(
                    // mqttMessageIdService,
                    // mqttSubscribeStoreService
                    // mqttRetainMessageStoreService
                    // redisUserService
                    // topicService
                    bytedeskEventPublisher);
        }
        return subscribe;
    }

    public UnSubscribe unSubscribe() {
        if (unSubscribe == null) {
            unSubscribe = new UnSubscribe(
                    // mqttSubscribeStoreService
                    // topicService
                    bytedeskEventPublisher);
        }
        return unSubscribe;
    }

    public Publish publish() {
        if (publish == null) {
            publish = new Publish(
                    // mqttRetainMessageStoreService,
                    bytedeskEventPublisher
            // mqttMessageIdService,
            // mqttClientIdStoreService,
            // mqttSessionStoreService
            );
        }
        return publish;
    }

    public DisConnect disConnect() {
        if (disConnect == null) {
            disConnect = new DisConnect(
                    mqttSessionStoreService,
                    // topicService,
                    bytedeskEventPublisher
            // mqttSubscribeStoreService,
            // mqttDupPublishMessageStoreService,
            // mqttDupPubRelMessageStoreService,
            // mqttClientIdStoreService
            );
        }
        return disConnect;
    }

    public PingReq pingReq() {
        if (pingReq == null) {
            pingReq = new PingReq();
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
            pubAck = new PubAck(
            // mqttDupPublishMessageStoreService
            );
        }
        return pubAck;
    }

    public PubRec pubRec() {
        if (pubRec == null) {
            pubRec = new PubRec(
            // mqttDupPublishMessageStoreService, mqttDupPubRelMessageStoreService
            );
        }
        return pubRec;
    }

    public PubComp pubComp() {
        if (pubComp == null) {
            pubComp = new PubComp(
            // mqttDupPubRelMessageStoreService
            );
        }
        return pubComp;
    }

    public MqttSessionService getMqttSessionStoreService() {
        return mqttSessionStoreService;
    }

}
