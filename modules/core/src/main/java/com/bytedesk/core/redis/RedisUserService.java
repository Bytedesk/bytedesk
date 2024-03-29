/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-27 10:54:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-26 13:20:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis;

import java.util.Set;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import com.bytedesk.core.constant.RedisConsts;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RedisUserService {

    private final StringRedisTemplate stringRedisTemplate;

    // private final RedisThreadService redisThreadService;

    public void addAccessToken(String username, String accessToken) {
        String key = RedisConsts.ACCESS_TOKEN_PREFIX + username;
        stringRedisTemplate.opsForSet().add(key, accessToken);
    }

    /**
     * 存储客服订阅的topic
     */

    // 判断用户是否被缓存
    @SuppressWarnings("null")
    public boolean hasTopics(@NonNull String uid) {
        return stringRedisTemplate.opsForSet().isMember(RedisConsts.USER_TOPIC_PREFIX + uid, uid);
    }

    // 存储用户topic
    public void addTopic(@NonNull String uid, @NonNull String topic) {
        stringRedisTemplate.opsForSet().add(RedisConsts.USER_TOPIC_PREFIX + uid, topic);
    }

    public void removeTopic(@NonNull String uid, @NonNull String topic) {
        stringRedisTemplate.opsForSet().remove(RedisConsts.USER_TOPIC_PREFIX + uid, topic);
    }

    public Set<String> getTopics(@NonNull String uid) {
        return stringRedisTemplate.opsForSet().members(RedisConsts.USER_TOPIC_PREFIX + uid);
    }

    // 判断客服账号是否被禁用
    // private static final String USER_DISABLED = "bytedeskim:user:disabled"; //
    // "bytedeskim:user:enabled";

    public void enable(@NonNull String uid) {
        stringRedisTemplate.opsForSet().remove(RedisConsts.USER_DISABLED, uid);
    }

    public void disable(@NonNull String uid) {
        stringRedisTemplate.opsForSet().add(RedisConsts.USER_DISABLED, uid);
    }

    // 判断用户是否被禁用
    @SuppressWarnings("null")
    public boolean isDisabled(@NonNull String uid) {
        return stringRedisTemplate.opsForSet().isMember(RedisConsts.USER_DISABLED, uid);
    }

}
