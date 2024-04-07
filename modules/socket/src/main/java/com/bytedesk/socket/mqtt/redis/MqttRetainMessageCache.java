/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-29 10:41:15
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

import com.bytedesk.socket.mqtt.model.MqttRetainMessage;
import com.bytedesk.socket.mqtt.util.MqttConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author jackning
 */
@Service
public class MqttRetainMessageCache {

    public static final String CACHE_PRE = MqttConsts.MQTT_PREFIX + "retain:";

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    public MqttRetainMessage put(String topic, MqttRetainMessage retainMessageStore) {

        redisTemplate.opsForValue().set(CACHE_PRE + topic, retainMessageStore);

        return retainMessageStore;
    }

    public MqttRetainMessage get(String topic) {
        return (MqttRetainMessage) redisTemplate.opsForValue().get(CACHE_PRE + topic);
    }

    public boolean containsKey(String topic) {
        return redisTemplate.hasKey(CACHE_PRE + topic);
    }

    public void remove(String topic) {
        redisTemplate.delete(CACHE_PRE + topic);
    }

    public Map<String, MqttRetainMessage> all() {
        Map<String, MqttRetainMessage> map = new HashMap<>();
        Set<String> set = redisTemplate.keys(CACHE_PRE + "*");
        if (set != null && !set.isEmpty()) {
            set.forEach(entry -> map.put(entry.substring(CACHE_PRE.length()),
                    (MqttRetainMessage) redisTemplate.opsForValue().get(entry)));
        }
        return map;
    }
}
