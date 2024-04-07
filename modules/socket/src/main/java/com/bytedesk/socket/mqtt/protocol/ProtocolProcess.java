package com.bytedesk.socket.mqtt.protocol;

import com.bytedesk.core.event.BytedeskEventPublisher;
import com.bytedesk.core.redis.RedisUserService;

// import com.bytedesk.core.publisher.EventPublisher;
// import com.bytedesk.core.redis.RedisAutoReplyService;
// import com.bytedesk.core.redis.RedisBlockService;
// import com.bytedesk.core.redis.RedisConnectService;
// import com.bytedesk.core.redis.RedisHostService;
// import com.bytedesk.core.redis.RedisRoutingRoundRobin;
// import com.bytedesk.core.redis.RedisSettingService;
// import com.bytedesk.core.redis.RedisStatisticService;
// import com.bytedesk.core.redis.RedisUserService;
// import com.bytedesk.core.service.GroupService;
// import com.bytedesk.core.service.MessageService;
// import com.bytedesk.core.service.ThreadService;
// import com.bytedesk.core.service.UserService;

import com.bytedesk.socket.mqtt.service.*;
import com.bytedesk.socket.redis.RedisMessageCacheOfflineService;

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
    private MqttSessionStoreService mqttSessionStoreService;

    @Autowired
    private MqttSubscribeStoreService mqttSubscribeStoreService;

    @Autowired
    private MqttAuthService mqttAuthService;

    // @Autowired
    // private RedisBlockService redisBlockService;

    // @Autowired
    // private RedisAutoReplyService redisAutoReplyService;

    @Autowired
    private MqttMessageIdService mqttMessageIdService;

    @Autowired
    private MqttRetainMessageStoreService mqttRetainMessageStoreService;

    @Autowired
    private MqttDupPublishMessageStoreService mqttDupPublishMessageStoreService;

    @Autowired
    private MqttDupPubRelMessageStoreService mqttDupPubRelMessageStoreService;

    @Autowired
    private MqttClientIdStoreService mqttClientIdStoreService;

    @Autowired
    private BytedeskEventPublisher bytedeskEventPublisher;

    // @Autowired
    // private UserService userService;

    // @Autowired
    // private GroupService groupService;

    // @Autowired
    // private ThreadService threadService;

    // @Autowired
    // private MessageService messageService;

    @Autowired
    private RedisUserService redisUserService;

    // @Autowired
    // private RedisRoutingRoundRobin redisRoutingRoundRobin;

    // @Autowired
    // private RedisStatisticService redisStatisticService;

    // @Autowired
    // private RedisHostService redisHostService;

    // @Autowired
    // private RedisConnectService redisConnectService;

    // @Autowired
    // private RedisSettingService redisSettingService;

    @Autowired
    private RedisMessageCacheOfflineService redisMessageCacheOfflineService;

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
                    mqttDupPublishMessageStoreService,
                    mqttDupPubRelMessageStoreService,
                    mqttSubscribeStoreService,
                    mqttClientIdStoreService,
                    // userService, groupService, threadService,
                    // redisUserService, redisRoutingRoundRobin, redisStatisticService,
                    // redisHostService,
                    // redisConnectService,
                    // messageService,
                    redisUserService,
                    redisMessageCacheOfflineService);
        }
        return connect;
    }

    public Subscribe subscribe() {
        if (subscribe == null) {
            subscribe = new Subscribe(
                mqttMessageIdService,
                mqttSubscribeStoreService,
                mqttRetainMessageStoreService
            // redisUserService
            );
        }
        return subscribe;
    }

    public UnSubscribe unSubscribe() {
        if (unSubscribe == null) {
            unSubscribe = new UnSubscribe(mqttSubscribeStoreService);
        }
        return unSubscribe;
    }

    public Publish publish() {
        if (publish == null) {
            publish = new Publish(
                mqttRetainMessageStoreService,
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
                mqttSubscribeStoreService,
                mqttDupPublishMessageStoreService,
                mqttDupPubRelMessageStoreService,
                mqttClientIdStoreService
            // userService,
            // redisUserService, redisRoutingRoundRobin, redisStatisticService,
            // redisHostService,
            // redisConnectService
            // messageService
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
            pubAck = new PubAck(mqttDupPublishMessageStoreService);
        }
        return pubAck;
    }

    public PubRec pubRec() {
        if (pubRec == null) {
            pubRec = new PubRec(mqttDupPublishMessageStoreService, mqttDupPubRelMessageStoreService);
        }
        return pubRec;
    }

    public PubComp pubComp() {
        if (pubComp == null) {
            pubComp = new PubComp(mqttDupPubRelMessageStoreService);
        }
        return pubComp;
    }

    public MqttSessionStoreService getMqttSessionStoreService() {
        return mqttSessionStoreService;
    }

    // public RedisUserService getRedisUserService() {
    // return redisUserService;
    // }

    // public RedisService getRedisService() {
    // return redisService;
    // }

    // public RedisStatisticService getRedisStatisticService() {
    // return redisStatisticService;
    // }

    // public RedisHostService getRedisHostService() {
    // return redisHostService;
    // }

    // public RedisConnectService getRedisConnectService() {
    // return redisConnectService;
    // }

}
