/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:31:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.number;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.utils.BdDateUtils;

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
@EntityListeners({FreeSwitchNumberEntityListener.class})
@Table(name = "bytedesk_freeswitch_number")
public class FreeSwitchNumberEntity extends BaseEntity {

    /**
     * 用户名（SIP用户名）
     */
    @Column(unique = true)
    private String username;

    /**
     * SIP域名
     */
    private String domain;

    /**
     * 密码
     */
    private String password;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 账户代码
     */
    private String accountcode;

    /**
     * 是否启用
     */
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    /**
     * 最后注册时间
     */
    private ZonedDateTime lastRegister;

    /**
     * 注册IP地址
     */
    private String registerIp;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 备注
     */
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
               lastRegister.isAfter(BdDateUtils.now().minusMinutes(5));
    }
}
