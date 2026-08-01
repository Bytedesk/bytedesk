/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-19 11:39:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 22:09:32
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

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import lombok.extern.slf4j.Slf4j;

import com.bytedesk.core.config.properties.BytedeskProperties;

// @Slf4j
@Configuration
@Description("CORS Configuration - Cross-Origin Resource Sharing configuration for handling cross-domain requests")
public class CorsConfig {

    private final BytedeskProperties bytedeskProperties;

    public CorsConfig(BytedeskProperties bytedeskProperties) {
        this.bytedeskProperties = bytedeskProperties;
    }

    /**
     * 经测试：仅有此处起作用，corsFilter()和WebMvcConfig.addCorsMappings()不起作用
     * 
     * @return
     */
    // https://docs.spring.io/spring-security/reference/reactive/channels/cors.html
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(resolveAllowedOriginPatterns());
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("X-Request-Id"));
        // configuration.setAllowedMethods(List.of("GET", "POST", "PUT", DELETE, "OPTIONS"));
        // configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        //
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private List<String> resolveAllowedOriginPatterns() {
        String configuredOrigins = bytedeskProperties.getCorsAllowedOrigins();
        if (!StringUtils.hasText(configuredOrigins)) {
            return List.of("*");
        }

        List<String> originPatterns = Arrays.stream(StringUtils.commaDelimitedListToStringArray(configuredOrigins))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();

        return originPatterns.isEmpty() ? List.of("*") : originPatterns;
    }

}