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

import com.bytedesk.core.base.BaseRequest;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * FreeSwitch网关请求实体
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FreeSwitchGatewayRequest extends BaseRequest {

    /**
     * 网关名称
     */
    @Size(max = 100, message = "网关名称长度不能超过100字符")
    private String gatewayName;

    /**
     * 网关描述
     */
    @Size(max = 255, message = "网关描述长度不能超过255字符")
    private String description;

    /**
     * SIP服务器地址
     */
    @Size(max = 255, message = "SIP服务器地址长度不能超过255字符")
    private String proxy;

    /**
     * 用户名
     */
    @Size(max = 100, message = "用户名长度不能超过100字符")
    private String username;

    /**
     * 密码
     */
    @Size(max = 255, message = "密码长度不能超过255字符")
    private String password;

    /**
     * 从号码
     */
    @Size(max = 100, message = "从号码长度不能超过100字符")
    private String fromUser;

    /**
     * 从域名
     */
    @Size(max = 100, message = "从域名长度不能超过100字符")
    private String fromDomain;

    /**
     * 注册
     */
    @lombok.Builder.Default
    private Boolean register = true;

    /**
     * 注册传输协议
     */
    @Size(max = 20, message = "注册传输协议长度不能超过20字符")
    @lombok.Builder.Default
    private String registerTransport = "udp";

    /**
     * 是否启用
     */
    @lombok.Builder.Default
    private Boolean enabled = true;

    /**
     * 扩展配置（JSON格式）
     */
    private String configJson;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500字符")
    private String remarks;

    // 以下字段用于搜索，创建/更新时不需要验证

    /**
     * 网关状态 - 用于搜索
     */
    private String status;
}
