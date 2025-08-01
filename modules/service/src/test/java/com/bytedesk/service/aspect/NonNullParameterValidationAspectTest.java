/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-20 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.aspect;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @NonNull 参数验证切面测试
 */
@Slf4j
@SpringBootTest
@TestPropertySource(properties = {
    "bytedesk.validation.nonnull.enabled=true",
    "bytedesk.validation.nonnull.throw-exception=true",
    "bytedesk.validation.nonnull.log-level=WARN"
})
class NonNullParameterValidationAspectTest {

    @Autowired
    private TestService testService;

    @Test
    void testValidParameters() {
        // 测试有效参数
        assertDoesNotThrow(() -> {
            testService.testMethod("valid", "valid2");
        });
    }

    @Test
    void testNullParameter() {
        // 测试 null 参数
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testService.testMethod(null, "valid");
        });
        
        assertTrue(exception.getMessage().contains("null/empty parameters"));
        assertTrue(exception.getMessage().contains("param1"));
    }

    @Test
    void testEmptyStringParameter() {
        // 测试空字符串参数
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testService.testMethod("", "valid");
        });
        
        assertTrue(exception.getMessage().contains("null/empty parameters"));
        assertTrue(exception.getMessage().contains("param1"));
    }

    @Test
    void testWhitespaceStringParameter() {
        // 测试空白字符串参数
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testService.testMethod("   ", "valid");
        });
        
        assertTrue(exception.getMessage().contains("null/empty parameters"));
        assertTrue(exception.getMessage().contains("param1"));
    }

    @Test
    void testMultipleNullParameters() {
        // 测试多个 null 参数
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testService.testMethod(null, null);
        });
        
        assertTrue(exception.getMessage().contains("null/empty parameters"));
        assertTrue(exception.getMessage().contains("param1"));
        assertTrue(exception.getMessage().contains("param2"));
    }

    @Test
    void testObjectParameter() {
        // 测试对象参数
        assertDoesNotThrow(() -> {
            testService.testObjectMethod("valid", new Object());
        });
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testService.testObjectMethod("valid", null);
        });
        
        assertTrue(exception.getMessage().contains("null/empty parameters"));
        assertTrue(exception.getMessage().contains("obj"));
    }

    @Test
    void testMethodWithoutNonNullAnnotation() {
        // 测试没有 @NonNull 注解的方法
        assertDoesNotThrow(() -> {
            testService.testMethodWithoutAnnotation(null, null);
        });
    }

    /**
     * 测试服务类
     */
    @Service
    public static class TestService {
        
        public String testMethod(@NonNull String param1, @NonNull String param2) {
            return param1 + param2;
        }
        
        public String testObjectMethod(@NonNull String param, @NonNull Object obj) {
            return param + obj.toString();
        }
        
        public String testMethodWithoutAnnotation(String param1, String param2) {
            return (param1 != null ? param1 : "") + (param2 != null ? param2 : "");
        }
    }
} 