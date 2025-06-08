/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 19:50:07
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

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * FreeSwitch用户实体
 * 对应数据库表：freeswitch_users
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({FreeSwitchUserEntityListener.class})
@Table(name = "bytedesk_freeswitch_user")
public class FreeSwitchUserEntity extends BaseEntity {

    /**
     * 用户名（SIP用户名）
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * SIP域名
     */
    @Column(nullable = false, length = 100)
    private String domain;

    /**
     * 密码
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 显示名称
     */
    @Column(length = 100)
    private String displayName;

    /**
     * 邮箱
     */
    @Column(length = 100)
    private String email;

    /**
     * 账户代码
     */
    @Column(length = 50)
    private String accountcode;

    /**
     * 是否启用
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 最后注册时间
     */
    @Column
    private LocalDateTime lastRegister;

    /**
     * 注册IP地址
     */
    @Column(length = 45)
    private String registerIp;

    /**
     * 用户代理
     */
    @Column(length = 255)
    private String userAgent;

    /**
     * 备注
     */
    @Column(length = 500)
    private String remarks;

    /**
     * 获取完整的SIP地址
     */
    public String getSipAddress() {
        return username + "@" + domain;
    }

    /**
     * 检查用户是否在线
     */
    public boolean isOnline() {
        return enabled && lastRegister != null && 
               lastRegister.isAfter(LocalDateTime.now().minusMinutes(5));
    }
}
