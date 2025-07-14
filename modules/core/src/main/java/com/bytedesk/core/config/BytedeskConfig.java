/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-30 07:52:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-19 11:45:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.bytedesk.core.utils.ApplicationContextHolder;

// import io.swagger.v3.oas.models.OpenAPI;
// import io.swagger.v3.oas.models.info.Info;
import lombok.Getter;

/**
 * @author bytedesk.com on 2018/12/29
 */
@Getter
@Configuration
@Description("Bytedesk Core Configuration - 核心配置类，提供 ModelMapper、密码编码器、认证管理器等基础Bean")
public class BytedeskConfig {

    @Value("${application.version}")
    private String appVersion;

    @Bean
    public ModelMapper modelMapper() {
        // return new ModelMapper();
        ModelMapper modelMapper = new ModelMapper();
        
        // 设置更严格的匹配策略
        modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .setSkipNullEnabled(true)
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        
        return modelMapper;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ApplicationEventPublisher applicationEventPublisher(ApplicationEventPublisher publisher) {
        return publisher;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory()); //
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        restTemplate.setInterceptors(Arrays.asList(new TraceIdInterceptor()));
        return restTemplate;
    }

    @Bean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    // @Bean
    // public OpenAPI apiInfo() {
    //     return new OpenAPI().info(new Info().title("bytedesk apis").version(appVersion));
    // }

    // @Bean
    // public GroupedOpenApi httpApi() {
    // return GroupedOpenApi.builder()
    // .group("http")
    // .pathsToMatch("/**")
    // .build();
    // }

}
