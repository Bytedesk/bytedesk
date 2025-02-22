/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-22 11:54:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-22 12:37:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.config;

import org.springframework.context.annotation.Configuration;

/**
 * http://127.0.0.1:9003/swagger-ui/index.html
 */
@Configuration
public class SpringDocConfig {

    // @Value("${application.version}")
    // private String version;
    
    // @Value("${server.port}")
    // private String port;
    
    // @Bean
    // public OpenAPI customOpenApi() {
    //     return new OpenAPI()
    //     .components(new Components()
    //                 .addSecuritySchemes("openApiSecurityScheme", new SecurityScheme()
    //                                     .type(SecurityScheme.Type.HTTP)
    //                                     .bearerFormat("JWT")
    //                                     .in(SecurityScheme.In.HEADER)
    //                                     .name("Authorization")
    //                                     .scheme("Bearer")))
    //     // set title and version
    //     .info(new Info()
    //           .title("Bytedesk API")
    //           .version(version)
    //           .description("Team IM && AI Customer Service")
    //           .license(new License()
    //                    .name("Apache 2.0")
    //                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    // }
}

