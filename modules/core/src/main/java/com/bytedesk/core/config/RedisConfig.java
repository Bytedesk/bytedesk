/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-29 12:01:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-29 12:10:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

// https://docs.spring.io/spring-data/redis/reference/redis/getting-started.html
@Configuration
public class RedisConfig {

    @Autowired
    private JedisProperties jedisProperties;

    // https://docs.spring.io/spring-data/redis/reference/redis/connection-modes.html
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
                jedisProperties.getHost(), jedisProperties.getPort());
        redisStandaloneConfiguration.setPassword(jedisProperties.getPassword());
        redisStandaloneConfiguration.setDatabase(jedisProperties.getDatabase());
        // 
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
