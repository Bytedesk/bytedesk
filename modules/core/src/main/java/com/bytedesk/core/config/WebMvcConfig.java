/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-26 15:28:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 21:45:55
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

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bytedesk.core.ip_access.IpAccessInterceptor;

// import com.bytedesk.core.config.BytedeskProperties;

// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Configuration
@Description("Web MVC Configuration - Web MVC配置类，配置跨域、拦截器、消息转换器等")
public class WebMvcConfig implements WebMvcConfigurer {

    // https://www.baeldung.com/spring-mvc-static-resources
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/",
            "classpath:/resources/",
            "classpath:/static/",
            "classpath:/templates/",
            "classpath:/public/",
    };

    @Autowired
    private IpAccessInterceptor ipAccessInterceptor;

//     @Autowired
//     private BlackAccessInterceptor visitorAccessInterceptor;

    /**
     * @{VisitorAnonymousController}
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册IP访问拦截器
        registry.addInterceptor(ipAccessInterceptor)
                .addPathPatterns("/visitor/**"); // 仅拦截visitor相关接口，如需拦截其他接口，请直接在此数组中添加即可
                
        // 注册Visitor访问拦截器，只拦截visitor相关接口
        // registry.addInterceptor(visitorAccessInterceptor)
                // .addPathPatterns("/visitor/**"); // 仅拦截visitor相关接口，如需拦截其他接口，请直接在此数组中添加即可
    }

    /**
     * 跨域配置: 允许跨域访问
     * 在 CorsConfig.java中配置，此处没有必要？
     */
    // https://spring.io/guides/gs/rest-service-cors
    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    // //
    // registry.addMapping("/**")
    // .allowedMethods("*")
    // .allowedOriginPatterns("*")
    // // allow cookies
    // .allowCredentials(true);
    // }

    /**
     * https://www.baeldung.com/spring-mvc-static-resources
     * spring.mvc.static-path-pattern=/**
     * 静态资源的配置 - 使得可以从磁盘中读取 Html、图片、视频、音频等
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // for (String path : CLASSPATH_RESOURCE_LOCATIONS) {
        // log.info("CLASSPATH_RESOURCE_LOCATIONS: {}", path);
        // }
        registry.addResourceHandler("/**")
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }

}
