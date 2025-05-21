/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 16:07:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@AllArgsConstructor
public class MqttSessionService {

    // considering cluster mode, use redis to store sessions
    private Map<String, MqttSession> clientIdSessionMap = new ConcurrentHashMap<>();

    public void put(String clientId, MqttSession mqttSession) {
        clientIdSessionMap.put(clientId, mqttSession);
    }

    public MqttSession get(String clientId) {
        return clientIdSessionMap.get(clientId);
    }

    public Boolean containsKey(String clientId) {
        return clientIdSessionMap.containsKey(clientId);
    }

    public void remove(String clientId) {
        if (clientIdSessionMap.containsKey(clientId)) {
            clientIdSessionMap.remove(clientId);
        } else {
            log.warn("Attempted to remove non-existent clientId: {}", clientId);
        }
    }

    public List<String> getAllClientIds() {
        if (clientIdSessionMap.keySet().size() > 0) {
            return new ArrayList<>(clientIdSessionMap.keySet());
        } else {
            return new ArrayList<>();
        }
    }

}
