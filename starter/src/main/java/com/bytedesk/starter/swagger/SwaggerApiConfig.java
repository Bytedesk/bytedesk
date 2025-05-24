/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 11:10:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-24 14:59:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
// import org.springdoc.core.models.GroupedOpenApi;
// import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 分组和国际化支持配置
 */
@Configuration
public class SwaggerApiConfig {
    
    /**
     * Api分组接口 - 所有接口
     */
    @Bean
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("all-apis")
                .displayName("所有接口")
                .pathsToMatch("/api/v1/**")
                .build();
    }
    
    /**
     * 群组管理接口
     */
    @Bean
    public GroupedOpenApi groupApis() {
        return GroupedOpenApi.builder()
                .group("group-apis")
                .displayName("群组接口")
                .pathsToMatch( "/api/v1/group/**")
                .build();
    }

    /**
     * 部门管理接口
     */
    @Bean
    public GroupedOpenApi departmentApis() {
        return GroupedOpenApi.builder()
                .group("department-apis")
                .displayName("部门接口")
                .pathsToMatch("/api/v1/department/**")
                .build();
    }

    /**
     * 成员管理接口
     */
    @Bean
    public GroupedOpenApi memberApis() {
        return GroupedOpenApi.builder()
                .group("member-apis")
                .displayName("成员接口")
                .pathsToMatch("/api/v1/member/**")
                .build();
    }
    
}
