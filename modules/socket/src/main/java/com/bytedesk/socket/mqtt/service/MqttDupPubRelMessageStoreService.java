/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-22 15:05:42
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

import com.bytedesk.socket.mqtt.model.MqttDupPubRelMessage;
import com.bytedesk.socket.mqtt.service.MqttDupPubRelMessageStoreService;
import com.bytedesk.socket.mqtt.redis.MqttDupPubRelMessageCache;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class MqttDupPubRelMessageStoreService {

    private final MqttDupPubRelMessageCache mqttDupPubRelMessageCache;

    public void put(String clientId, MqttDupPubRelMessage dupPubRelMessageStore) {
        mqttDupPubRelMessageCache.put(clientId, dupPubRelMessageStore.getMessageId(), dupPubRelMessageStore);
    }

    public List<MqttDupPubRelMessage> get(String clientId) {
        if (mqttDupPubRelMessageCache.containsKey(clientId)) {
            ConcurrentHashMap<Integer, MqttDupPubRelMessage> map = mqttDupPubRelMessageCache.get(clientId);
            Collection<MqttDupPubRelMessage> collection = map.values();
            return new ArrayList<>(collection);
        }
        return new ArrayList<>();
    }

    public void remove(String clientId, int messageId) {
        mqttDupPubRelMessageCache.remove(clientId, messageId);
    }

    public void removeByClient(String clientId) {
        if (mqttDupPubRelMessageCache.containsKey(clientId)) {
            mqttDupPubRelMessageCache.remove(clientId);
        }
    }

}
