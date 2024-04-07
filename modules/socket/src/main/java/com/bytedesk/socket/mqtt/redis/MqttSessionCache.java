/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-04 12:07:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.mqtt.redis;

import com.bytedesk.socket.mqtt.model.MqttSession;
import com.bytedesk.socket.mqtt.util.MqttConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 *
 * @author jackning
 */
@Service
public class MqttSessionCache {

    public static final String CACHE_PRE = MqttConsts.MQTT_PREFIX + "session:";

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    public MqttSession put(String clientId, MqttSession mqttSession) {

        redisTemplate.opsForValue().set(CACHE_PRE + clientId, mqttSession);

        return mqttSession;
    }

    public MqttSession get(String clientId) {

        return (MqttSession) redisTemplate.opsForValue().get(CACHE_PRE + clientId);
    }

    public boolean containsKey(String clientId) {

        return redisTemplate.hasKey(CACHE_PRE + clientId);
    }

    public void remove(String clientId) {

        redisTemplate.delete(CACHE_PRE + clientId);
    }

    public Map<String, MqttSession> all() {
        Map<String, MqttSession> map = new HashMap<>();
        Set<String> set = redisTemplate.keys(CACHE_PRE + "*");
        if (set != null && !set.isEmpty()) {
            set.forEach(entry -> map.put(entry.substring(CACHE_PRE.length()),
                    (MqttSession) redisTemplate.opsForValue().get(entry)));
        }
        return map;
    }
}
