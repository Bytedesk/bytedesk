/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-27 18:45:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-29 15:58:26
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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
// import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import tools.jackson.datatype.hibernate7.Hibernate7Module;

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
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class RedisCacheConfig implements CachingConfigurer {

    @Value("${spring.cache.redis.time-to-live:1800000}")
    private long timeToLive;

    @Value("${spring.cache.redis.key-prefix:bytedeskim:cache:}")
    private String keyPrefix;
    
    // Spring Data Redis 4.1.0 uses tools.jackson (Jackson 3.x), incompatible with
    // com.fasterxml.jackson (Jackson 2.x) ObjectMapper. BaseResponse fix instead.
    // private final ObjectMapper redisObjectMapper;

    // public RedisCacheConfig(
    //     @Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper
    // ) {
    //     this.redisObjectMapper = redisObjectMapper;
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
        cacheConfigurations.put("user", defaultCacheConfiguration().entryTtl(Duration.ofMinutes(30)));
        // 在线状态缓存 - 5秒过期（避免 presence 接口频繁扫库）
        cacheConfigurations.put("presence", defaultCacheConfiguration().entryTtl(Duration.ofSeconds(5)));
        // 活跃连接列表缓存 - 5秒过期
        cacheConfigurations.put("activeConnections", defaultCacheConfiguration().entryTtl(Duration.ofSeconds(5)));
        // 平台设置缓存 - 30分钟过期（用于邮件、短信、工单中心等平台级配置）
        cacheConfigurations.put("platformSettings", defaultCacheConfiguration().entryTtl(Duration.ofMinutes(30)));
        // 系统全局配置缓存 - 30分钟过期（品牌/协议等 KV 覆盖值，保存后逐次失效）
        cacheConfigurations.put("system_config", defaultCacheConfiguration().entryTtl(Duration.ofMinutes(30)));
        // 会话缓存 - 4小时过期
        // cacheConfigurations.put("token", defaultCacheConfiguration().entryTtl(Duration.ofHours(24)));
        // 
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware() // 支持事务
                .build();
    }
    
    /**
     * 默认缓存配置
     * 使用Jackson作为JSON序列化器，需要包含类型信息以便正确反序列化
     */
    private RedisCacheConfiguration defaultCacheConfiguration() {
        // 注册 Hibernate7 模块，解决缓存 JPA 实体时懒加载代理/集合无法反序列化的问题：
        // - 未初始化的懒加载代理/集合序列化为 null（无需数据库会话）
        // - PersistentSet/PersistentBag 等 Hibernate 集合替换为标准 JDK 集合，
        //   避免 JSON 中写入 org.hibernate.collection.spi.* 类型，
        //   反序列化时触发 "Cannot lazily initialize collection (no session)"
        Hibernate7Module hibernate7Module = new Hibernate7Module()
                .enable(Hibernate7Module.Feature.REPLACE_PERSISTENT_COLLECTIONS);
        GenericJacksonJsonRedisSerializer serializer = GenericJacksonJsonRedisSerializer.builder()
                .enableUnsafeDefaultTyping()
                .customize(mapperBuilder -> mapperBuilder.addModule(hibernate7Module))
                .build();

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
