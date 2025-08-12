/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-12 21:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-12 21:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * JPA Entity Listener 辅助工具类
 * 用于在 Entity Listener 中安全地获取 Spring Bean
 */
@Slf4j
public class EntityListenerHelper {

    /**
     * 安全地获取 Spring Bean
     * 
     * @param beanClass 要获取的Bean类型
     * @param entityId 实体ID，用于日志记录
     * @return Bean实例，如果ApplicationContext未初始化则返回null
     */
    public static <T> T safeGetBean(Class<T> beanClass, String entityId) {
        try {
            if (!ApplicationContextHolder.isInitialized()) {
                log.warn("ApplicationContext not yet initialized, skipping bean access for entity: {}", entityId);
                return null;
            }
            return ApplicationContextHolder.getBean(beanClass);
        } catch (Exception e) {
            log.error("Failed to get bean {} for entity {}: {}", beanClass.getSimpleName(), entityId, e.getMessage());
            return null;
        }
    }

    /**
     * 安全地执行需要Spring Bean的操作
     * 
     * @param beanClass 要获取的Bean类型
     * @param entityId 实体ID，用于日志记录
     * @param operation 要执行的操作
     */
    public static <T> void safeExecuteWithBean(Class<T> beanClass, String entityId, java.util.function.Consumer<T> operation) {
        T bean = safeGetBean(beanClass, entityId);
        if (bean != null) {
            try {
                operation.accept(bean);
            } catch (Exception e) {
                log.error("Failed to execute operation with bean {} for entity {}: {}", 
                         beanClass.getSimpleName(), entityId, e.getMessage(), e);
            }
        }
    }
}
