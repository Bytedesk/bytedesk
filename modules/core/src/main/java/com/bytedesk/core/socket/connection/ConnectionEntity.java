/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:35:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.connection;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Connection entity for tracking user/agent live connections across devices and protocols.
 * Supports multi-client online status by recording each client connection session.
 *
 * Database Table: bytedesk_core_connection
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({ConnectionEntityListener.class})
@Table(name = "bytedesk_core_connection")
public class ConnectionEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // ================= Session identity =================
    /** 关联用户 UID（客服或访客均可） */
    @Column(name = "user_uid")
    private String userUid;

    /** 组织 UID（可选，用于范围统计） */
    @Column(name = "org_uid")
    private String orgUid;

    /** 客户端唯一标识（如 MQTT clientId、WS sessionId 等） */
    @Column(name = "client_id")
    private String clientId;

    /** 设备 UID（可选，来自 clientId 或 SDK） */
    @Column(name = "device_uid")
    private String deviceUid;

    // ================= Session meta =================
    /** 平台：WEB / IOS / ANDROID / DESKTOP / OTHER */
    @Column(name = "platform")
    private String platform;

    /** 协议：MQTT / WS / SSE / OTHER */
    @Column(name = "protocol")
    private String protocol;

    /** 渠道：如 WEB_VISITOR / WEB / APP 等（与系统 channel 对齐） */
    @Column(name = "channel")
    private String channel;

    /** 客户端 IP */
    @Column(name = "ip")
    private String ip;

    /** User-Agent（可选，长度较长） */
    @Column(name = "user_agent", length = 512)
    private String userAgent;

    // ================= Session lifecycle =================
    /** 连接状态：CONNECTED / DISCONNECTED */
    @Builder.Default
    @Column(name = "status")
    private String status = ConnectionStatusEnum.DISCONNECTED.name();

    /** 首次连接毫秒时间戳 */
    @Column(name = "connected_at")
    private Long connectedAt;

    /** 最近心跳/活跃毫秒时间戳 */
    @Column(name = "last_heartbeat_at")
    private Long lastHeartbeatAt;

    /** 断开连接毫秒时间戳 */
    @Column(name = "disconnected_at")
    private Long disconnectedAt;

    /** 心跳存活 TTL（秒），用于判定是否在线（lastHeartbeatAt 未过期） */
    @Builder.Default
    @Column(name = "ttl_seconds")
    private Integer ttlSeconds = 90;

    // ================= Legacy fields (kept for compatibility) =================
    /** 名称与描述历史字段（保留兼容） */
    private String name;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /** 历史类型字段（保留兼容，不再用于在线态判定） */
    @Builder.Default
    @Column(name = "connection_type")
    private String type = ConnectionTypeEnum.CUSTOMER.name();

}
