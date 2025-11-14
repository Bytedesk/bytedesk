/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-01 16:57:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-01 17:17:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.email;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 邮件监听配置
 * 用于管理不同协议的监听策略
 */
@Data
@Component
@ConfigurationProperties(prefix = "bytedesk.email.listener")
public class EmailListenerConfig {

    /**
     * 是否启用IDLE监听（仅支持IMAP协议）
     */
    private boolean idleEnabled = true;

    /**
     * 是否启用轮询同步（支持所有协议）
     */
    private boolean pollingEnabled = true;

    /**
     * IDLE监听检查间隔（毫秒）
     */
    private long idleCheckInterval = 30000; // 30秒

    /**
     * 轮询同步最小间隔（分钟）
     */
    private int minPollingInterval = 1;

    /**
     * 轮询同步最大间隔（分钟）
     */
    private int maxPollingInterval = 60;

    /**
     * 连接超时时间（毫秒）
     */
    private long connectionTimeout = 30000; // 30秒

    /**
     * 读取超时时间（毫秒）
     */
    private long readTimeout = 30000; // 30秒

    /**
     * 重连间隔（毫秒）
     */
    private long reconnectInterval = 5000; // 5秒

    /**
     * 最大重连次数
     */
    private int maxReconnectAttempts = 3;

    /**
     * 是否启用调试日志
     */
    private boolean debugEnabled = false;

    /**
     * 获取监听策略
     * @param protocol 邮件协议
     * @return 监听策略
     */
    public ListenerStrategy getListenerStrategy(String protocol) {
        if (EmailProtocolEnum.IMAP.name().equals(protocol)) {
            if (idleEnabled) {
                return ListenerStrategy.IDLE;
            } else if (pollingEnabled) {
                return ListenerStrategy.POLLING;
            }
        } else if (EmailProtocolEnum.POP3.name().equals(protocol) || 
                   EmailProtocolEnum.EXCHANGE.name().equals(protocol)) {
            if (pollingEnabled) {
                return ListenerStrategy.POLLING;
            }
        }
        return ListenerStrategy.NONE;
    }

    /**
     * 监听策略枚举
     */
    public enum ListenerStrategy {
        /**
         * IDLE监听（实时）
         */
        IDLE,
        
        /**
         * 轮询同步（定时）
         */
        POLLING,
        
        /**
         * 无监听
         */
        NONE
    }
} 