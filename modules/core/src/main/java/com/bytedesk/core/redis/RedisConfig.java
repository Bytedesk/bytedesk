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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import redis.clients.jedis.ConnectionPoolConfig;

// https://docs.spring.io/spring-data/redis/reference/redis/getting-started.html
@Configuration
public class RedisConfig {

    private final JedisProperties jedisProperties;

    private final JedisPoolProperties jedisPoolProperties;

    private final RedisClusterSwitchProperties redisClusterSwitchProperties;
    
    // private final ObjectMapper objectMapperBean;

    public RedisConfig(JedisProperties jedisProperties,
            JedisPoolProperties jedisPoolProperties,
            RedisClusterSwitchProperties redisClusterSwitchProperties
            // @Qualifier("redisObjectMapper") ObjectMapper objectMapperBean
        ) {
        this.jedisProperties = jedisProperties;
        this.jedisPoolProperties = jedisPoolProperties;
        this.redisClusterSwitchProperties = redisClusterSwitchProperties;
        // this.objectMapperBean = objectMapperBean;
    }
    
    // https://github.com/redis/jedis
    // JedisPoolConfig is deprecated since Jedis 7.x — use ConnectionPoolConfig instead.
    @Bean
    public ConnectionPoolConfig jedisPoolConfig() {
        ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
        poolConfig.setMaxIdle(jedisPoolProperties.getMaxIdle());
        poolConfig.setMaxTotal(jedisPoolProperties.getMaxTotal());
        poolConfig.setMinIdle(jedisPoolProperties.getMinIdle());
        poolConfig.setMaxWait(Duration.ofMillis(jedisPoolProperties.getMaxWaitMillis()));
        poolConfig.setBlockWhenExhausted(jedisPoolProperties.isBlockWhenExhausted());
        return poolConfig;
    }

    // https://github.com/redis/jedis
    // https://docs.spring.io/spring-data/redis/reference/redis/connection-modes.html
    JedisConnectionFactory jedisConnectionFactory() {
        JedisClientConfiguration clientConfiguration = JedisClientConfiguration.builder()
                .usePooling()
                .poolConfig(jedisPoolConfig())
                .build();

        JedisConnectionFactory connectionFactory;
        if (isClusterEnabled()) {
            RedisClusterConfiguration clusterConfiguration = buildClusterConfiguration();
            connectionFactory = new JedisConnectionFactory(clusterConfiguration, clientConfiguration);
        } else {
            RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(
                    jedisProperties.getHost(), jedisProperties.getPort());
            if (StringUtils.hasText(jedisProperties.getPassword())) {
                standaloneConfiguration.setPassword(RedisPassword.of(jedisProperties.getPassword()));
            }
            standaloneConfiguration.setDatabase(jedisProperties.getDatabase());
            connectionFactory = new JedisConnectionFactory(standaloneConfiguration, clientConfiguration);
        }

        connectionFactory.setConvertPipelineAndTxResults(true);
        return connectionFactory;
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
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        
        GenericJacksonJsonRedisSerializer serializer = GenericJacksonJsonRedisSerializer.builder()
                .enableUnsafeDefaultTyping()
                .build();
        
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
    // // // GenericJacksonJsonRedisSerializer jackson2JsonRedisSerializer
    // public GenericJacksonJsonRedisSerializer jackson2JsonSerializer() {
    //     // 初始化objectmapper
    //     ObjectMapper objectMapper = new ObjectMapper();
    //     objectMapper.setSerializationInclusion(Include.NON_NULL);
    //     objectMapper.activateDefaultTyping(
    //         LaissezFaireSubTypeValidator.instance , 
    //         ObjectMapper.DefaultTyping.NON_FINAL,
    //             JsonTypeInfo.As.WRAPPER_ARRAY);
    //     // 
    //     // return new Jackson2JsonRedisSerializer(objectMapper, Object.class);
    //     return new GenericJacksonJsonRedisSerializer(objectMapper);
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
    //         GenericJacksonJsonRedisSerializer jackson2JsonRedisSerializer) {

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
