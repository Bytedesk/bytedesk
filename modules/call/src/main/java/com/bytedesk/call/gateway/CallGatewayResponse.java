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
package com.bytedesk.call.gateway;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Call网关响应实体
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CallGatewayResponse extends BaseResponse {

    /**
     * 网关名称
     */
    private String gatewayName;

    /**
     * 网关描述
     */
    private String description;

    /**
     * SIP服务器地址
     */
    private String proxy;

    /**
     * 用户名
     */
    private String username;

    /**
     * 从号码
     */
    private String fromUser;

    /**
     * 从域名
     */
    private String fromDomain;

    /**
     * 注册
     */
    private Boolean register;

    /**
     * 注册传输协议
     */
    private String registerTransport;

    /**
     * 网关状态
     */
    private String status;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 扩展配置（JSON格式）
     */
    private String configJson;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 是否在线
     */
    private Boolean online;

    /**
     * 从实体创建响应对象
     */
    public static CallGatewayResponse fromEntity(CallGatewayEntity entity) {
        return CallGatewayResponse.builder()
                .uid(entity.getUid())
                .gatewayName(entity.getGatewayName())
                .description(entity.getDescription())
                .proxy(entity.getProxy())
                .username(entity.getUsername())
                .fromUser(entity.getFromUser())
                .fromDomain(entity.getFromDomain())
                .register(entity.getRegister())
                .registerTransport(entity.getRegisterTransport())
                .status(entity.getStatus())
                .enabled(entity.getEnabled())
                .configJson(entity.getConfigJson())
                .remarks(entity.getRemarks())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .online(entity.isOnline())
                .build();
    }

    /**
     * 从实体创建响应对象（隐藏密码）
     */
    public static CallGatewayResponse fromEntitySafe(CallGatewayEntity entity) {
        CallGatewayResponse response = fromEntity(entity);
        // 不返回密码信息
        return response;
    }
}
