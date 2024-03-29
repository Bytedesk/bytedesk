/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-27 18:45:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-29 10:56:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.cache;

// import java.time.Duration;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
// import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

/**
 * cache config
 * https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/#io.caching
 * https://www.51cto.com/article/753777.html
 */
@Configuration
@EnableCaching
public class CacheConfig {

    // @Autowired
    // private BytedeskProperties bytedeskProperties;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    // @Value("${cache.caffeine.maximumSize}")
    // private int maximumSize;

    // @Value("${cache.caffeine.expireAfterWriteSeconds}")
    // private int expireAfterWriteSeconds;

    // @Value("${spring.cache.redis.time-to-live}")
    // private long redisTimeToLiveSeconds;

    // @Bean
    // public CacheManager cacheManager() {
    //     if (bytedeskProperties.getCacheLevel() == 0) {
    //         return new NoOpCacheManager();
    //     } else if (bytedeskProperties.getCacheLevel() == 1) {
    //         return caffeineCacheManager();
    //     } else {
    //         return compositeCacheManager(caffeineCacheManager(), redisCacheManager());
    //     }
    // }

    // @SuppressWarnings("null")
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    // private Caffeine<Object, Object> caffeineCacheBuilder() {
    //     return Caffeine.newBuilder()
    //             .expireAfterWrite(expireAfterWriteSeconds, TimeUnit.SECONDS)
    //             .maximumSize(maximumSize);
    // }

    /**
     * https://docs.spring.io/spring-data/redis/reference/redis/redis-cache.html
     * 使用自定义rediscache时，遇到下面错误：
     * FIXME: SerializationException: Could not read JSON:failed to lazily initialize a collection: could not initialize proxy - no Session 
     * @return
     */
    // @Bean
    @SuppressWarnings("null")
    public RedisCacheManager redisCacheManager() {
        return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(cacheConfiguration())
        .build();
    }
   
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
    
    /**
     *  FIXME: 二级缓存未触发redis缓存？
     * @param caffeineCacheManager
     * @param redisCacheManager
     * @return
     */
    @SuppressWarnings("null")
    public CompositeCacheManager compositeCacheManager(CaffeineCacheManager caffeineCacheManager,
            RedisCacheManager redisCacheManager) {
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
        // Arrays.asList(caffeineCacheManager, redisCacheManager) 中caffeineCacheManager放在前面，
        // 即先查询caffeineCacheManager缓存，未命中则查询redisCacheManager。顺序很重要，别搞错
        compositeCacheManager.setCacheManagers(Arrays.asList(caffeineCacheManager, redisCacheManager));
        compositeCacheManager.setFallbackToNoOpCache(false); // 关闭缓存未命中时自动创建的空缓存
        return compositeCacheManager;
    }
    
}
