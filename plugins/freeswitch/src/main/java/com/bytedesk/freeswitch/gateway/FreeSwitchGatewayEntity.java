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
package com.bytedesk.freeswitch.gateway;

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
 * FreeSwitch网关实体
 * 对应数据库表：freeswitch_gateways
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({FreeSwitchGatewayEntityListener.class})
@Table(name = "freeswitch_gateways")
public class FreeSwitchGatewayEntity extends BaseEntity {

    /**
     * 网关名称
     */
    @Column(name = "gateway_name", nullable = false, unique = true, length = 100)
    private String gatewayName;

    /**
     * 网关描述
     */
    @Column(length = 255)
    private String description;

    /**
     * SIP服务器地址
     */
    @Column(nullable = false, length = 255)
    private String proxy;

    /**
     * 用户名
     */
    @Column(nullable = false, length = 100)
    private String username;

    /**
     * 密码
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 从号码
     */
    @Column(name = "from_user", length = 100)
    private String fromUser;

    /**
     * 从域名
     */
    @Column(name = "from_domain", length = 100)
    private String fromDomain;

    /**
     * 注册
     */
    @Builder.Default
    @Column(name = "is_register", nullable = false)
    private Boolean register = true;

    /**
     * 注册传输协议
     */
    @Builder.Default
    @Column(name = "register_transport", length = 20)
    private String registerTransport = "udp";

    /**
     * 网关状态
     */
    @Builder.Default
    @Column(length = 20)
    private String status = "DOWN";

    /**
     * 是否启用
     */
    @Builder.Default
    @Column(name = "is_enabled", nullable = false)
    private Boolean enabled = true;

    /**
     * 扩展配置（JSON格式）
     */
    @Column(name = "config_json", columnDefinition = "TEXT")
    private String configJson;

    /**
     * 备注
     */
    @Column(length = 500)
    private String remarks;

    /**
     * 检查网关是否在线
     */
    public boolean isOnline() {
        return "UP".equalsIgnoreCase(status) && enabled;
    }

    /**
     * 获取完整的代理地址
     */
    public String getFullProxy() {
        if (proxy.contains("sip:")) {
            return proxy;
        }
        return "sip:" + proxy;
    }
}
