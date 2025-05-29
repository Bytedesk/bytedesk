/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-29 12:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-29 12:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 公共的 Jackson ObjectMapper 配置
 * 用于Redis缓存和RedisTemplate的序列化
 */
@Configuration
public class ObjectMapperConfig {

    /**
     * 创建配置好的ObjectMapper实例
     * 支持Java 8日期时间类型和循环引用处理
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
            // 禁用默认类型信息，避免在JSON中包含类型信息
            // .activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL)
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
        
        return objectMapper;
    }
}
