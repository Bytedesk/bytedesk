/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-22 17:41:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-12 16:53:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bytedesk.core.constant.RedisConsts;

import lombok.extern.slf4j.Slf4j;

/**
 * 登录重试次数限制和账户锁定的Redis服务
 * 专门处理用户登录失败重试、账户锁定等安全相关功能
 */
@Slf4j
@Service
public class RedisLoginRetryService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    /**
     * 获取用户登录失败次数
     * @param username 用户名
     * @return 失败次数，如果不存在返回0
     */
    public int getLoginFailedCount(String username) {
        if (username == null || username.trim().isEmpty()) {
            return 0;
        }
        String key = RedisConsts.LOGIN_FAILED_PREFIX + username;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Integer.parseInt(value) : 0;
    }
    
    /**
     * 增加用户登录失败次数
     * @param username 用户名
     * @param ttl 过期时间（秒）
     * @return 增加后的失败次数
     */
    public int incrementLoginFailedCount(String username, long ttl) {
        if (username == null || username.trim().isEmpty()) {
            return 0;
        }
        String key = RedisConsts.LOGIN_FAILED_PREFIX + username;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null &amp;amp;& count == 1) {
            // 第一次失败，设置过期时间
            redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
        }
        return count != null ? count.intValue() : 0;
    }
    
    /**
     * 重置用户登录失败次数
     * @param username 用户名
     */
    public void resetLoginFailedCount(String username) {
        if (username == null || username.trim().isEmpty()) {
            return;
        }
        String key = RedisConsts.LOGIN_FAILED_PREFIX + username;
        Boolean result = redisTemplate.delete(key);
        if (Boolean.TRUE.equals(result)) {
            log.debug("Reset login failed count for user: {}", username);
        }
    }
    
    /**
     * 检查用户是否被锁定
     * @param username 用户名
     * @return true if locked, false otherwise
     */
    public boolean isUserLocked(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String key = RedisConsts.LOGIN_LOCKED_PREFIX + username;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    /**
     * 锁定用户登录
     * @param username 用户名
     * @param ttl 锁定时间（秒）
     */
    public void lockUser(String username, long ttl) {
        if (username == null || username.trim().isEmpty()) {
            return;
        }
        String key = RedisConsts.LOGIN_LOCKED_PREFIX + username;
        redisTemplate.opsForValue().set(key, RedisConsts.LOGIN_LOCKED_VALUE);
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
        log.info("User {} locked for {} seconds due to excessive login failures", username, ttl);
    }
    
    /**
     * 解锁用户登录
     * @param username 用户名
     */
    public void unlockUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            return;
        }
        String key = RedisConsts.LOGIN_LOCKED_PREFIX + username;
        Boolean result = redisTemplate.delete(key);
        if (Boolean.TRUE.equals(result)) {
            log.info("User {} unlocked", username);
        }
    }
    
    /**
     * 获取用户锁定剩余时间
     * @param username 用户名
     * @return 剩余锁定时间（秒），如果未锁定返回-1
     */
    public long getLockRemainingTime(String username) {
        if (username == null || username.trim().isEmpty()) {
            return -1;
        }
        String key = RedisConsts.LOGIN_LOCKED_PREFIX + username;
        Long ttl = redisTemplate.getExpire(key);
        return ttl != null ? ttl : -1;
    }
    
    /**
     * 批量清理过期的登录失败记录
     * 用于定期清理，避免Redis内存占用过多
     */
    public void cleanupExpiredRecords() {
        // 这里可以实现批量清理逻辑
        // 由于Redis会自动过期，通常不需要手动清理
        log.debug("Cleanup expired login retry records completed");
    }
    
    /**
     * 获取用户登录状态统计信息
     * @param username 用户名
     * @return 包含失败次数和锁定状态的统计信息
     */
    public LoginRetryStats getLoginRetryStats(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new LoginRetryStats();
        }
        
        int failedCount = getLoginFailedCount(username);
        boolean isLocked = isUserLocked(username);
        long lockRemainingTime = getLockRemainingTime(username);
        
        return new LoginRetryStats(failedCount, isLocked, lockRemainingTime);
    }
    
    /**
     * 登录重试统计信息
     */
    public static class LoginRetryStats {
        private final int failedCount;
        private final boolean isLocked;
        private final long lockRemainingTime;
        
        public LoginRetryStats() {
            this(0, false, -1);
        }
        
        public LoginRetryStats(int failedCount, boolean isLocked, long lockRemainingTime) {
            this.failedCount = failedCount;
            this.isLocked = isLocked;
            this.lockRemainingTime = lockRemainingTime;
        }
        
        public int getFailedCount() {
            return failedCount;
        }
        
        public boolean isLocked() {
            return isLocked;
        }
        
        public long getLockRemainingTime() {
            return lockRemainingTime;
        }
        
        @Override
        public String toString() {
            return "LoginRetryStats{" +
                    "failedCount=" + failedCount +
                    ", isLocked=" + isLocked +
                    ", lockRemainingTime=" + lockRemainingTime +
                    '}';
        }
    }
}
