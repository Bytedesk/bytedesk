/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-12 17:58:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-16 17:34:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.mqtt.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.event.MqttConnectedEvent;
import com.bytedesk.core.event.MqttDisconnectedEvent;
import com.bytedesk.core.uid.utils.NetUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author bytedesk.com on 2019-07-17
 */
@Slf4j
@Component
@AllArgsConstructor
public class MqttEventListener {

    @EventListener
    public void onMqttConnectedEvent(MqttConnectedEvent event) {
        String clientId = event.getClientId();
        // 用户clientId格式: uid/client
        final String uid = clientId.split("/")[0];
        log.info("onMqttConnectedEvent clientId {}, uid {}, hostname {}", clientId, uid, NetUtils.getHostname());
        // 
        // 更新在线状态
        // updateConnectedStatus(uid);
        // redis查询离线消息，并推送
        // sendOfflineMessage(uid);
    }

    @EventListener
    public void onMqttDisconnectedEvent(MqttDisconnectedEvent event) {
        String clientId = event.getClientId();
        log.info("onMqttDisconnectedEvent clientId {}", clientId);
        // 删除客户端id
        // mqttClientIdStoreService.remove(clientId);
        // 删除订阅
        // mqttSubscribeStoreService.removeForClient(clientId);
        // 删除topic
    }

    // @EventListener
    // public void onMqttSubscribeEvent(MqttSubscribeEvent event) {
    //     String uid = event.getUid();
    //     String topic = event.getTopic();
    //     log.info("onMqttSubscribeEvent uid {}, topic {}", uid, topic);
    //     //
    //     subscribe(uid, topic);
    // }

    // @EventListener
    // public void onMqttUnsubscribeEvent(MqttUnsubscribeEvent event) {
    //     String uid = event.getUid();
    //     String topic = event.getTopic();
    //     log.info("onMqttUnsubscribeEvent uid {}, topic {}", uid, topic);
    //     //
    //     unsubscribe(uid, topic);
    // }

    // 
    // private void subscribe(String uid, String topic) {
    //     // log.info("subscribe uid {}, topic {}", uid, topic);
    //     //
    //     MqttQoS qoS = MqttQoS.AT_LEAST_ONCE;
    //     topicService.subscribe(topic, uid, qoS.value());
    //     // List<String> clientIdList = mqttClientIdStoreService.get(uid);
    //     // clientIdList.forEach(clientId -> {
    //     //     // log.info("subscribe clientId {}", clientId);
    //     //     final MqttSubscribe subscribeStore = new MqttSubscribe(clientId, topic, qoS.value());
    //     //     mqttSubscribeStoreService.put(topic, subscribeStore);
    //     // });
    // }

    // private void unsubscribe(String uid, String topic) {
    //     // log.info("unsubscribe uid {}, topic {}", uid, topic);
    //     // redisUserService.removeTopic(uid, topic);
    //     //
    //     // List<String> clientIdList = mqttClientIdStoreService.get(uid);
    //     // clientIdList.forEach(clientId -> {
    //     //     // log.info("unsubscribe clientId {}", clientId);
    //     //     mqttSubscribeStoreService.remove(topic, clientId);
    //     // });
    // }


    
}
