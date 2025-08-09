/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 10:35:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-24 10:40:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * FreeSwitch配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "bytedesk.freeswitch")
public class FreeSwitchProperties {

    /**
     * 是否启用FreeSwitch
     */
    private boolean enabled = false;
    
    /**
     * FreeSwitch服务器地址
     */
    private String server = "127.0.0.1";
    
    /**
     * ESL端口
     */
    private int eslPort = 8021;
    
    /**
     * ESL密码
     */
    private String eslPassword = "bytedesk123";
    
    /**
     * SIP端口
     */
    private int sipPort = 15060;
    
    /**
     * WebRTC端口
     */
    private int webrtcPort = 17443;
    
    /**
     * WebSocket信令端口
     */
    private int wsPort = 15066;
    
    /**
     * 默认呼叫超时时间(秒)
     */
    private int callTimeout = 60;
    
    /**
     * RTP媒体端口范围开始
     */
    private int rtpPortStart = 16000;
    
    /**
     * RTP媒体端口范围结束
     */
    private int rtpPortEnd = 16129;
}
