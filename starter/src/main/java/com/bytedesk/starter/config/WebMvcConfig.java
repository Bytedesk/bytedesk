/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-26 15:28:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-27 13:16:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.config;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bytedesk.core.config.BytedeskProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // @Autowired
    // private UploadStorageProperties uploadStorageProperties;

    // @Value("${bytedesk.upload-dir}")
    // private static String uploadDir;

    @Autowired
    private BytedeskProperties bytedeskProperties;
    
    
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/",
            "classpath:/resources/",
            "classpath:/static/",
            "classpath:/templates/",
            // String.format("file:%s", uploadDir),
            // "file:/Users/ningjinpeng/Desktop/git/private/weiyu/server/upload/"
    };

    /**
     * 跨域配置: 允许跨域访问
     */
    // https://spring.io/guides/gs/rest-service-cors
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedOrigins(bytedeskProperties.getCorsAllowedOrigins());
    }

	/**
     * 静态资源的配置 - 使得可以从磁盘中读取 Html、图片、视频、音频等
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        for (String path : CLASSPATH_RESOURCE_LOCATIONS) {
            log.info("CLASSPATH_RESOURCE_LOCATIONS: {}", path);
        }
        /*
            配置server虚拟路径，handler为前台访问的URL目录，locations为files相对应的本地路径
            也就是说如果有一个 upload/avatar/aaa.png 请求，那程序会到后面的目录里面找aaa.png文件
            另外：如果项目中有使用Shiro，则还需要在Shiro里面配置过滤下
         */
		registry
            .addResourceHandler("/**")
            .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }

}
