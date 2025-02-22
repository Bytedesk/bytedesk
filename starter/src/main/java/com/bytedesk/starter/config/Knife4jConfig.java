/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 12:33:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-22 13:17:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * http://127.0.0.1:9003/doc.html#/home
 * 
 * https://doc.xiaominfo.com/docs/quick-start
 * https://github.com/xiaoymin/knife4j?tab=readme-ov-file
 */
@Configuration
public class Knife4jConfig {

	@Value("${application.version}")
    private String version;
    
    @Value("${server.port}")
    private String port;

    /**
     * 根据@Tag 上的排序，写入x-order
     *
     * @return the global open api customizer
     */
    // @Bean
    // public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
    //     return openApi -> {
    //         if (openApi.getTags()!=null){
    //             openApi.getTags().forEach(tag -> {
    //                 Map<String,Object> map=new HashMap<>();
    //                 map.put("x-order", RandomUtil.randomInt(0,100));
    //                 tag.setExtensions(map);
    //             });
    //         }
    //         if(openApi.getPaths()!=null){
    //             openApi.addExtension("x-test123","333");
    //             openApi.getPaths().addExtension("x-abb",RandomUtil.randomInt(1,100));
    //         }
    //     };
    // }
    
    @Bean
	@Primary
    public OpenAPI customOpenApi() {
        return new OpenAPI()
        .components(new Components()
                    .addSecuritySchemes("openApiSecurityScheme", new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .scheme("Bearer")))
        // set title and version
        .info(new Info()
              .title("Bytedesk API")
              .version(version)
              .description("Team IM && AI Customer Service")
              .license(new License()
                       .name("Apache 2.0")
                       .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }

}
