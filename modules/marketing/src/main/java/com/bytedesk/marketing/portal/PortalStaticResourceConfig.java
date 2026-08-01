package com.bytedesk.marketing.portal;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bytedesk.kbase.kbase.KbaseProperties;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class PortalStaticResourceConfig implements WebMvcConfigurer {

    private final KbaseProperties kbaseProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String portalRoot = kbaseProperties.resolvePortalHtmlRootDir();
        if (!StringUtils.hasText(portalRoot)) {
            return;
        }
        // PortalStaticService 输出目录: ${resolvePortalHtmlRootDir}/**
        // 访问路径: /portal/**
        registry.addResourceHandler("/portal/**")
                .addResourceLocations("file:" + portalRoot);
    }
}
