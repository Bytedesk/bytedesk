/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-20 19:55:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-11-20 19:55:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, hosting, or deploying Bytedesk IM as a service is prohibited – see the LICENSE for details.
 *   仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.remote.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * 远程协助模块配置（properties/remote.properties）
 *
 * enabled=false 时：前端不下发 remote 能力（desktop/remote 不显示远程按钮），
 * 后端邀请码签发/连接接口同时拒绝（防止绕过前端直接调用）。
 * fail-closed：配置缺失按关闭处理。
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "bytedesk.remote")
public class RemoteProperties {

    /** 是否启用远程协助（bytedesk.remote.enabled，默认 false） */
    private boolean enabled = false;

    /** 邀请码有效期（分钟，bytedesk.remote.invitation.ttl-minutes，默认 5） */
    private int invitationTtlMinutes = 5;

    /** 单用户连接尝试上限/防刷（bytedesk.remote.invitation.max-connect-attempts，默认 10） */
    private int invitationMaxConnectAttempts = 10;

    /** 会话心跳间隔下限（毫秒，bytedesk.remote.session.heartbeat-interval-ms，默认 1500） */
    private long sessionHeartbeatIntervalMs = 1500;

    /** 控制授权租约上限（毫秒，bytedesk.remote.session.lease-max-ms，默认 5000） */
    private long sessionLeaseMaxMs = 5000;
}
