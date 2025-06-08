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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bytedesk.freeswitch.user.FreeSwitchUserEntity;

/**
 * FreeSwitch用户仓库接口
 */
@Repository
public interface FreeSwitchUserRepository extends JpaRepository<FreeSwitchUserEntity, Long>, 
        JpaSpecificationExecutor<FreeSwitchUserEntity> {

    /**
     * 根据UID查找用户
     */
    Optional<FreeSwitchUserEntity> findByUid(String uid);

    /**
     * 根据用户名查找用户
     */
    Optional<FreeSwitchUserEntity> findByUsername(String username);

    /**
     * 根据用户名和域名查找用户
     */
    Optional<FreeSwitchUserEntity> findByUsernameAndDomain(String username, String domain);

    /**
     * 根据邮箱查找用户
     */
    List<FreeSwitchUserEntity> findByEmail(String email);

    /**
     * 查找启用的用户
     */
    List<FreeSwitchUserEntity> findByEnabledTrue();

    /**
     * 根据域名查找用户
     */
    List<FreeSwitchUserEntity> findByDomain(String domain);

    /**
     * 根据域名查找用户（分页）
     */
    Page<FreeSwitchUserEntity> findByDomain(String domain, Pageable pageable);

    /**
     * 根据账户代码查找用户
     */
    List<FreeSwitchUserEntity> findByAccountcode(String accountcode);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查用户名和域名组合是否存在
     */
    boolean existsByUsernameAndDomain(String username, String domain);

    /**
     * 查找在线用户（最近指定时间内有注册记录）
     */
    List<FreeSwitchUserEntity> findByEnabledTrueAndLastRegisterAfter(LocalDateTime cutoffTime);

    /**
     * 统计域名下的用户数量
     */
    long countByDomain(String domain);

    /**
     * 统计在线用户数量
     */
    long countByEnabledTrueAndLastRegisterAfter(LocalDateTime cutoffTime);

    /**
     * 查找在线用户（最近5分钟内有注册记录）
     */
    @Query("SELECT u FROM FreeSwitchUserEntity u WHERE u.enabled = true AND u.lastRegister > :cutoffTime")
    List<FreeSwitchUserEntity> findOnlineUsers(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * 根据用户名模糊搜索
     */
    Page<FreeSwitchUserEntity> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    /**
     * 根据显示名称模糊搜索
     */
    Page<FreeSwitchUserEntity> findByDisplayNameContainingIgnoreCase(String displayName, Pageable pageable);

    /**
     * 统计启用的用户数量
     */
    long countByEnabledTrue();

    /**
     * 统计在线用户数量
     */
    @Query("SELECT COUNT(u) FROM FreeSwitchUserEntity u WHERE u.enabled = true AND u.lastRegister > :cutoffTime")
    long countOnlineUsers(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * 更新用户最后注册时间和IP
     */
    @Query("UPDATE FreeSwitchUserEntity u SET u.lastRegister = :registerTime, u.registerIp = :registerIp WHERE u.username = :username AND u.domain = :domain")
    int updateLastRegister(@Param("username") String username, 
                          @Param("domain") String domain,
                          @Param("registerTime") LocalDateTime registerTime, 
                          @Param("registerIp") String registerIp);
}
