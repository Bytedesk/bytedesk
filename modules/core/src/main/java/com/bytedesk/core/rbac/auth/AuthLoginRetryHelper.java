/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-09 14:15:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-09 12:56:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.redis.RedisLoginRetryService;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录重试辅助工具类
 * 封装登录失败处理、重试次数统计、用户锁定等逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthLoginRetryHelper {

    private final RedisLoginRetryService redisLoginRetryService;
    private final BytedeskProperties bytedeskProperties;

    /**
     * 登录重试配置
     */
    public static class LoginRetryConfig {
        public final int maxRetryCount;
        public final int lockTimeMinutes;
        
        public LoginRetryConfig(int maxRetryCount, int lockTimeMinutes) {
            this.maxRetryCount = maxRetryCount;
            this.lockTimeMinutes = lockTimeMinutes;
        }
    }

    /**
     * 获取登录重试配置
     */
    public LoginRetryConfig getLoginRetryConfig() {
        int maxRetryCount = 3; // 默认值
        int lockTimeMinutes = 10; // 默认值
        
        try {
            if (bytedeskProperties != null && bytedeskProperties.getCustom() != null) {
                if (bytedeskProperties.getCustom().getLoginMaxRetryCount() != null) {
                    maxRetryCount = bytedeskProperties.getCustom().getLoginMaxRetryCount();
                }
                if (bytedeskProperties.getCustom().getLoginMaxRetryLockTime() != null) {
                    lockTimeMinutes = bytedeskProperties.getCustom().getLoginMaxRetryLockTime();
                }
            }
        } catch (Exception e) {
            log.warn("Failed to get BytedeskProperties, using default values", e);
        }
        
        return new LoginRetryConfig(maxRetryCount, lockTimeMinutes);
    }

    /**
     * 检查用户是否被锁定
     * @param username 用户名
     * @return 如果用户被锁定，返回错误响应；否则返回null
     */
    public ResponseEntity<?> checkUserLocked(String username) {
        if (redisLoginRetryService.isUserLocked(username)) {
            long remainingTime = redisLoginRetryService.getLockRemainingTime(username);
            if (remainingTime > 0) {
                long minutes = remainingTime / 60;
                long seconds = remainingTime % 60;
                String timeStr = minutes > 0 ? minutes + "分" + seconds + "秒" : seconds + "秒";
                return ResponseEntity.ok().body(JsonResult.error("账户已被锁定，请" + timeStr + "后重试", -1, false));
            } else {
                // 锁定时间已过，解锁用户
                redisLoginRetryService.unlockUser(username);
            }
        }
        return null;
    }

    /**
     * 检查是否已达到最大重试次数
     * @param username 用户名
     * @param config 重试配置
     * @return 如果已达到最大重试次数，返回错误响应；否则返回null
     */
    public ResponseEntity<?> checkMaxRetryReached(String username, LoginRetryConfig config) {
        // 如果最大重试次数为0，表示不限制
        if (config.maxRetryCount > 0) {
            int currentFailedCount = redisLoginRetryService.getLoginFailedCount(username);
            if (currentFailedCount >= config.maxRetryCount) {
                // 达到最大重试次数，锁定用户
                if (config.lockTimeMinutes > 0) {
                    redisLoginRetryService.lockUser(username, config.lockTimeMinutes * 60L);
                    return ResponseEntity.ok().body(JsonResult.error("密码错误次数过多，账户已被锁定" + config.lockTimeMinutes + "分钟", -1, false));
                }
            }
        }
        return null;
    }

    /**
     * 处理登录失败，增加失败次数并返回相应错误信息
     * @param username 用户名
     * @param config 重试配置
     * @param errorMessage 自定义错误信息，可为null
     * @return 错误响应
     */
    public ResponseEntity<?> handleLoginFailure(String username, LoginRetryConfig config, String errorMessage) {
        if (config.maxRetryCount > 0) {
            int newFailedCount = redisLoginRetryService.incrementLoginFailedCount(username, 3600L); // 1小时过期
            int remainingAttempts = config.maxRetryCount - newFailedCount;
            
            if (remainingAttempts > 0) {
                return ResponseEntity.ok().body(JsonResult.error("用户名或密码错误，还可尝试" + remainingAttempts + "次", -1, false));
            } else {
                // 达到最大重试次数，锁定用户
                if (config.lockTimeMinutes > 0) {
                    redisLoginRetryService.lockUser(username, config.lockTimeMinutes * 60L);
                    return ResponseEntity.ok().body(JsonResult.error("密码错误次数过多，账户已被锁定" + config.lockTimeMinutes + "分钟", -1, false));
                } else {
                    return ResponseEntity.ok().body(JsonResult.error("用户名或密码错误", -1, false));
                }
            }
        } else {
            return ResponseEntity.ok().body(JsonResult.error(errorMessage != null ? errorMessage : "用户名或密码错误", -1, false));
        }
    }

    /**
     * 处理登录失败（简化版本，自动获取配置）
     * @param username 用户名
     * @param errorMessage 错误信息
     * @return 错误响应
     */
    public ResponseEntity<?> handleLoginFailure(String username, String errorMessage) {
        LoginRetryConfig config = getLoginRetryConfig();
        return handleLoginFailure(username, config, errorMessage);
    }

    /**
     * 登录成功后重置失败次数
     * @param username 用户名
     */
    public void resetLoginFailedCount(String username) {
        redisLoginRetryService.resetLoginFailedCount(username);
        log.debug("Reset login failed count for user: {}", username);
    }

    /**
     * 执行登录前的所有检查
     * @param username 用户名
     * @return 如果检查失败，返回错误响应；否则返回null
     */
    public ResponseEntity<?> performPreLoginChecks(String username) {
        LoginRetryConfig config = getLoginRetryConfig();
        
        // 检查用户是否被锁定
        ResponseEntity<?> lockedResponse = checkUserLocked(username);
        if (lockedResponse != null) {
            return lockedResponse;
        }
        
        // 检查是否已达到最大重试次数
        return checkMaxRetryReached(username, config);
    }
}
