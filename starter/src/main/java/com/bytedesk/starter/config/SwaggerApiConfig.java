/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 11:10:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-24 11:10:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 分组和国际化支持配置
 */
@Configuration
public class SwaggerApiConfig {

    // 移除了 langParameterApi 方法，因为已经合并到 SpringDocConfig 中的 customOpenApi 方法了
    
    /**
     * API 分组示例 - 核心接口
     */
    @Bean
    public GroupedOpenApi coreApis() {
        return GroupedOpenApi.builder()
                .group("core-apis")
                .pathsToMatch("/api/v1/core/**")
                .build();
    }
    
    /**
     * API 分组示例 - 团队管理接口
     */
    @Bean
    public GroupedOpenApi teamApis() {
        return GroupedOpenApi.builder()
                .group("team-apis")
                .pathsToMatch("/api/v1/team/**", "/api/v1/group/**")
                .build();
    }
    
    /**
     * API 分组示例 - 聊天接口
     */
    @Bean
    public GroupedOpenApi chatApis() {
        return GroupedOpenApi.builder()
                .group("chat-apis")
                .pathsToMatch("/api/v1/chat/**", "/api/v1/message/**")
                .build();
    }
}
