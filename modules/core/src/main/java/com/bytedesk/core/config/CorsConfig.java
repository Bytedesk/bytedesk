/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-19 11:39:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-19 16:20:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
//  */
package com.bytedesk.core.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Configuration
public class CorsConfig {

    /**
     * 经测试：仅有此处起作用，corsFilter()和WebMvcConfig.addCorsMappings()不起作用
     * @return
     */
     // https://docs.spring.io/spring-security/reference/reactive/integrations/cors.html
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // configuration.setAllowCredentials(true); // 不能启用
        configuration.setAllowedOrigins(List.of("*")); // 必须启用
        // configuration.setAllowedOriginPatterns(List.of("*")); // 不能启用
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        // configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // 
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

//     @Bean
//     public CorsFilter corsFilter() {
//         log.info("CorsConfig.corsFilter()");
//         CorsConfiguration corsConfiguration = new CorsConfiguration();
//         //1,允许任何来源
//         corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
//         //2,允许任何请求头
//         corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
//         //3,允许任何方法
//         corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
//         //4,允许凭证
//         corsConfiguration.setAllowCredentials(true);
//         // 
//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", corsConfiguration);
//         // 
//         return new CorsFilter(source);
//     }
}