/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-23 10:53:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-23 16:38:40
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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import com.bytedesk.core.redis.RedisEvent;

import lombok.extern.slf4j.Slf4j;

// https://howtodoinjava.com/spring-data/redis-streams-processing/
@Slf4j
@Configuration
public class RedisStreamConfig {
    
    @Value("${bytedesk.redis-stream-key}")
    private String streamKey;

    @Autowired
    private RedisStreamListener redisStreamListener;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Bean
    public Subscription subscription(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {

        String groupName = streamKey;

        try {
            // redisConnectionFactory.getConnection().streamCommands()
            //         .xGroupCreate(streamKey.getBytes(), groupName, ReadOffset.from("0-0"), true);
            redisTemplate.opsForStream().createGroup(streamKey, groupName);
        } catch (Exception e) {
            // 如果异常信息表明组已存在，则可以选择忽略或进行其他处理
            if (e.getMessage().contains("BUSYGROUP Consumer Group name already exists")) {
                // 组已存在，根据业务需求选择是否要抛出异常或进行其他处理
                log.info("Consumer group '{}' already exists for stream '{}'", groupName, streamKey);
            } else {
                // 如果是其他异常，则重新抛出
                throw e;
            }
        }

        StreamOffset<String> streamOffset = StreamOffset.create(streamKey, ReadOffset.lastConsumed());

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, RedisEvent>> options =
            StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofMillis(100))
                .targetType(RedisEvent.class)
                .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, RedisEvent>> container = StreamMessageListenerContainer
                .create(redisConnectionFactory, options);

        String hostName = InetAddress.getLocalHost().getHostName();
        Subscription subscription = container.receive(
                Consumer.from(streamKey, hostName),
                streamOffset, redisStreamListener);

        log.info("container start, hostName: {}", hostName);
        container.start();

        return subscription;
    }


}
