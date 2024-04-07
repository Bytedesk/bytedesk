/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-28 14:35:28
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

import org.springframework.stereotype.Service;

import com.bytedesk.socket.protobuf.config.ProtobufRedisTemplate;
import com.bytedesk.socket.protobuf.constant.ProtobufConsts;
import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

/**
 * 暂时缓存，定时存储到MySQL数据库
 */
// @Slf4j
@Service
@AllArgsConstructor
public class RedisMessageCacheProtobufService {

    private final ProtobufRedisTemplate protobufRedisTemplate;
    
    //
    public void push(final byte[] bytes) {
        protobufRedisTemplate.opsForList().rightPush(ProtobufConsts.MESSAGE_PREFIX, bytes);
    }

    public byte[] pop() {
        return (byte[]) protobufRedisTemplate.opsForList().leftPop(ProtobufConsts.MESSAGE_PREFIX);
    }

    public Long length() {
        return protobufRedisTemplate.opsForList().size(ProtobufConsts.MESSAGE_PREFIX);
    }

    public void clear() {
        while (length() > 0) {
            pop();
        }
    }
}