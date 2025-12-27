/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-07 16:27:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-05 14:54:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PreDestroy;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.authority.AuthorityEntity;
import com.bytedesk.core.rbac.authority.event.AuthorityCreateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleEventListener {

    private final RoleRestService roleRestService;
    
    // 存储收集到的权限
    private final ConcurrentHashMap<String, Set<String>> roleAuthorityMap = new ConcurrentHashMap<>();

    // 启动阶段延迟绑定：避免 role 未创建就绑定导致失败
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "role-authority-binder");
        t.setDaemon(true);
        return t;
    });
    private final AtomicInteger startupAttempts = new AtomicInteger(0);
    private final AtomicBoolean flushing = new AtomicBoolean(false);
    private volatile boolean appReady = false;
    private volatile ScheduledFuture<?> startupFuture;

    @EventListener
    public void onAuthorityCreateEvent(AuthorityCreateEvent event) {
        AuthorityEntity authorityEntity = event.getAuthority();
        // log.info("role AuthorityCreateEvent: {}", authorityEntity.getUid());
        
        String authorityUid = authorityEntity.getUid();
        if (!StringUtils.hasText(authorityUid)) {
            return;
        }

        String authorityValue = authorityEntity.getValue();

        // SUPER: 永远拥有全部权限
        roleAuthorityMap.computeIfAbsent(BytedeskConsts.DEFAULT_ROLE_SUPER_UID, k -> ConcurrentHashMap.newKeySet()).add(authorityUid);

        // ADMIN: 拥有除 Settings 写入/更新（CREATE/UPDATE）之外的所有权限
        if (!RoleAuthorityRules.isAdminExcludedPermission(authorityValue)) {
            roleAuthorityMap.computeIfAbsent(BytedeskConsts.DEFAULT_ROLE_ADMIN_UID, k -> ConcurrentHashMap.newKeySet()).add(authorityUid);
        }

        // AGENT: 知识库（kbase）模块所有 READ 权限
        if (RoleAuthorityRules.isKbaseReadPermission(authorityValue)) {
            roleAuthorityMap.computeIfAbsent(BytedeskConsts.DEFAULT_ROLE_AGENT_UID, k -> ConcurrentHashMap.newKeySet()).add(authorityUid);
        }

        // 启动完成后若角色已就绪，则直接尝试 flush（避免错过 ApplicationReady 之后才创建的权限）
        if (appReady) {
            tryFlushOnce();
        }
    }

    @EventListener
    public void onApplicationReadyEvent(ApplicationReadyEvent event) {
        appReady = true;

        // 仅在启动阶段短暂重试：避免长期定时任务。
        // 2s 一次，最多 30 次（约 1 分钟），角色就绪后立即 flush 并停止。
        if (startupFuture == null) {
            startupFuture = scheduler.scheduleWithFixedDelay(this::tryFlushSafely, 0, 2, TimeUnit.SECONDS);
        }
    }

    /**
     * 权限创建主要发生在应用启动阶段（自动初始化，不走接口创建）。
     * 这里在 ApplicationReady 统一处理一次即可：
     * - 既能保证角色已创建
     * - 也避免 Quartz 每分钟轮询带来的持续 DB 操作与噪音日志
     */
    private void flushCollectedAuthorities() {
        roleAuthorityMap.forEach((roleUid, authorityUids) -> {
            if (authorityUids == null || authorityUids.isEmpty()) {
                return;
            }
            try {
                RoleRequest roleRequest = RoleRequest.builder()
                        .uid(roleUid)
                        .authorityUids(authorityUids)
                        .build();
                roleRestService.addAuthoritiesSystem(roleRequest);
            } catch (Exception e) {
                // 启动阶段容错：不阻断应用启动
                log.warn("Failed to bind collected authorities to role uid={} (size={}): {}",
                        roleUid, authorityUids.size(), e.getMessage());
            } finally {
                authorityUids.clear();
            }
        });
        roleAuthorityMap.clear();
    }

    private void tryFlushSafely() {
        try {
            tryFlushOnce();
        } catch (Exception e) {
            log.warn("Role authority startup flush attempt failed: {}", e.getMessage());
        }
    }

    private void tryFlushOnce() {
        if (!appReady) {
            return;
        }
        if (!flushing.compareAndSet(false, true)) {
            return;
        }
        try {
            int attempt = startupAttempts.incrementAndGet();

            boolean rolesReady = areDefaultRolesReady();
            boolean hasPending = roleAuthorityMap.values().stream().anyMatch(set -> set != null && !set.isEmpty());

            if (rolesReady && hasPending) {
                flushCollectedAuthorities();
                stopStartupRetry();
                return;
            }

            // 角色已就绪但没有待处理数据：再等几轮，避免极端情况下 ready 之后才触发 AuthorityCreateEvent
            if (rolesReady && !hasPending && attempt >= 3) {
                stopStartupRetry();
                return;
            }

            if (attempt >= 30) {
                if (hasPending) {
                    log.warn("Role authority binding not completed after {} attempts; pending data remains.", attempt);
                }
                stopStartupRetry();
            }
        } finally {
            flushing.set(false);
        }
    }

    private boolean areDefaultRolesReady() {
        try {
            return Boolean.TRUE.equals(roleRestService.existsByUid(BytedeskConsts.DEFAULT_ROLE_SUPER_UID))
                    && Boolean.TRUE.equals(roleRestService.existsByUid(BytedeskConsts.DEFAULT_ROLE_ADMIN_UID))
                    && Boolean.TRUE.equals(roleRestService.existsByUid(BytedeskConsts.DEFAULT_ROLE_AGENT_UID))
                    && Boolean.TRUE.equals(roleRestService.existsByUid(BytedeskConsts.DEFAULT_ROLE_USER_UID));
        } catch (Exception e) {
            // 启动期容错：查库异常时认为未就绪，等待下一轮
            log.warn("Failed to check default role readiness: {}", e.getMessage());
            return false;
        }
    }

    private void stopStartupRetry() {
        ScheduledFuture<?> future = this.startupFuture;
        if (future != null) {
            future.cancel(false);
            this.startupFuture = null;
        }
    }

    @PreDestroy
    public void shutdownScheduler() {
        stopStartupRetry();
        scheduler.shutdownNow();
    }

}
