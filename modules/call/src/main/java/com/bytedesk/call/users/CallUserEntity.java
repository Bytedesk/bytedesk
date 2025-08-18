/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-12 17:33:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.users;

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
 * Call用户实体
 * 对应数据库表：freeswitch_users
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({CallUserEntityListener.class})
@Table(name = "bytedesk_call_user")
public class CallUserEntity extends BaseEntity {

    /**
     * 用户名（SIP用户名）
     */
    @Column(unique = true)
    private String username;

    /**
     * SIP域名
     * 对应数据库字段：domain
     */
    @Column(nullable = false, length = 100)
    @Builder.Default
    private String domain = "127.0.0.1";

    /**
     * 密码
     * 对应数据库字段：password
     */
    @Column(nullable = false, length = 100)
    private String password;

    /**
     * 语音邮件密码
     * 对应数据库字段：vm_password
     */
    @Column(name = "vm_password", length = 50)
    private String vmPassword;

    /**
     * 是否启用
     * 对应数据库字段：enabled
     */
    @Builder.Default
    
    private Boolean enabled = true;

    /**
     * 呼叫显示名称
     * 对应数据库字段：caller_id_name
     */
    @Column(name = "caller_id_name", length = 100)
    private String callerIdName;

    /**
     * 呼叫显示号码
     * 对应数据库字段：caller_id_number
     */
    @Column(name = "caller_id_number", length = 50)
    private String callerIdNumber;

    /**
     * 外呼显示名称
     * 对应数据库字段：outbound_caller_id_name
     */
    @Column(name = "outbound_caller_id_name", length = 100)
    private String outboundCallerIdName;

    /**
     * 外呼显示号码
     * 对应数据库字段：outbound_caller_id_number
     */
    @Column(name = "outbound_caller_id_number", length = 50)
    private String outboundCallerIdNumber;

    /**
     * 通话权限
     * 对应数据库字段：toll_allow
     */
    @Column(name = "toll_allow", length = 200)
    @Builder.Default
    private String tollAllow = "domestic,international,local";

    /**
     * 账户代码
     * 对应数据库字段：accountcode
     */
    @Column(length = 50)
    private String accountcode;

    /**
     * 用户上下文
     * 对应数据库字段：user_context
     */
    @Column(name = "user_context", length = 50)
    @Builder.Default
    private String userContext = "default";

    /**
     * 呼叫组
     * 对应数据库字段：callgroup
     */
    @Column(length = 50)
    @Builder.Default
    private String callgroup = "bytedesk";

    // 以下是原有字段，保留但调整名称以匹配数据库结构

    /**
     * 显示名称（兼容字段，对应caller_id_name）
     */
    @Column(name = "display_name", length = 100)
    private String displayName;

    /**
     * 邮箱
     */
    @Column(length = 100)
    private String email;

    /**
     * 最后注册时间
     */
    @Column(name = "last_register")
    private ZonedDateTime lastRegister;

    /**
     * 注册IP地址
     */
    @Column(name = "register_ip", length = 45)
    private String registerIp;

    /**
     * 用户代理
     */
    @Column(name = "user_agent", length = 500)
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
               lastRegister.isAfter(BdDateUtils.now().minusMinutes(5));
    }

    /**
     * 获取呼叫显示名称，优先使用caller_id_name，如果没有则使用displayName
     */
    public String getEffectiveCallerIdName() {
        return callerIdName != null ? callerIdName : displayName;
    }

    /**
     * 获取呼叫显示号码，优先使用caller_id_number，如果没有则使用username
     */
    public String getEffectiveCallerIdNumber() {
        return callerIdNumber != null ? callerIdNumber : username;
    }

    /**
     * 检查是否有外呼权限
     */
    public boolean hasOutboundPermission() {
        return enabled && tollAllow != null && 
               (tollAllow.contains("domestic") || tollAllow.contains("international") || tollAllow.contains("local"));
    }
}
