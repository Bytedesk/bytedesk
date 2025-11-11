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
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Index;
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
@Table(name = "bytedesk_core_connection", indexes = {
    @Index(name = "idx_core_conn_client_id", columnList = "clientId")
})
public class ConnectionEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 客户端唯一标识（如 MQTT clientId、WS sessionId 等） */
    private String clientId;

    /** 设备 UID（可选，来自 clientId 或 SDK） */
    private String deviceUid;

    // ================= Session meta =================
    // 说明：平台 platform 已在 BaseEntity 中以 platform_type 列存在，避免重复定义

    /** 协议：MQTT / WS / SSE / OTHER */
    @Builder.Default
    private String protocol = ConnectionProtocalEnum.MQTT.name();

    /** 渠道：如 WEB_VISITOR / WEB / APP 等（与系统 channel 对齐） */
    private String channel;

    /** 客户端 IP */
    private String ip;

    /** User-Agent（可选，长度较长） */
    @Column(length = 512)
    private String userAgent;

    // ================= Session lifecycle =================
    /** 连接状态：CONNECTED / DISCONNECTED */
    @Builder.Default
    private String status = ConnectionStatusEnum.DISCONNECTED.name();

    /** 首次连接毫秒时间戳 */
    private Long connectedAt;

    /** 最近心跳/活跃毫秒时间戳 */
    private Long lastHeartbeatAt;

    /** 断开连接毫秒时间戳 */
    private Long disconnectedAt;

    /** 心跳存活 TTL（秒），用于判定是否在线（lastHeartbeatAt 未过期） */
    @Builder.Default
    private Integer ttlSeconds = 90;

}
