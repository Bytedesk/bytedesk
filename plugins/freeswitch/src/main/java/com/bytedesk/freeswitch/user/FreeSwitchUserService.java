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
package com.bytedesk.freeswitch.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * FreeSwitch用户服务类
 * 处理SIP用户管理、注册状态监控等业务逻辑
 */
@Slf4j
@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true")
public class FreeSwitchUserService {

    private final FreeSwitchUserRepository userRepository;

    /**
     * 创建新用户
     */
    @Transactional
    public FreeSwitchUserEntity createUser(String username, String domain, String password,
                                         String displayName, String email, String accountcode) {
        FreeSwitchUserEntity user = FreeSwitchUserEntity.builder()
                .username(username)
                .domain(domain)
                .password(password)
                .displayName(displayName)
                .email(email)
                .accountcode(accountcode)
                .enabled(true)
                .build();
        
        FreeSwitchUserEntity saved = userRepository.save(user);
        log.info("创建用户: {} (ID: {}) -> {}@{}", username, saved.getId(), username, domain);
        return saved;
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
     * 获取指定域名的用户列表
     */
    public List<FreeSwitchUserEntity> findByDomain(String domain) {
        return userRepository.findByDomain(domain);
    }

    /**
     * 获取所有启用的用户
     */
    public List<FreeSwitchUserEntity> findEnabledUsers() {
        return userRepository.findByEnabledTrue();
    }

    /**
     * 获取所有用户（分页）
     */
    public Page<FreeSwitchUserEntity> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * 根据域名查找用户（分页）
     */
    public Page<FreeSwitchUserEntity> findByDomain(String domain, Pageable pageable) {
        return userRepository.findByDomain(domain, pageable);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public FreeSwitchUserEntity updateUser(Long id, String password, String displayName,
                                         String email, String accountcode) {
        FreeSwitchUserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + id));

        if (password != null) user.setPassword(password);
        if (displayName != null) user.setDisplayName(displayName);
        if (email != null) user.setEmail(email);
        if (accountcode != null) user.setAccountcode(accountcode);

        FreeSwitchUserEntity saved = userRepository.save(user);
        log.info("更新用户: {} (ID: {})", user.getUsername(), id);
        return saved;
    }

    /**
     * 启用用户
     */
    @Transactional
    public void enableUser(Long id) {
        FreeSwitchUserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + id));
        
        user.setEnabled(true);
        userRepository.save(user);
        log.info("启用用户: {} (ID: {})", user.getUsername(), id);
    }

    /**
     * 禁用用户
     */
    @Transactional
    public void disableUser(Long id) {
        FreeSwitchUserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + id));
        
        user.setEnabled(false);
        userRepository.save(user);
        log.info("禁用用户: {} (ID: {})", user.getUsername(), id);
    }

    /**
     * 更新用户注册信息
     */
    @Transactional
    public void updateUserRegistration(String username, String domain, String registerIp, String userAgent) {
        Optional<FreeSwitchUserEntity> userOpt = userRepository.findByUsernameAndDomain(username, domain);
        if (userOpt.isPresent()) {
            FreeSwitchUserEntity user = userOpt.get();
            user.setLastRegister(LocalDateTime.now());
            user.setRegisterIp(registerIp);
            user.setUserAgent(userAgent);
            userRepository.save(user);
            log.debug("更新用户注册信息: {}@{} from {}", username, domain, registerIp);
        } else {
            log.warn("用户不存在: {}@{}", username, domain);
        }
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long id) {
        FreeSwitchUserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + id));
        
        userRepository.delete(user);
        log.info("删除用户: {} (ID: {})", user.getUsername(), id);
    }

    /**
     * 获取在线用户列表
     */
    public List<FreeSwitchUserEntity> findOnlineUsers() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);
        return userRepository.findByEnabledTrueAndLastRegisterAfter(cutoffTime);
    }

    /**
     * 根据邮箱查找用户
     */
    public List<FreeSwitchUserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 根据账户代码查找用户
     */
    public List<FreeSwitchUserEntity> findByAccountcode(String accountcode) {
        return userRepository.findByAccountcode(accountcode);
    }

    /**
     * 获取用户总数
     */
    public long countTotal() {
        return userRepository.count();
    }

    /**
     * 获取启用的用户数量
     */
    public long countEnabled() {
        return userRepository.countByEnabledTrue();
    }

    /**
     * 获取在线的用户数量
     */
    public long countOnline() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);
        return userRepository.countByEnabledTrueAndLastRegisterAfter(cutoffTime);
    }

    /**
     * 根据域名统计用户数量
     */
    public long countByDomain(String domain) {
        return userRepository.countByDomain(domain);
    }

    /**
     * 检查用户名和域名组合是否存在
     */
    public boolean existsByUsernameAndDomain(String username, String domain) {
        return userRepository.existsByUsernameAndDomain(username, domain);
    }

    /**
     * 验证用户密码
     */
    public boolean validateUserPassword(String username, String domain, String password) {
        Optional<FreeSwitchUserEntity> userOpt = userRepository.findByUsernameAndDomain(username, domain);
        if (userOpt.isPresent()) {
            FreeSwitchUserEntity user = userOpt.get();
            return user.getEnabled() && user.getPassword().equals(password);
        }
        return false;
    }
}
