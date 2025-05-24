/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 11:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-24 12:21:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.swagger;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Swagger UI 配置，添加自定义资源和视图控制器
 */
@Configuration
public class SwaggerUIConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保自定义 JavaScript 文件可被访问
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 添加一个重定向，从 /api/docs 到 Swagger UI
        registry.addRedirectViewController("/api-docs", "/swagger-language.html");
        
        // 为API文档添加语言选择页面
        registry.addViewController("/api-language").setViewName("forward:/swagger-language.html");
    }
}
