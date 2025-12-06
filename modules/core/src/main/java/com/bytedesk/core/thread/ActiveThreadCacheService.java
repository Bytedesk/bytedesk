/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.RedisConsts;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 活跃会话缓存服务
 * 使用 Redis Hash 存储活跃服务会话的关键信息，避免频繁查询数据库
 * 
 * 缓存策略：
 * 1. 会话创建（状态为 CHATTING/ROBOTING/QUEUING）时加入缓存
 * 2. 会话关闭（状态为 CLOSED）时从缓存移除
 * 3. 会话状态变更时更新缓存
 * 4. 消息发送时更新 updatedAtMillis
 */
@Slf4j
@Service
@AllArgsConstructor
public class ActiveThreadCacheService {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 添加或更新活跃会话到缓存
     */
    public void addOrUpdateActiveThread(ThreadEntity thread) {
        if (thread == null || !StringUtils.hasText(thread.getUid())) {
            return;
        }

        // 只缓存服务类型的会话
        if (!isServiceThread(thread)) {
            return;
        }

        // 已关闭的会话不加入缓存
        if (thread.isClosed()) {
            removeActiveThread(thread.getUid());
            return;
        }

        try {
            ActiveThreadCache cache = ActiveThreadCache.fromThread(thread);
            stringRedisTemplate.opsForHash().put(
                    RedisConsts.ACTIVE_SERVICE_THREADS_KEY,
                    thread.getUid(),
                    cache.toJson());
            log.debug("Added/Updated active thread cache: {}", thread.getUid());
        } catch (Exception e) {
            log.warn("Failed to add active thread to cache: {}, error: {}", thread.getUid(), e.getMessage());
        }
    }

    /**
     * 从缓存移除活跃会话
     */
    public void removeActiveThread(String threadUid) {
        if (!StringUtils.hasText(threadUid)) {
            return;
        }

        try {
            stringRedisTemplate.opsForHash().delete(RedisConsts.ACTIVE_SERVICE_THREADS_KEY, threadUid);
            log.debug("Removed active thread from cache: {}", threadUid);
        } catch (Exception e) {
            log.warn("Failed to remove active thread from cache: {}, error: {}", threadUid, e.getMessage());
        }
    }

    /**
     * 获取单个活跃会话缓存
     */
    public Optional<ActiveThreadCache> getActiveThread(String threadUid) {
        if (!StringUtils.hasText(threadUid)) {
            return Optional.empty();
        }

        try {
            Object value = stringRedisTemplate.opsForHash().get(RedisConsts.ACTIVE_SERVICE_THREADS_KEY, threadUid);
            if (value != null) {
                return Optional.of(ActiveThreadCache.fromJson(value.toString()));
            }
        } catch (Exception e) {
            log.warn("Failed to get active thread from cache: {}, error: {}", threadUid, e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * 获取所有活跃服务会话缓存
     * 用于替代数据库查询 findServiceThreadStateStarted
     */
    public List<ActiveThreadCache> getAllActiveServiceThreads() {
        List<ActiveThreadCache> result = new ArrayList<>();

        try {
            Map<Object, Object> entries = stringRedisTemplate.opsForHash()
                    .entries(RedisConsts.ACTIVE_SERVICE_THREADS_KEY);

            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                try {
                    ActiveThreadCache cache = ActiveThreadCache.fromJson(entry.getValue().toString());
                    // 过滤掉已关闭的会话（理论上不应该存在，但做防御性检查）
                    if (!ThreadProcessStatusEnum.CLOSED.name().equals(cache.getStatus())) {
                        result.add(cache);
                    } else {
                        // 清理异常数据
                        removeActiveThread(cache.getUid());
                    }
                } catch (Exception e) {
                    log.warn("Failed to parse active thread cache entry: {}", entry.getKey());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to get all active service threads from cache: {}", e.getMessage());
        }

        return result;
    }

    /**
     * 更新会话的最后更新时间
     * 用于消息发送时更新
     */
    public void updateThreadTimestamp(String threadUid) {
        getActiveThread(threadUid).ifPresent(cache -> {
            cache.updateTimestamp();
            try {
                stringRedisTemplate.opsForHash().put(
                        RedisConsts.ACTIVE_SERVICE_THREADS_KEY,
                        threadUid,
                        cache.toJson());
            } catch (Exception e) {
                log.warn("Failed to update thread timestamp in cache: {}", threadUid);
            }
        });
    }

    /**
     * 更新会话状态
     */
    public void updateThreadStatus(String threadUid, String newStatus) {
        if (ThreadProcessStatusEnum.CLOSED.name().equals(newStatus)) {
            removeActiveThread(threadUid);
            return;
        }

        getActiveThread(threadUid).ifPresent(cache -> {
            cache.updateStatus(newStatus);
            try {
                stringRedisTemplate.opsForHash().put(
                        RedisConsts.ACTIVE_SERVICE_THREADS_KEY,
                        threadUid,
                        cache.toJson());
            } catch (Exception e) {
                log.warn("Failed to update thread status in cache: {}", threadUid);
            }
        });
    }

    /**
     * 获取活跃会话数量
     */
    public long getActiveThreadCount() {
        try {
            Long size = stringRedisTemplate.opsForHash().size(RedisConsts.ACTIVE_SERVICE_THREADS_KEY);
            return size != null ? size : 0L;
        } catch (Exception e) {
            log.warn("Failed to get active thread count: {}", e.getMessage());
            return 0L;
        }
    }

    /**
     * 判断是否为服务类型会话
     */
    private boolean isServiceThread(ThreadEntity thread) {
        String type = thread.getType();
        return ThreadTypeEnum.AGENT.name().equals(type)
                || ThreadTypeEnum.WORKGROUP.name().equals(type)
                || ThreadTypeEnum.ROBOT.name().equals(type);
    }

    /**
     * 判断是否为服务类型（基于缓存数据）
     */
    public boolean isServiceType(String type) {
        return ThreadTypeEnum.AGENT.name().equals(type)
                || ThreadTypeEnum.WORKGROUP.name().equals(type)
                || ThreadTypeEnum.ROBOT.name().equals(type);
    }

    /**
     * 清空所有活跃会话缓存
     * 慎用！仅用于系统维护
     */
    public void clearAllActiveThreads() {
        try {
            stringRedisTemplate.delete(RedisConsts.ACTIVE_SERVICE_THREADS_KEY);
            log.info("Cleared all active thread cache");
        } catch (Exception e) {
            log.warn("Failed to clear all active threads: {}", e.getMessage());
        }
    }

    /**
     * 从数据库重建缓存
     * 用于系统启动或缓存失效时的恢复
     */
    public void rebuildCacheFromDatabase(List<ThreadEntity> threads) {
        if (threads == null || threads.isEmpty()) {
            return;
        }

        log.info("Rebuilding active thread cache from database, thread count: {}", threads.size());
        
        for (ThreadEntity thread : threads) {
            addOrUpdateActiveThread(thread);
        }
        
        log.info("Active thread cache rebuilt, cached count: {}", getActiveThreadCount());
    }
}
