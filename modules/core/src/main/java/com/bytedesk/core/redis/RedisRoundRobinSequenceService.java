/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-10-15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms of the license.
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *   contact: 270580156@qq.com 
 *   联系：270580156@qq.com 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bytedesk.core.constant.RedisConsts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务轮询序号服务：基于 Redis 原子 INCR 提供跨实例单调递增序号。
 * 
 * <p>用于自动分配等需要按作用域公平轮询的场景：
 * <ul>
 *   <li>同一 scopeKey 下每次调用返回全局唯一且严格递增的正整数</li>
 *   <li>Redis 异常时明确抛出 {@link RoundRobinSequenceException}，绝不回退默认值、
 *       本地计数器或“首候选人”，避免故障期间分配失衡</li>
 *   <li>key 不设置 TTL、不按请求重置；失败回滚不扣回已消耗序号（允许空洞）</li>
 * </ul>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RedisRoundRobinSequenceService {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 获取指定作用域的下一个轮询序号（原子 INCR，从 1 开始）。
     *
     * @param scopeKey 作用域标识（业务侧编码 orgUid/ticketType/scopeKind/scopeId 等段）
     * @return 严格递增的正整数序号（>= 1）
     * @throws RoundRobinSequenceException Redis 不可用、返回空值或非正值时抛出，调用方必须失败处理
     */
    public long nextSequence(String scopeKey) {
        if (scopeKey == null || scopeKey.isBlank()) {
            throw new RoundRobinSequenceException("round robin scope key is blank");
        }
        String key = RedisConsts.ROUND_ROBIN_SEQUENCE_PREFIX + scopeKey;
        Long sequence;
        try {
            sequence = redisTemplate.opsForValue().increment(key);
        } catch (RuntimeException ex) {
            // 网络超时时 INCR 可能已执行：不重试、不返回默认值，序号允许空洞
            throw new RoundRobinSequenceException("round robin sequence increment failed for scope: " + scopeKey, ex);
        }
        if (sequence == null) {
            throw new RoundRobinSequenceException("round robin sequence increment returned null for scope: " + scopeKey);
        }
        if (sequence <= 0L) {
            // INCR 正常不会返回非正值；防御计数器被外部改写为非法值的情况
            throw new RoundRobinSequenceException(
                    "round robin sequence corrupted (non-positive value " + sequence + ") for scope: " + scopeKey);
        }
        if (sequence == Long.MAX_VALUE) {
            throw new RoundRobinSequenceException("round robin sequence overflow for scope: " + scopeKey);
        }
        if (log.isDebugEnabled()) {
            log.debug("round robin sequence {} -> {}", scopeKey, sequence);
        }
        return sequence;
    }

    /** 轮询取号失败（Redis 异常/返回非法值）时抛出，调用方必须显式失败处理 */
    public static class RoundRobinSequenceException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public RoundRobinSequenceException(String message) {
            super(message);
        }

        public RoundRobinSequenceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
