// package com.bytedesk.ai.config;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.context.properties.EnableConfigurationProperties;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
// import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

// @EnableConfigurationProperties({ RedisProperies.class })
// @Configuration
// public class RedisConfig {
    
//     @Autowired
//     private RedisProperies redisProperies;

//     @Bean
//     JedisConnectionFactory jedisConnectionFactory() {
//         RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(redisProperies.getHost(), redisProperies.getPort());
//         standaloneConfiguration.setPassword(redisProperies.getPassword());
//         standaloneConfiguration.setDatabase(redisProperies.getDatabase());
//         return new JedisConnectionFactory(standaloneConfiguration);
//     }
// }
