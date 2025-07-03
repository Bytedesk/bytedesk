/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-29 12:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 13:15:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Role;
import org.springframework.beans.factory.config.BeanDefinition;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;

/**
 * Jackson ObjectMapper 配置
 * 提供两种不同的 ObjectMapper 实例:
 * 1. 全局通用的 ObjectMapper (不包含类型信息)
 * 2. Redis专用的 ObjectMapper (包含类型信息)
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class JacksonConfig {

    /**
     * 自定义 ZonedDateTime 反序列化器
     * 支持多种日期时间格式，包括没有时区信息的格式
     */
    public static class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {
        
        private static final DateTimeFormatter[] FORMATTERS = {
            // 带时区的格式
            DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"),
            // 不带时区的格式，使用系统默认时区
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        };

        @Override
        public ZonedDateTime deserialize(JsonParser p, DeserializationContext context) throws IOException {
            String value = p.getValueAsString();
            if (value == null || value.trim().isEmpty()) {
                return null;
            }

            // 尝试多种格式解析
            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    if (formatter == DateTimeFormatter.ISO_ZONED_DATE_TIME) {
                        // 对于 ISO 格式，直接解析
                        return ZonedDateTime.parse(value, formatter);
                    } else {
                        // 对于其他格式，先解析为 LocalDateTime，然后转换为 ZonedDateTime
                        LocalDateTime localDateTime = LocalDateTime.parse(value, formatter);
                        return localDateTime.atZone(ZoneId.systemDefault());
                    }
                } catch (DateTimeParseException e) {
                    // 继续尝试下一个格式
                    continue;
                }
            }

            // 如果所有格式都失败，抛出异常
            throw new IOException("Unable to parse ZonedDateTime: " + value);
        }
    }

    /**
     * 创建标准的ObjectMapper实例，用于一般的JSON序列化/反序列化
     * 支持Java 8日期时间类型和循环引用处理，但不包含类型信息
     * 
     * @return 配置好的ObjectMapper实例
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        // 使用JsonMapper.builder()替代直接创建ObjectMapper实例
        ObjectMapper objectMapper = JsonMapper.builder()
            // 设置可见性
            .visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
            // 处理循环引用
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false)
            // 美化输出，方便调试
            .enable(SerializationFeature.INDENT_OUTPUT)
            // 提高容错性
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            // 处理Jackson注解
            .enable(MapperFeature.USE_ANNOTATIONS)
            // 构建ObjectMapper实例
            .build();
        
        // 添加Java 8时间模块 (在build后添加，因为builder中没有直接设置模块的方法)
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 创建自定义模块来处理 ZonedDateTime
        SimpleModule zonedDateTimeModule = new SimpleModule();
        zonedDateTimeModule.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer());
        objectMapper.registerModule(zonedDateTimeModule);
        
        return objectMapper;
    }
    
    /**
     * 创建包含类型信息的ObjectMapper实例，专用于Redis缓存和RedisTemplate
     * 支持Java 8日期时间类型和循环引用处理，并包含类型信息以便正确反序列化
     * 
     * @return 配置好的Redis专用ObjectMapper实例
     */
    @Bean(name = "redisObjectMapper")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ObjectMapper redisObjectMapper() {
        // 使用JsonMapper.builder()替代直接创建ObjectMapper实例
        ObjectMapper objectMapper = JsonMapper.builder()
            // 设置可见性
            .visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
            // 启用默认类型信息，确保在JSON中包含类型信息，用于Redis反序列化
            .activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance, 
                ObjectMapper.DefaultTyping.NON_FINAL, 
                JsonTypeInfo.As.WRAPPER_ARRAY)
            // 处理循环引用
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false)
            // 美化输出，方便调试
            .enable(SerializationFeature.INDENT_OUTPUT)
            // 提高容错性
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            // 处理Jackson注解
            .enable(MapperFeature.USE_ANNOTATIONS)
            // 禁用代理对象的序列化
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            // 构建ObjectMapper实例
            .build();
        
        // 添加Java 8时间模块 (在build后添加，因为builder中没有直接设置模块的方法)
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 创建自定义模块来处理 ZonedDateTime
        SimpleModule zonedDateTimeModule = new SimpleModule();
        zonedDateTimeModule.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer());
        objectMapper.registerModule(zonedDateTimeModule);
        
        // 添加Hibernate代理支持
        objectMapper.registerModule(new Hibernate5JakartaModule()
            .configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, false)
            .configure(Hibernate5JakartaModule.Feature.USE_TRANSIENT_ANNOTATION, false)
            .configure(Hibernate5JakartaModule.Feature.REPLACE_PERSISTENT_COLLECTIONS, true));
        
        return objectMapper;
    }
}
