/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-12 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-08-12 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.tool;

import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.tool.event.ToolCreateEvent;
import com.bytedesk.ai.tool.event.ToolRegistryRefreshEvent;
import com.bytedesk.ai.tool.event.ToolUpdateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 统一处理 ToolEntity 变更后的缓存失效。
 *
 * <p>JPA 实体监听器 {@link ToolEntityListener} 在 {@code @PostPersist} / {@code @PostUpdate}
 * 时发布 {@link ToolCreateEvent} / {@link ToolUpdateEvent}（软删除设置 {@code deleted=true}
 * 同样触发 {@code @PostUpdate}）。本监听器将这些事件转换为：
 *
 * <ol>
 *   <li>{@link ToolRegistryRefreshEvent} → 通知 {@link ToolRegistryCacheService} 清空 Caffeine 本地缓存；</li>
 *   <li>清除 Redis 二级缓存 "tool" 的全部条目（findByUid / findByName / findByKey 等）。</li>
 * </ol>
 *
 * <p>这样所有写入路径（create / update / deleteByUid / syncSystemTool / disableStalePlatformSystemTools）
 * 都无需各自散落 {@code @CacheEvict} 注解，避免遗漏。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolCacheInvalidationListener {

    private static final String TOOL_CACHE_NAME = "tool";

    private final ApplicationEventPublisher applicationEventPublisher;

    private final CacheManager cacheManager;

    @EventListener
    public void onToolCreate(ToolCreateEvent event) {
        invalidateCaches("create", event.getTool() == null ? null : event.getTool().getUid());
    }

    @EventListener
    public void onToolUpdate(ToolUpdateEvent event) {
        invalidateCaches("update", event.getTool() == null ? null : event.getTool().getUid());
    }

    /**
     * 统一失效 Caffeine 本地缓存 + Redis 二级缓存。
     *
     * @param action 触发动作（create / update / delete），仅用于日志
     * @param toolUid 变更的工具 UID，仅用于日志
     */
    private void invalidateCaches(String action, String toolUid) {
        // 1. 失效 Caffeine 本地缓存（ToolRegistryCacheService 内部监听本事件）
        applicationEventPublisher.publishEvent(new ToolRegistryRefreshEvent(this));

        // 2. 失效 Redis 二级缓存 "tool"
        try {
            org.springframework.cache.Cache toolCache = cacheManager.getCache(TOOL_CACHE_NAME);
            if (toolCache != null) {
                toolCache.clear();
            }
        } catch (Exception e) {
            log.warn("Failed to clear Redis cache '{}' on tool {}: {}", TOOL_CACHE_NAME, action, e.getMessage());
        }

        log.debug("Tool cache invalidated: action={}, toolUid={}", action, toolUid);
    }
}
