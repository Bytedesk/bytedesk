/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-20 16:20:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-04 16:36:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.redis.RedisConsts;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MqttConnectionService {

    private final StringRedisTemplate stringRedisTemplate;

    public void addConnected(String clientId) {
        if (!StringUtils.hasText(clientId)) {
            return;
        }
        // 获取当前时间戳，并加上60秒
        long expireAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(60);
        // 将clientId添加到有序集合中，并设置其分数为过期时间戳
        stringRedisTemplate.opsForZSet().add(RedisConsts.CONNECTED_MQTT_CLIENT_IDS, clientId, expireAt);
    }

    public void disconnected(String clientId) {
        if (!StringUtils.hasText(clientId)) {
            return;
        }
        stringRedisTemplate.opsForZSet().remove(RedisConsts.CONNECTED_MQTT_CLIENT_IDS, clientId);
    }

    public Boolean isConnected(String clientId) {
        if (!StringUtils.hasText(clientId)) {
            return false;
        }
        Double score = stringRedisTemplate.opsForZSet().score(RedisConsts.CONNECTED_MQTT_CLIENT_IDS, clientId);
        if (score == null) {
            return false;
        }
        // 检查当前时间是否超过了分数（过期时间戳）
        return System.currentTimeMillis() < score;
    }

    // 定期清理过期的clientId
    public void cleanExpiredClients() {
        long now = System.currentTimeMillis();
        // 删除有序集合中分数在当前时间之前的元素
        stringRedisTemplate.opsForZSet().removeRangeByScore(RedisConsts.CONNECTED_MQTT_CLIENT_IDS, 0, now);
    }

    // 获取所有clientIds
    // public String[] isConnectedClientIds() {
    //     return stringRedisTemplate.opsForZSet().range(RedisConsts.CONNECTED_MQTT_CLIENT_IDS, 0, -1).toArray(new String[0]);
    // }

    // 获取所有已连接的clientId
    public Set<String> getConnectedClientIds() {
        long now = System.currentTimeMillis();
        // 获取所有未过期的clientId
        return stringRedisTemplate.opsForZSet().rangeByScore(RedisConsts.CONNECTED_MQTT_CLIENT_IDS, now, Double.MAX_VALUE);
    }

    public Set<String> getConnectedUserUids() {
        // 用户clientId格式: userUid/client/deviceUid
        // 将clientId按/分割，取第一个元素为userUid
        Set<String> clientIds = getConnectedClientIds();
        return clientIds.stream().map(clientId -> clientId.split("/")[0]).collect(Collectors.toSet());
    }
    
    
}
