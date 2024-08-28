/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-23 08:06:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-23 19:26:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis.stream;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bytedesk.core.redis.RedisEvent;

import lombok.extern.slf4j.Slf4j;

// https://howtodoinjava.com/spring-data/redis-streams-processing/
@Service
@Slf4j
public class RedisStreamService {

    @Value("${bytedesk.redis-stream-key}")
    private String streamKey;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public RecordId produce(RedisEvent redisEvent) {
        log.info("produce streamKey {}, redisEvent {}", streamKey, redisEvent.toString());

        ObjectRecord<String, RedisEvent> record = StreamRecords.newRecord()
                .ofObject(redisEvent)
                .withStreamKey(streamKey);

        RecordId recordId = this.redisTemplate.opsForStream()
                .add(record);
        log.info("recordId: {}", recordId);

        if (Objects.isNull(recordId)) {
            log.info("error sending event: {}", redisEvent);
            return null;
        }

        return recordId;
    }

}
