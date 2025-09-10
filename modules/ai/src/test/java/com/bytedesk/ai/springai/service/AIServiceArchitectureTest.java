/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-10 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-10 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试AI服务模块化架构
 */
@Slf4j
@SpringBootTest
@TestPropertySource(properties = {
    "spring.ai.enabled=false", // 禁用Spring AI自动配置以避免测试中的问题
    "logging.level.com.bytedesk.ai=DEBUG"
})
public class AIServiceArchitectureTest {

    @Autowired
    private SpringAIServiceRegistry springAIServiceRegistry;

    @Test
    public void testServiceRegistryIsAvailable() {
        assertNotNull(springAIServiceRegistry, "SpringAIServiceRegistry should be available");
        log.info("SpringAIServiceRegistry is available");
    }

    @Test
    public void testBaseModuleServicesAreRegistered() {
        // 测试基础模块的服务是否被注册
        String[] baseServices = {"zhipuai", "deepseek", "dashscope", "ollama", "custom"};
        
        for (String service : baseServices) {
            try {
                boolean isAvailable = springAIServiceRegistry.isServiceAvailable(service);
                log.info("Base service '{}' availability: {}", service, isAvailable);
                
                if (isAvailable) {
                    assertNotNull(springAIServiceRegistry.getServiceByProviderName(service), 
                        "Service " + service + " should not be null when available");
                }
            } catch (Exception e) {
                log.warn("Service '{}' is not available: {}", service, e.getMessage());
            }
        }
    }

    @Test
    public void testEnterpriseServicesIfAvailable() {
        // 测试企业版服务（如果存在）
        String[] enterpriseServices = {"coze", "dify", "maxkb", "ragflow", "n8n"};
        
        for (String service : enterpriseServices) {
            try {
                boolean isAvailable = springAIServiceRegistry.isServiceAvailable(service);
                log.info("Enterprise service '{}' availability: {}", service, isAvailable);
                
                if (isAvailable) {
                    assertNotNull(springAIServiceRegistry.getServiceByProviderName(service), 
                        "Enterprise service " + service + " should not be null when available");
                    log.info("Enterprise service '{}' is properly registered", service);
                }
            } catch (Exception e) {
                log.info("Enterprise service '{}' is not available (expected if enterprise module not loaded): {}", 
                    service, e.getMessage());
            }
        }
    }

    @Test
    public void testAllRegisteredServices() {
        var allServices = springAIServiceRegistry.getAllServices();
        log.info("Total registered services: {}", allServices.size());
        
        allServices.forEach((name, service) -> {
            log.info("Registered service: {} -> {}", name, service.getClass().getSimpleName());
            assertNotNull(service, "Service " + name + " should not be null");
        });
        
        assertTrue(allServices.size() > 0, "At least one service should be registered");
    }

    @Test
    public void testFallbackToCustomService() {
        // 测试对于不存在的服务，是否会fallback到custom服务
        try {
            var service = springAIServiceRegistry.getServiceByProviderName("non-existent-service");
            assertNotNull(service, "Should fallback to custom service for non-existent providers");
            log.info("Successfully fallback to custom service for non-existent provider");
        } catch (IllegalArgumentException e) {
            log.info("No fallback available, which is expected if custom service is not configured: {}", e.getMessage());
        }
    }
}
