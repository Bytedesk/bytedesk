/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-23 07:42:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-23 11:12:55
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;
// import com.fasterxml.jackson.databind.ObjectMapper;

import com.bytedesk.core.redis.RedisEvent;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

// https://howtodoinjava.com/spring-data/redis-streams-processing/
@Slf4j
@Service
public class RedisStreamListener implements StreamListener<String, ObjectRecord<String, RedisEvent>> {

    @Value("${bytedesk.cache.redis-stream-key}")
    private String streamKey;

    // @Autowired
    // private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    @SneakyThrows
    public void onMessage(ObjectRecord<String, RedisEvent> record) {

        RedisEvent redisEvent = record.getValue();
        log.info("onMessage streamKey: {}，redisEvent: {}", streamKey, redisEvent.toString());

        // redisTemplate.opsForValue().set(redisEvent.getContent(),
        //         objectMapper.writeValueAsString(redisEvent));

        // 确认消息已消费，从redis pending list中删除
        redisTemplate.opsForStream().acknowledge(streamKey, record);
    }

}


// https://docs.spring.io/spring-data/redis/reference/redis/redis-streams.html#redis.streams.receive.containers
// public class RedisStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

//     @Override
//     public void onMessage(MapRecord<String, String, String> message) {

//         System.out.println("MessageId: " + message.getId());
//         System.out.println("Stream: " + message.getStream());
//         System.out.println("Body: " + message.getValue());

//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'onMessage'");
//     }
    
// }

