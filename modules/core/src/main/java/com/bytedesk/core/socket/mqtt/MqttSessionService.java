/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-30 14:18:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话存储接口类
 *
 */
// @Slf4j
@Service
@AllArgsConstructor
public class MqttSessionService {

    private Map<String, MqttSession> clientidSessionMap = new ConcurrentHashMap<>();

    public void put(String clientId, MqttSession mqttSession) {
        clientidSessionMap.put(clientId, mqttSession);
    }

    public MqttSession get(String clientId) {
        return clientidSessionMap.get(clientId);
    }

    public boolean containsKey(String clientId) {
        return clientidSessionMap.containsKey(clientId);
    }

    public void remove(String clientId) {
        clientidSessionMap.remove(clientId);
    }

    public List<String> getAllClientIds() {
        if (clientidSessionMap.keySet().size() > 0) {
            return new ArrayList<>(clientidSessionMap.keySet());
        } else {
            return new ArrayList<>();
        }
    }

}
