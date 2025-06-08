/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-09 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.freeswitch.model.FreeSwitchUserEntity;
import com.bytedesk.freeswitch.repository.FreeSwitchUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch用户服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class FreeSwitchUserService {

    private final FreeSwitchUserRepository userRepository;

    /**
     * 创建用户
     */
    @Transactional
    public FreeSwitchUserEntity createUser(FreeSwitchUserEntity user) {
        log.info("创建FreeSwitch用户: {}", user.getUsername());
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsernameAndDomain(user.getUsername(), user.getDomain())) {
            throw new RuntimeException("用户名已存在: " + user.getUsername() + "@" + user.getDomain());
        }
        
        return userRepository.save(user);
    }

    /**
     * 更新用户
     */
    @Transactional
    public FreeSwitchUserEntity updateUser(FreeSwitchUserEntity user) {
        log.info("更新FreeSwitch用户: {}", user.getUsername());
        return userRepository.save(user);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("删除FreeSwitch用户: {}", id);
        userRepository.deleteById(id);
    }

    /**
     * 根据ID查找用户
     */
    public Optional<FreeSwitchUserEntity> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 根据用户名查找用户
     */
    public Optional<FreeSwitchUserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 根据用户名和域名查找用户
     */
    public Optional<FreeSwitchUserEntity> findByUsernameAndDomain(String username, String domain) {
        return userRepository.findByUsernameAndDomain(username, domain);
    }

    /**
     * 根据邮箱查找用户
     */
    public Optional<FreeSwitchUserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 获取所有启用的用户
     */
    public List<FreeSwitchUserEntity> findEnabledUsers() {
        return userRepository.findByEnabledTrue();
    }

    /**
     * 根据域名查找用户
     */
    public List<FreeSwitchUserEntity> findByDomain(String domain) {
        return userRepository.findByDomain(domain);
    }

    /**
     * 获取在线用户列表
     */
    public List<FreeSwitchUserEntity> getOnlineUsers() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);
        return userRepository.findOnlineUsers(cutoffTime);
    }

    /**
     * 分页查询用户
     */
    public Page<FreeSwitchUserEntity> findUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * 根据用户名模糊搜索
     */
    public Page<FreeSwitchUserEntity> searchByUsername(String username, Pageable pageable) {
        return userRepository.findByUsernameContainingIgnoreCase(username, pageable);
    }

    /**
     * 启用用户
     */
    @Transactional
    public void enableUser(Long id) {
        Optional<FreeSwitchUserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            FreeSwitchUserEntity user = userOpt.get();
            user.setEnabled(true);
            userRepository.save(user);
            log.info("启用FreeSwitch用户: {}", user.getUsername());
        }
    }

    /**
     * 禁用用户
     */
    @Transactional
    public void disableUser(Long id) {
        Optional<FreeSwitchUserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            FreeSwitchUserEntity user = userOpt.get();
            user.setEnabled(false);
            userRepository.save(user);
            log.info("禁用FreeSwitch用户: {}", user.getUsername());
        }
    }

    /**
     * 更新用户注册信息
     */
    @Transactional
    public void updateUserRegistration(String username, String domain, String registerIp, String userAgent) {
        LocalDateTime registerTime = LocalDateTime.now();
        int updated = userRepository.updateLastRegister(username, domain, registerTime, registerIp);
        
        if (updated > 0) {
            // 同时更新用户代理
            Optional<FreeSwitchUserEntity> userOpt = userRepository.findByUsernameAndDomain(username, domain);
            if (userOpt.isPresent()) {
                FreeSwitchUserEntity user = userOpt.get();
                user.setUserAgent(userAgent);
                userRepository.save(user);
            }
            log.info("更新用户注册信息: {}@{} from {}", username, domain, registerIp);
        }
    }

    /**
     * 验证用户认证
     */
    public boolean authenticateUser(String username, String domain, String password) {
        Optional<FreeSwitchUserEntity> userOpt = userRepository.findByUsernameAndDomain(username, domain);
        
        if (userOpt.isPresent()) {
            FreeSwitchUserEntity user = userOpt.get();
            if (user.getEnabled() && user.getPassword().equals(password)) {
                log.info("用户认证成功: {}@{}", username, domain);
                return true;
            }
        }
        
        log.warn("用户认证失败: {}@{}", username, domain);
        return false;
    }

    /**
     * 更新用户最后注册时间
     */
    @Transactional
    public void updateLastRegistration(String username, LocalDateTime registrationTime) {
        Optional<FreeSwitchUserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            FreeSwitchUserEntity user = optionalUser.get();
            user.setLastRegister(registrationTime);
            userRepository.save(user);
            log.info("更新用户最后注册时间: 用户名={}, 注册时间={}", username, registrationTime);
        } else {
            log.warn("未找到用户名为{}的用户，无法更新注册时间", username);
        }
    }

    /**
     * 获取用户统计信息
     */
    public UserStatistics getUserStatistics() {
        long totalUsers = userRepository.count();
        long enabledUsers = userRepository.countByEnabledTrue();
        long onlineUsers = userRepository.countOnlineUsers(LocalDateTime.now().minusMinutes(5));
        
        return UserStatistics.builder()
                .totalUsers(totalUsers)
                .enabledUsers(enabledUsers)
                .onlineUsers(onlineUsers)
                .build();
    }

    /**
     * 用户统计信息类
     */
    public static class UserStatistics {
        private long totalUsers;
        private long enabledUsers;
        private long onlineUsers;
        
        public static UserStatisticsBuilder builder() {
            return new UserStatisticsBuilder();
        }
        
        public static class UserStatisticsBuilder {
            private long totalUsers;
            private long enabledUsers;
            private long onlineUsers;
            
            public UserStatisticsBuilder totalUsers(long totalUsers) {
                this.totalUsers = totalUsers;
                return this;
            }
            
            public UserStatisticsBuilder enabledUsers(long enabledUsers) {
                this.enabledUsers = enabledUsers;
                return this;
            }
            
            public UserStatisticsBuilder onlineUsers(long onlineUsers) {
                this.onlineUsers = onlineUsers;
                return this;
            }
            
            public UserStatistics build() {
                UserStatistics stats = new UserStatistics();
                stats.totalUsers = this.totalUsers;
                stats.enabledUsers = this.enabledUsers;
                stats.onlineUsers = this.onlineUsers;
                return stats;
            }
        }
        
        // Getters
        public long getTotalUsers() { return totalUsers; }
        public long getEnabledUsers() { return enabledUsers; }
        public long getOnlineUsers() { return onlineUsers; }
    }
}
