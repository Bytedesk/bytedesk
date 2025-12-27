/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-26 15:28:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 22:08:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
//  */
package com.bytedesk.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Description("Core Web MVC Configuration - 核心Web MVC配置类，配置静态资源等基础功能")
public class WebMvcConfig implements WebMvcConfigurer {

    // https://www.baeldung.com/spring-mvc-static-resources
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/",
            "classpath:/resources/",
            "classpath:/static/",
            "classpath:/templates/",
            "classpath:/public/",
    };
    
    /**
     * https://www.baeldung.com/spring-mvc-static-resources
     * spring.mvc.static-path-pattern=/**
     * 静态资源的配置 - 使得可以从磁盘中读取 Html、图片、视频、音频等
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }

    // @Override
    // public void addFormatters(FormatterRegistry registry) {
    //     // 兼容前端在 query 参数中传递 superUser=undefined 的情况。
    //     // 这里将 "undefined"/"null"/空字符串 统一视为 false，避免参数绑定阶段报错。
    //     registry.addConverter(new Converter<String, Boolean>() {
    //         @Override
    //         public Boolean convert(String source) {
    //             if (source == null) {
    //                 return null;
    //             }
    //             String value = source.trim();
    //             if (value.isEmpty()) {
    //                 return Boolean.FALSE;
    //             }
    //             value = value.toLowerCase(Locale.ROOT);
    //             if ("undefined".equals(value) || "null".equals(value)) {
    //                 return Boolean.FALSE;
    //             }
    //             return switch (value) {
    //                 case "true", "on", "yes", "1" -> Boolean.TRUE;
    //                 case "false", "off", "no", "0" -> Boolean.FALSE;
    //                 default -> throw new IllegalArgumentException("Invalid boolean value [" + source + "]");
    //             };
    //         }
    //     });
    // }

}
