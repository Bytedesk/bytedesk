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

import com.bytedesk.socket.mqtt.model.MqttSubscribe;
import com.bytedesk.socket.mqtt.util.MqttConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jackning
 */
@Service
public class MqttSubscribeNotWildcardCache {

    public static final String CACHE_PRE = MqttConsts.MQTT_PREFIX + "subnotwildcard:";

    public static final String CACHE_CLIENT_PRE = MqttConsts.MQTT_PREFIX + "client:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    // org.springframework.data.redis.serializer.SerializationException:
    // Cannot serialize; nested exception is
    // org.springframework.core.serializer.support.SerializationFailedException:
    // Failed to serialize object using DefaultSerializer;
    // FIXME: nested exception is java.lang.OutOfMemoryError: GC overhead limit
    // exceeded
    public MqttSubscribe put(String topic, String clientId, MqttSubscribe mqttSubscribe) {
        redisTemplate.opsForHash().put(CACHE_PRE + topic, clientId, mqttSubscribe);
        stringRedisTemplate.opsForSet().add(CACHE_CLIENT_PRE + clientId, topic);
        return mqttSubscribe;
    }

    public MqttSubscribe get(String topic, String clientId) {
        return (MqttSubscribe) redisTemplate.opsForHash().get(CACHE_PRE + topic, clientId);
    }

    public boolean containsKey(String topic, String clientId) {
        return redisTemplate.opsForHash().hasKey(CACHE_PRE + topic, clientId);
    }

    public void remove(String topic, String clientId) {
        stringRedisTemplate.opsForSet().remove(CACHE_CLIENT_PRE + clientId, topic);
        redisTemplate.opsForHash().delete(CACHE_PRE + topic, clientId);
    }

    public void removeForClient(String clientId) {
        for (String topic : stringRedisTemplate.opsForSet().members(CACHE_CLIENT_PRE + clientId)) {
            redisTemplate.opsForHash().delete(CACHE_PRE + topic, clientId);
        }
        stringRedisTemplate.delete(CACHE_CLIENT_PRE + clientId);
    }

    public Map<String, ConcurrentHashMap<String, MqttSubscribe>> all() {
        Map<String, ConcurrentHashMap<String, MqttSubscribe>> map = new HashMap<>();
        Set<String> set = redisTemplate.keys(CACHE_PRE + "*");
        if (set != null && !set.isEmpty()) {
            set.forEach(
                    entry -> {
                        ConcurrentHashMap<String, MqttSubscribe> map1 = new ConcurrentHashMap<>();
                        Map<Object, Object> map2 = redisTemplate.opsForHash().entries(entry);
                        if (map2 != null && !map2.isEmpty()) {
                            map2.forEach((k, v) -> {
                                map1.put((String) k, (MqttSubscribe) v);
                            });
                            map.put(entry.substring(CACHE_PRE.length()), map1);
                        }
                    });
        }
        return map;
    }

    public List<MqttSubscribe> all(String topic) {
        List<MqttSubscribe> list = new ArrayList<>();
        Map<Object, Object> map = redisTemplate.opsForHash().entries(CACHE_PRE + topic);
        if (map != null && !map.isEmpty()) {
            map.forEach((k, v) -> {
                list.add((MqttSubscribe) v);
            });
        }
        return list;
    }

}
