/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-27 15:45:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-28 09:48:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
// import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.bytedesk.kbase.faq.FaqEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Configuration
public class RobotConfig {

    @Bean
    public RedisTemplate<String, FaqEntity> redisTemplateFaqEntity(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, FaqEntity> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Create base ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Configure ObjectMapper with JavaTimeModule and type handling
        objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY)
            .build();

        // Configure GenericJackson2JsonRedisSerializer with custom ObjectMapper
        GenericJackson2JsonRedisSerializer serializer = 
            new GenericJackson2JsonRedisSerializer(objectMapper);
        
        // Set serializers
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setDefaultSerializer(serializer);
        
        template.afterPropertiesSet();
        return template;
    }
    
}
