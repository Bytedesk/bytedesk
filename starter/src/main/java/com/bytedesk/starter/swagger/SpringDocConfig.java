/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-22 11:54:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-24 15:15:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * http://127.0.0.1:9003/swagger-ui/index.html?lang=zh_CN
 * 支持国际化: 添加参数 ?lang=en|zh_CN|zh_TW
 */
@Configuration
public class SpringDocConfig implements WebMvcConfigurer {

    @Value("${application.version}")
    private String version;
    
    @Value("${server.port}")
    private String port;
    
    @Autowired
    private MessageSource messageSource;
    
    /**
     * 配置 OpenAPI Bean，并添加国际化和安全配置
     */
    @Bean
    public OpenAPI customOpenApi() {
        // 获取当前区域设置
        String title = messageSource.getMessage("swagger.title", null, LocaleContextHolder.getLocale());
        String description = messageSource.getMessage("swagger.description", null, LocaleContextHolder.getLocale());
        String licenseName = messageSource.getMessage("swagger.license", null, LocaleContextHolder.getLocale());
        String licenseUrl = messageSource.getMessage("swagger.license.url", null, LocaleContextHolder.getLocale());
        
        // 创建 OpenAPI
        OpenAPI openAPI = new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("openApiSecurityScheme", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .bearerFormat("JWT")
                    .in(SecurityScheme.In.HEADER)
                    .name("Authorization")
                    .scheme("Bearer")))
            // set title and version
            .info(new Info()
                .title(title)
                .version(version)
                .description(description)
                .license(new License()
                    .name(licenseName)
                    .url(licenseUrl)));
        
        // 添加全局语言参数，使Swagger UI可以在所有请求中包含语言参数
        Parameter langParameter = new Parameter()
            .in("query")
            .name("lang")
            .description("Language (en, zh_CN, zh_TW)")
            .schema(new io.swagger.v3.oas.models.media.StringSchema().type("string").example("en"))
            .required(false);
            
        // 初始化 components.parameters 如果需要
        if (openAPI.getComponents().getParameters() == null) {
            openAPI.getComponents().addParameters("lang", langParameter);
        } else {
            openAPI.getComponents().getParameters().put("lang", langParameter);
        }
        
        return openAPI;
    }
    
    /**
     * 配置 SwaggerWelcomeCommon Bean，解决 SwaggerWelcomeCommon 注入问题
     */
    @Bean
    public SwaggerWelcomeCommon swaggerWelcomeCommon(SwaggerUiConfigProperties swaggerUiConfigProperties,
                                                    SwaggerUiConfigParameters swaggerUiConfigParameters) {
        return new SwaggerWelcomeCommon(swaggerUiConfigProperties, swaggerUiConfigParameters);
    }
}

