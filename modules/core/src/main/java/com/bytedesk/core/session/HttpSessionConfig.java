/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-05 00:04:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-13 14:25:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
// package com.bytedesk.core.session;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
// import org.springframework.data.redis.serializer.RedisSerializer;
// import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

// /**
//  * https://docs.spring.io/spring-session/reference/guides/boot-redis.html
//  * 
//  * @description 
//  * @author jackning
//  * @date 2024-04-05 00:15:55
//  */
// @Configuration
// @EnableRedisHttpSession(redisNamespace = "bytedeskim")
// public class HttpSessionConfig {
    
//     @Bean
//     public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
//         return new GenericJackson2JsonRedisSerializer();
//     }

//     /**
//      * Custom Cookie
//      * https://docs.spring.io/spring-session/reference/guides/java-custom-cookie.html#custom-cookie-sample
//      * @return
//      */
//     // @Bean
// 	// public CookieSerializer cookieSerializer() {
// 	// 	DefaultCookieSerializer serializer = new DefaultCookieSerializer();
// 	// 	serializer.setCookieName("JSESSIONID"); 
// 	// 	serializer.setCookiePath("/"); 
// 	// 	serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$"); 
// 	// 	return serializer;
// 	// }

// }


// <!-- <dependency>
//             <groupId>org.springframework.session</groupId>
//             <artifactId>spring-session-data-redis</artifactId>
//             <scope>provided</scope>
//         </dependency> -->