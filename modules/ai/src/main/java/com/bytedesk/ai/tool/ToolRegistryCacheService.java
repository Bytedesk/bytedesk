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

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.tool.event.ToolRegistryRefreshEvent;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.extern.slf4j.Slf4j;

/**
 * 工具注册表热路径的 Caffeine 本地缓存层。
 *
 * <p>背景：{@link ToolRestService#resolveRuntimeTool} 原先每次都执行
 * {@code toolRepository.findAll().stream()}，在每条对话消息的工具解析阶段都会触发，
 * 开销较大。本服务用 Caffeine 本地缓存（TTL 60s）消除这一热点查询，
 * 通过 {@link ToolRegistryRefreshEvent} 做主动失效，配合 TTL 兜底最终一致性。
 *
 * <p>缓存 key 格式：{@code runtimeToolName + "@" + orgUid}（orgUid 可为 null）。
 *
 * @see ToolRegistryRefreshEvent
 */
@Slf4j
@Service
public class ToolRegistryCacheService {

    /** 运行时工具实体缓存：runtimeToolName@orgUid → ToolEntity（缺省值也缓存以防空查询穿透）。 */
    private final Cache<String, Optional<ToolEntity>> runtimeToolCache;

    /** 运行时工具启用状态缓存：runtimeToolName@orgUid → Boolean。 */
    private final Cache<String, Boolean> runtimeToolEnabledCache;

    /** 缓存 TTL（秒），与文档 §6.5 建议的 30s–60s 区间一致。 */
    private static final long TTL_SECONDS = 60;

    /** 本地缓存最大条目数，避免无限增长。 */
    private static final long MAX_SIZE = 4096;

    public ToolRegistryCacheService() {
        this.runtimeToolCache = Caffeine.newBuilder()
                .expireAfterWrite(TTL_SECONDS, TimeUnit.SECONDS)
                .maximumSize(MAX_SIZE)
                .recordStats()
                .build();
        this.runtimeToolEnabledCache = Caffeine.newBuilder()
                .expireAfterWrite(TTL_SECONDS, TimeUnit.SECONDS)
                .maximumSize(MAX_SIZE)
                .recordStats()
                .build();
    }

    /**
     * 读取缓存的运行时工具实体；缓存未命中返回 null，由调用方回源查询。
     *
     * @param runtimeToolName 运行时工具名（key / name / methodName / beanName 之一）
     * @param orgUid          组织 UID，可为 null（仅匹配平台级工具）
     * @return 缓存命中时返回 {@code Optional}（可能为 empty）；未命中返回 null
     */
    public Optional<ToolEntity> getRuntimeTool(String runtimeToolName, String orgUid) {
        if (runtimeToolName == null || runtimeToolName.isBlank()) {
            return Optional.empty();
        }
        return runtimeToolCache.getIfPresent(buildKey(runtimeToolName, orgUid));
    }

    /**
     * 写入运行时工具实体缓存。
     */
    public void putRuntimeTool(String runtimeToolName, String orgUid, Optional<ToolEntity> toolEntity) {
        if (runtimeToolName == null || runtimeToolName.isBlank()) {
            return;
        }
        runtimeToolCache.put(buildKey(runtimeToolName, orgUid),
                toolEntity == null ? Optional.empty() : toolEntity);
    }

    /**
     * 读取缓存的运行时工具启用状态；缓存未命中返回 null，由调用方回源查询。
     */
    public Boolean getRuntimeToolEnabled(String runtimeToolName, String orgUid) {
        if (runtimeToolName == null || runtimeToolName.isBlank()) {
            return null;
        }
        return runtimeToolEnabledCache.getIfPresent(buildKey(runtimeToolName, orgUid));
    }

    /**
     * 写入运行时工具启用状态缓存。
     */
    public void putRuntimeToolEnabled(String runtimeToolName, String orgUid, boolean enabled) {
        if (runtimeToolName == null || runtimeToolName.isBlank()) {
            return;
        }
        runtimeToolEnabledCache.put(buildKey(runtimeToolName, orgUid), enabled);
    }

    /**
     * 失效全部本地缓存。由 {@link ToolRegistryRefreshEvent} 触发。
     */
    public void invalidateAll() {
        long toolSize = runtimeToolCache.estimatedSize();
        long enabledSize = runtimeToolEnabledCache.estimatedSize();
        runtimeToolCache.invalidateAll();
        runtimeToolEnabledCache.invalidateAll();
        log.info("ToolRegistryCache invalidated all (tools={}, enabled={})", toolSize, enabledSize);
    }

    /**
     * 失效单个工具的缓存条目（orgUid 为 null 时失效所有 orgUid 的同名条目较复杂，
     * 当前直接全量失效，保证一致性）。
     */
    public void invalidate(String runtimeToolName, String orgUid) {
        // 单条失效在 multi-org 场景下可能遗漏同名不同 org 的条目，为安全起见全量失效
        invalidateAll();
    }

    /**
     * 监听注册表刷新事件，清空本地缓存。
     * <p>软删除（{@code deleted=true}）会触发 JPA {@code @PostUpdate} → {@link com.bytedesk.ai.tool.event.ToolUpdateEvent}，
     * 由 ToolRestService 转发为本事件，因此删除场景同样被覆盖。
     */
    @EventListener
    public void onToolRegistryRefresh(ToolRegistryRefreshEvent event) {
        log.debug("ToolRegistryCache refresh triggered by {}", event.getSourceType());
        invalidateAll();
    }

    private String buildKey(String runtimeToolName, String orgUid) {
        return runtimeToolName.trim() + "@" + (orgUid == null ? "" : orgUid);
    }

    /** 仅用于测试 / 监控：返回缓存 TTL。 */
    public Duration getTtl() {
        return Duration.ofSeconds(TTL_SECONDS);
    }
}
