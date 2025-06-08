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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * FreeSwitch用户响应实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeSwitchUserResponse {

    /**
     * ID
     */
    private Long id;

    /**
     * 用户名（SIP用户名）
     */
    private String username;

    /**
     * SIP域名
     */
    private String domain;

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
    private Boolean enabled;

    /**
     * 最后注册时间
     */
    private LocalDateTime lastRegister;

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
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * SIP地址
     */
    private String sipAddress;

    /**
     * 是否在线
     */
    private Boolean online;

    /**
     * 从实体创建响应对象
     */
    public static FreeSwitchUserResponse fromEntity(FreeSwitchUserEntity entity) {
        return FreeSwitchUserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .domain(entity.getDomain())
                .displayName(entity.getDisplayName())
                .email(entity.getEmail())
                .accountcode(entity.getAccountcode())
                .enabled(entity.getEnabled())
                .lastRegister(entity.getLastRegister())
                .registerIp(entity.getRegisterIp())
                .userAgent(entity.getUserAgent())
                .remarks(entity.getRemarks())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .sipAddress(entity.getSipAddress())
                .online(entity.isOnline())
                .build();
    }

    /**
     * 从实体创建响应对象（隐藏密码）
     */
    public static FreeSwitchUserResponse fromEntitySafe(FreeSwitchUserEntity entity) {
        FreeSwitchUserResponse response = fromEntity(entity);
        // 不返回密码信息
        return response;
    }
}
