/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-26 10:47:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.redis;

import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

import com.bytedesk.socket.protobuf.config.ProtobufRedisTemplate;
import com.bytedesk.socket.protobuf.constant.ProtobufConsts;
import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

/**
 * 缓存离线消息，客户端重连之后，自动推送到客户端
 */
// @Slf4j
@Service
@AllArgsConstructor
public class RedisMessageCacheOfflineService {

    private final ProtobufRedisTemplate protobufRedisTemplate;

    // private static final String MESSAGE_OFFLINE_PREFIX =
    // "bytedeskim:message:offline:";

    public void push(String uid, final byte[] bytes) {
        protobufRedisTemplate.opsForList().rightPush(ProtobufConsts.MESSAGE_OFFLINE_PREFIX + uid, bytes);
        protobufRedisTemplate.expire(ProtobufConsts.MESSAGE_OFFLINE_PREFIX + uid, 7, TimeUnit.DAYS);
    }

    public byte[] pop(String uid) {
        return (byte[]) protobufRedisTemplate.opsForList().leftPop(ProtobufConsts.MESSAGE_OFFLINE_PREFIX + uid);
    }

    public Long length(String uid) {
        return protobufRedisTemplate.opsForList().size(ProtobufConsts.MESSAGE_OFFLINE_PREFIX + uid);
    }

    public void clear(String uid) {
        while (length(uid) > 0) {
            pop(uid);
        }
    }

}