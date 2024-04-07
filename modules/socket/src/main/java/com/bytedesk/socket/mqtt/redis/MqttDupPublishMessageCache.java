/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-29 10:22:04
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

import com.bytedesk.socket.mqtt.model.MqttDupPublishMessage;
import com.bytedesk.socket.mqtt.util.MqttConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jackning
 */
@Service
public class MqttDupPublishMessageCache {

    private final static String CACHE_PRE = MqttConsts.MQTT_PREFIX + "publish:";

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    public MqttDupPublishMessage put(String clientId, Integer messageId, MqttDupPublishMessage dupPublishMessageStore) {

        redisTemplate.opsForHash().put(CACHE_PRE + clientId, String.valueOf(messageId), dupPublishMessageStore);

        return dupPublishMessageStore;
    }

    public ConcurrentHashMap<String, MqttDupPublishMessage> get(String clientId) {

        ConcurrentHashMap<String, MqttDupPublishMessage> map = new ConcurrentHashMap<>();

        Map<Object, Object> map1 = redisTemplate.opsForHash().entries(CACHE_PRE + clientId);

        if (map1 != null && !map1.isEmpty()) {
            map1.forEach((k, v) -> {
                // FIXME: java.lang.ClassCastException: java.lang.String cannot be cast to
                // java.lang.Integer
                map.put((String) k, (MqttDupPublishMessage) v);
            });
        }

        return map;
    }

    public boolean containsKey(String clientId) {
        return redisTemplate.hasKey(CACHE_PRE + clientId);
    }

    public void remove(String clientId, Integer messageId) {
        redisTemplate.opsForHash().delete(CACHE_PRE + clientId, String.valueOf(messageId));
    }

    public void remove(String clientId) {
        redisTemplate.delete(CACHE_PRE + clientId);
    }

}
