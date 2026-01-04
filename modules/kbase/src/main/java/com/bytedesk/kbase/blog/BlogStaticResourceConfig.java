package com.bytedesk.kbase.blog;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bytedesk.kbase.kbase.KbaseProperties;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class BlogStaticResourceConfig implements WebMvcConfigurer {

    private final KbaseProperties kbaseProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String blogRoot = kbaseProperties.resolveBlogHtmlRootDir();
        if (!StringUtils.hasText(blogRoot)) {
            return;
        }
        // BlogStaticService 输出目录: ${resolveBlogHtmlRootDir}/**
        // 访问路径: /blog/**
        registry.addResourceHandler("/blog/**")
                .addResourceLocations("file:" + blogRoot);
    }
}
