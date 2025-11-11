/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:50:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.connection;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.socket.mqtt.event.MqttConnectedEvent;
import com.bytedesk.core.socket.mqtt.event.MqttDisconnectedEvent;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ConnectionEventListener {

    private final ConnectionRestService connectionRestService;

    @EventListener
    public void onMqttConnectedEvent(MqttConnectedEvent event) {
        String clientId = event.getClientId();
        // 用户clientId格式: uid/client/deviceUid
        final String userUid = clientId.split("/")[0];
        final String deviceUid = clientId.contains("/") && clientId.split("/").length > 2 ? clientId.split("/")[2] : null;
        log.info("user onMqttConnectedEvent uid {}, clientId {}", userUid, clientId);
        // 标记连接（使用 ConnectionEntity 支持多端在线）
        // 无法从事件中获取更多上下文，使用协议 MQTT，其它信息置空/默认
        connectionRestService.markConnected(userUid, null, clientId, deviceUid,
                ConnectionProtocalEnum.MQTT.name(), null, null, null, 90);
    }

    @EventListener
    public void onMqttDisconnectedEvent(MqttDisconnectedEvent event) {
        String clientId = event.getClientId();
        // 用户clientId格式: uid/client/deviceUid
        final String userUid = clientId.split("/")[0];
        log.info("user onMqttDisconnectedEvent uid {}, clientId {}", userUid, clientId);
        // 先标记该 client 断开
        connectionRestService.markDisconnected(clientId);
    }

    /**
     * 每分钟调度：清理过期连接，移除超出 TTL 的会话，保持在线状态准确。
     */
    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        connectionRestService.expireStaleSessions();
    }
}

