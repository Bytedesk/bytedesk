/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-27 18:45:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-19 13:06:35
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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CachingConfigurer;
// import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * cache config
 * https://docs.spring.io/spring-boot/reference/io/caching.html
 * https://docs.spring.io/spring-data/redis/reference/redis/redis-cache.html
 * https://docs.spring.io/spring-framework/reference/integration/cache/annotations.html#cache-spel-context
 * https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/#io.caching
 * https://www.51cto.com/article/753777.html
 */
@Configuration
// @EnableCaching // 开启缓存
public class RedisCacheConfig implements CachingConfigurer {

    // @Bean
    // public Caffeine<Object, Object> caffeineConfig() {
    // return Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS);
    // }

    // @Bean
    // public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
    // CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    // caffeineCacheManager.setCaffeine(caffeine);
    // return caffeineCacheManager;
    // }

    // @Autowired
    // private BytedeskProperties bytedeskProperties;

    // @Autowired
    // private RedisConnectionFactory redisConnectionFactory;

    // @Value("${cache.caffeine.maximumSize}")
    // private Integer maximumSize;

    // @Value("${cache.caffeine.expireAfterWriteSeconds}")
    // private Integer expireAfterWriteSeconds;

    // @Value("${spring.cache.redis.time-to-live}")
    // private long redisTimeToLiveSeconds;

    // @Bean
    // public CacheManager cacheManager(RedisConnectionFactory
    // redisConnectionFactory,
    // GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
    // if (bytedeskProperties.getCacheLevel() == 0) {
    // return new NoOpCacheManager();
    // } else if (bytedeskProperties.getCacheLevel() == 1) {
    // return caffeineCacheManager();
    // } else {
    // return compositeCacheManager(caffeineCacheManager(),
    // redisCacheManager(redisConnectionFactory, jackson2JsonRedisSerializer));
    // }
    // }

    // public CaffeineCacheManager caffeineCacheManager() {
    // CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    // // cacheManager.setCaffeine(caffeineCacheBuilder());
    // return cacheManager;
    // }

    // // private Caffeine<Object, Object> caffeineCacheBuilder() {
    // // return Caffeine.newBuilder()
    // // .expireAfterWrite(expireAfterWriteSeconds, TimeUnit.SECONDS)
    // // .maximumSize(maximumSize);
    // // }

    // /**
    // * https://docs.spring.io/spring-data/redis/reference/redis/redis-cache.html
    // * 使用自定义redisCache时，遇到下面错误：
    // * FIXME: SerializationException: Could not read JSON:failed to lazily
    // initialize a collection: could not initialize proxy - no Session
    // * @return
    // */
    @Bean
    @ConditionalOnProperty(name ="spring.cache.type", havingValue = "redis")
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaults = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofDays(2))
                .enableTimeToIdle();
        return RedisCacheManager.builder(redisConnectionFactory)
                // .cacheDefaults(cacheConfiguration())
                .cacheDefaults(defaults)
                .build();
    }

    // public RedisCacheConfiguration cacheConfiguration() {
    //     return RedisCacheConfiguration.defaultCacheConfig()
    //             //  .entryTtl(Duration.ofMinutes(connectionProperties.getTimeToLive()))
    //             .disableCachingNullValues()
    //             .serializeValuesWith(RedisSerializationContext.SerializationPair
    //                     .fromSerializer(new CustomRedisSerializer()));
    //     // return RedisCacheConfiguration.defaultCacheConfig()
    //     //         .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()));
    //     // .serializeValuesWith(SerializationPair.fromSerializer(jackson2JsonRedisSerializer));
    //     // .serializeValuesWith(SerializationPair.fromSerializer(new
    //     // GenericJackson2JsonRedisSerializer()));
    // }

    // /**
    // * FIXME: 二级缓存未触发redis缓存？
    // * @param caffeineCacheManager
    // * @param redisCacheManager
    // * @return
    // */
    // // @SuppressWarnings("null")
    // public CompositeCacheManager compositeCacheManager(CaffeineCacheManager
    // caffeineCacheManager,
    // RedisCacheManager redisCacheManager) {
    // CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
    // // Arrays.asList(caffeineCacheManager, redisCacheManager)
    // 中caffeineCacheManager放在前面，
    // // 即先查询caffeineCacheManager缓存，未命中则查询redisCacheManager。顺序很重要，别搞错
    // compositeCacheManager.setCacheManagers(Arrays.asList(caffeineCacheManager,
    // redisCacheManager));
    // compositeCacheManager.setFallbackToNoOpCache(false); // 关闭缓存未命中时自动创建的空缓存
    // return compositeCacheManager;
    // }

    @Override
    public CacheErrorHandler errorHandler() {
        return new RedisCacheErrorHandler();
    }

}
