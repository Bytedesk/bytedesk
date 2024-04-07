/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-22 15:05:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.mqtt.service;

import com.bytedesk.socket.mqtt.model.MqttDupPublishMessage;
import com.bytedesk.socket.mqtt.service.MqttDupPublishMessageStoreService;
import com.bytedesk.socket.mqtt.redis.MqttDupPublishMessageCache;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class MqttDupPublishMessageStoreService {

    private final MqttDupPublishMessageCache mqttDupPublishMessageCache;

    public void put(String clientId, MqttDupPublishMessage dupPublishMessageStore) {
        mqttDupPublishMessageCache.put(clientId, dupPublishMessageStore.getMessageId(), dupPublishMessageStore);
    }

    public List<MqttDupPublishMessage> get(String clientId) {
        if (mqttDupPublishMessageCache.containsKey(clientId)) {
            ConcurrentHashMap<String, MqttDupPublishMessage> map = mqttDupPublishMessageCache.get(clientId);
            Collection<MqttDupPublishMessage> collection = map.values();
            return new ArrayList<>(collection);
        }
        return new ArrayList<>();
    }

    public void remove(String clientId, int messageId) {
        mqttDupPublishMessageCache.remove(clientId, messageId);
    }

    public void removeByClient(String clientId) {
        mqttDupPublishMessageCache.remove(clientId);
    }
}
