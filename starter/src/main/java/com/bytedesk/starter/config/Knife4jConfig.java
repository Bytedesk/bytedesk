/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 12:33:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-03 15:18:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.config;

import org.springframework.context.annotation.Configuration;

/**
 * http://127.0.0.1:9003/doc.html#/home
 * http://127.0.0.1:9003/swagger-ui/index.html
 * 
 * https://doc.xiaominfo.com/docs/quick-start
 * https://github.com/xiaoymin/knife4j?tab=readme-ov-file
 */
@Configuration
public class Knife4jConfig {

	// @Value("${application.version}")
    // private String version;
    
    // @Value("${server.port}")
    // private String port;

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
    
    // @Bean
	// @Primary
    // public OpenAPI customOpenAPI() {
    //     return new OpenAPI()
    //     .components(new Components()
    //                 .addSecuritySchemes("bearerAuth", new SecurityScheme()
    //                                     .type(SecurityScheme.Type.HTTP)
    //                                     .bearerFormat("JWT")
    //                                     .in(SecurityScheme.In.HEADER)
    //                                     .name("Authorization")
    //                                     .scheme("bearer")))
    //     .info(new Info()
    //           .title("Bytedesk API")
    //           .version(version)
    //           .description("Team IM && AI Customer Service")
    //           .license(new License()
    //                    .name("BSL 1.1")
    //                    .url("https://github.com/Bytedesk/bytedesk/blob/main/LICENSE")));
    // }
}
