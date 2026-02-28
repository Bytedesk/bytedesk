/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-29 12:01:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-29 12:49:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

// https://docs.spring.io/spring-data/redis/reference/redis/getting-started.html
@Configuration
public class RedisConfig {

    @Autowired
    private JedisProperties jedisProperties;

    @Autowired
    private JedisPoolProperties jedisPoolProperties;

    @Autowired
    private RedisClusterSwitchProperties redisClusterSwitchProperties;
    
    @Autowired
    @Qualifier("redisObjectMapper")
    private ObjectMapper objectMapperBean;
    
    // https://github.com/redis/jedis
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(jedisPoolProperties.getMaxIdle());
        poolConfig.setMaxTotal(jedisPoolProperties.getMaxTotal());
        poolConfig.setMinIdle(jedisPoolProperties.getMinIdle());
        poolConfig.setMaxWait(Duration.ofMillis(jedisPoolProperties.getMaxWaitMillis()));
        poolConfig.setBlockWhenExhausted(jedisPoolProperties.isBlockWhenExhausted());
        return poolConfig;
    }

    public JedisPool getJedisPool() {
        return new JedisPool(jedisPoolConfig(), jedisProperties.getHost(), jedisProperties.getPort());
    }

    // https://github.com/redis/jedis
    // https://docs.spring.io/spring-data/redis/reference/redis/connection-modes.html
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisClientConfiguration clientConfiguration = JedisClientConfiguration.builder()
                .usePooling()
                .poolConfig(jedisPoolConfig())
                .build();

        if (isClusterEnabled()) {
            RedisClusterConfiguration clusterConfiguration = buildClusterConfiguration();
            return new JedisConnectionFactory(clusterConfiguration, clientConfiguration);
        }

        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(
                jedisProperties.getHost(), jedisProperties.getPort());
        if (StringUtils.hasText(jedisProperties.getPassword())) {
            standaloneConfiguration.setPassword(RedisPassword.of(jedisProperties.getPassword()));
        }
        standaloneConfiguration.setDatabase(jedisProperties.getDatabase());
        return new JedisConnectionFactory(standaloneConfiguration, clientConfiguration);
    }

    private boolean isClusterEnabled() {
        return redisClusterSwitchProperties.isEnabled()
                || !CollectionUtils.isEmpty(jedisProperties.getCluster().getNodes());
    }

    private RedisClusterConfiguration buildClusterConfiguration() {
        if (CollectionUtils.isEmpty(jedisProperties.getCluster().getNodes())) {
            throw new IllegalStateException(
                    "Redis cluster mode is enabled but spring.data.redis.cluster.nodes is empty");
        }

        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(jedisProperties.getCluster().getNodes());
        if (StringUtils.hasText(jedisProperties.getPassword())) {
            clusterConfiguration.setPassword(RedisPassword.of(jedisProperties.getPassword()));
        }
        if (jedisProperties.getCluster().getMaxRedirects() != null) {
            clusterConfiguration.setMaxRedirects(jedisProperties.getCluster().getMaxRedirects());
        }
        return clusterConfiguration;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        
        // 使用共享的ObjectMapper配置
        ObjectMapper objectMapper = objectMapperBean;
        
        // 使用配置好的共享ObjectMapper创建序列化器
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        
        // 设置序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);
        
        return redisTemplate;
    }


    
    /**
     * 序列化定制
     * 
     * @return
     */
    // @Bean
    // // // Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer
    // public GenericJackson2JsonRedisSerializer jackson2JsonSerializer() {
    //     // 初始化objectmapper
    //     ObjectMapper objectMapper = new ObjectMapper();
    //     objectMapper.setSerializationInclusion(Include.NON_NULL);
    //     objectMapper.activateDefaultTyping(
    //         LaissezFaireSubTypeValidator.instance , 
    //         ObjectMapper.DefaultTyping.NON_FINAL,
    //             JsonTypeInfo.As.WRAPPER_ARRAY);
    //     // 
    //     // return new Jackson2JsonRedisSerializer(objectMapper, Object.class);
    //     return new GenericJackson2JsonRedisSerializer(objectMapper);
    // }

    // /**
    //  * 操作模板
    //  * 
    //  * @param connectionFactory
    //  * @param jackson2JsonRedisSerializer
    //  * @return
    //  */
    // @Bean
    // // Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer
    // public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
    //         GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {

    //     RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
    //     redisTemplate.setConnectionFactory(connectionFactory);

    //     // 设置key/hashkey序列化
    //     RedisSerializer<String> stringSerializer = new StringRedisSerializer();
    //     redisTemplate.setKeySerializer(stringSerializer);
    //     redisTemplate.setHashKeySerializer(stringSerializer);

    //     // 设置值序列化
    //     redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
    //     redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
    //     redisTemplate.afterPropertiesSet();

    //     return redisTemplate;
    // }

}
