/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-12 10:24:06
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-12 10:00:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.test.flux;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
// import org.springframework.data.redis.core.ReactiveRedisOperations;
// import org.springframework.data.redis.core.ReactiveRedisTemplate;
// import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
// import org.springframework.data.redis.serializer.RedisSerializationContext;
// import org.springframework.data.redis.serializer.StringRedisSerializer;

// @Configuration
// public class CoffeeConfiguration {
//     @Bean
//     ReactiveRedisOperations<String, Coffee> redisOperations(ReactiveRedisConnectionFactory factory) {
//         Jackson2JsonRedisSerializer<Coffee> serializer = new Jackson2JsonRedisSerializer<>(Coffee.class);

//         RedisSerializationContext.RedisSerializationContextBuilder<String, Coffee> builder = RedisSerializationContext
//                 .newSerializationContext(new StringRedisSerializer());

//         RedisSerializationContext<String, Coffee> context = builder.value(serializer).build();

//         return new ReactiveRedisTemplate<>(factory, context);
//     }

// }