/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-27 15:45:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-27 15:47:24
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
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.bytedesk.kbase.faq.FaqEntity;

@Configuration
public class RobotConfig {

    @Bean
    public RedisTemplate<String, FaqEntity> redisTemplateFaqEntity(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, FaqEntity> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 设置序列化器
        Jackson2JsonRedisSerializer<FaqEntity> serializer = new Jackson2JsonRedisSerializer<>(FaqEntity.class);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }
    
}
