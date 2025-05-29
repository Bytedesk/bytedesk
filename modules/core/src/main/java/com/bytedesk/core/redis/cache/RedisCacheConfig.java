/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-27 18:45:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-29 11:04:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis.cache;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
// import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * cache config
 * https://docs.spring.io/spring-boot/reference/io/caching.html
 * https://docs.spring.io/spring-data/redis/reference/redis/redis-cache.html
 * https://docs.spring.io/spring-framework/reference/integration/cache/annotations.html#cache-spel-context
 * https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/#io.caching
 * https://www.51cto.com/article/753777.html
 */
@Slf4j
@Configuration
@EnableCaching // 开启缓存
public class RedisCacheConfig implements CachingConfigurer {

    @Value("${spring.cache.redis.time-to-live:1800000}")
    private long timeToLive;

    @Value("${spring.cache.redis.key-prefix:bytedeskim:cache:}")
    private String keyPrefix;
    
    @Autowired
    private ObjectMapper objectMapperBean;

    /**
     * 自定义Key生成器，用于@Cacheable注解
     * 格式: 类名::方法名:参数
     */
    // @Bean
    // public KeyGenerator keyGenerator() {
    //     return (target, method, params) -> {
    //         StringBuilder sb = new StringBuilder();
    //         sb.append(target.getClass().getName());
    //         sb.append("::");
    //         sb.append(method.getName());
    //         sb.append(":");
    //         for (Object param : params) {
    //             if (param != null) {
    //                 sb.append(param.toString());
    //                 sb.append("_");
    //             }
    //         }
    //         return sb.toString();
    //     };
    // }

    /**
     * Redis缓存管理器配置
     * 支持TTL和自定义序列化
     */
    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = false)
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        log.info("初始化Redis缓存管理器");
        // 默认配置
        RedisCacheConfiguration defaultConfig = defaultCacheConfiguration();
        // 自定义不同缓存空间的TTL
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        // 用户缓存配置 - 30分钟过期
        cacheConfigurations.put("userCache", defaultCacheConfiguration().entryTtl(Duration.ofMinutes(30)));
        // 会话缓存 - 4小时过期
        cacheConfigurations.put("token", defaultCacheConfiguration().entryTtl(Duration.ofHours(24)));
        // 
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware() // 支持事务
                .build();
    }
    
    /**
     * 默认缓存配置
     * 使用Jackson作为JSON序列化器
     */
    private RedisCacheConfiguration defaultCacheConfiguration() {
        // 使用共享的ObjectMapper配置
        ObjectMapper objectMapper = objectMapperBean;

        // 使用配置好的共享ObjectMapper创建序列化器
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMillis(timeToLive)) // 设置TTL
                .disableCachingNullValues() // 不缓存null值
                .computePrefixWith(cacheName -> keyPrefix + cacheName + ":") // 设置key前缀
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                ) // 使用StringRedisSerializer来序列化key
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer)
                ); // 使用配置好的序列化器来序列化value
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new RedisCacheErrorHandler();
    }
}
