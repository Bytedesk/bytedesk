/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 11:40:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-24 11:59:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.swagger;

import java.util.Collections;
import java.util.List;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.tags.Tag;

/**
 * Swagger UI 自定义配置，支持国际化标签和描述
 */
@Configuration
public class SwaggerI18nConfig {
    
    @Autowired
    private MessageSource messageSource;
    
    /**
     * 自定义OpenAPI，实现标签国际化
     */
    @Bean
    public OpenApiCustomizer i18nOpenApiCustomizer() {
        return openApi -> {
            if (openApi.getTags() != null) {
                // 替换标签为国际化的版本
                List<Tag> tags = openApi.getTags();
                Collections.sort(tags, (Tag a, Tag b) -> a.getName().compareToIgnoreCase(b.getName()));
                
                for (Tag tag : tags) {
                    // 尝试翻译标签名称和描述
                    String i18nName = getI18nMessage("swagger.tag." + tag.getName() + ".name", tag.getName());
                    String i18nDescription = getI18nMessage("swagger.tag." + tag.getName() + ".description", 
                            tag.getDescription() != null ? tag.getDescription() : "");
                    
                    tag.setName(i18nName);
                    tag.setDescription(i18nDescription);
                }
            }
        };
    }
    
    /**
     * 自定义Operation，实现接口摘要和描述国际化
     */
    @Bean
    public OperationCustomizer combinedOperationCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            // 1. 处理操作ID的国际化
            String operationId = operation.getOperationId();
            if (operationId != null) {
                // 尝试翻译操作摘要
                String i18nSummary = getI18nMessage("swagger.operation." + operationId + ".summary", 
                        operation.getSummary() != null ? operation.getSummary() : "");
                
                // 尝试翻译操作描述
                String i18nDescription = getI18nMessage("swagger.operation." + operationId + ".description", 
                        operation.getDescription() != null ? operation.getDescription() : "");
                
                operation.setSummary(i18nSummary);
                operation.setDescription(i18nDescription);
            }
            
            // 2. 处理 @I18nApi 注解的国际化
            try {
                // 处理方法上的 I18nApi 注解
                var i18nApiAnnotation = handlerMethod.getMethodAnnotation(com.bytedesk.core.annotation.I18nApi.class);
                if (i18nApiAnnotation != null) {
                    // 设置国际化的摘要
                    if (!i18nApiAnnotation.summary().isEmpty()) {
                        operation.setSummary(getI18nMessage(i18nApiAnnotation.summary(), operation.getSummary()));
                    }
                    
                    // 设置国际化的描述
                    if (!i18nApiAnnotation.description().isEmpty()) {
                        operation.setDescription(getI18nMessage(i18nApiAnnotation.description(), operation.getDescription()));
                    }
                }
                
                // 处理类上的 I18nApi 注解
                var classAnnotation = handlerMethod.getBeanType().getAnnotation(com.bytedesk.core.annotation.I18nApi.class);
                if (classAnnotation != null) {
                    // 如果方法没有设置摘要，则使用类级别的配置
                    if (operation.getSummary() == null && !classAnnotation.summary().isEmpty()) {
                        operation.setSummary(getI18nMessage(classAnnotation.summary(), null));
                    }
                    
                    // 如果方法没有设置描述，则使用类级别的配置
                    if (operation.getDescription() == null && !classAnnotation.description().isEmpty()) {
                        operation.setDescription(getI18nMessage(classAnnotation.description(), null));
                    }
                }
            } catch (Exception e) {
                // 忽略类型找不到等异常
            }
            
            return operation;
        };
    }
    
    /**
     * 获取国际化消息，如果找不到则返回默认值
     */
    private String getI18nMessage(String key, String defaultValue) {
        try {
            return messageSource.getMessage(key, null, defaultValue, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * 将自定义配置应用到默认 GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi customizedApi() {
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(i18nOpenApiCustomizer())
                .addOperationCustomizer(combinedOperationCustomizer())
                .build();
    }
}
