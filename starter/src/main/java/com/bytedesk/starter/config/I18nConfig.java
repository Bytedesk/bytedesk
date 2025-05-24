/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-24 12:14:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.config;

import java.util.Locale;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 国际化配置
 */
@Configuration
public class I18nConfig implements WebMvcConfigurer {

    /**
     * 定义消息源
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/messages", "i18n/swagger");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * 定义本地化解析器，使用cookie存储用户语言选择
     */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver("locale");
        localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE); // 默认语言为简体中文
        return localeResolver;
    }

    /**
     * 定义本地化拦截器，参数lang用于切换语言
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang"); // 请求参数名称，例如：?lang=zh_CN
        return lci;
    }

    /**
     * 注册本地化拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
    
    /**
     * 创建语言过滤器Bean
     */
    @Bean
    public FilterRegistrationBean<BrowserLanguageFilter> browserLanguageFilter() {
        FilterRegistrationBean<BrowserLanguageFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new BrowserLanguageFilter(localeResolver()));
        registrationBean.addUrlPatterns("/swagger-ui/*", "/v3/api-docs/*", "/api/*");
        registrationBean.setOrder(1); // 确保较高优先级
        return registrationBean;
    }
}
