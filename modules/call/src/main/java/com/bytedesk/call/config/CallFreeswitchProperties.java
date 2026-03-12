/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 10:35:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-12 17:29:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.config;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.Data;

/**
 * Call配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "bytedesk.call.freeswitch")
public class CallFreeswitchProperties {

    /**
     * FreeSWITCH conf 目录（用于落盘 directory/*.xml）
     */
    private String confDir = "deploy/freeswitch/conf";

    /**
     * 是否启用Call
     */
    private boolean enabled = false;

    /**
     * Call服务器地址
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
     * ESL连接超时（秒）
     */
    private int connectTimeoutSeconds = 30;

    /**
     * ESL连接重试次数
     */
    private int maxRetries = 5;

    /**
     * ESL连接初始重试间隔（毫秒）
     */
    private int retryDelayMs = 3000;

    /**
     * ESL事件订阅参数，对应 event plain {subscriptions}
     * 默认 all，可配置为 "CHANNEL_CREATE CHANNEL_DESTROY ..."
     */
    private String eventSubscriptions = "all";

    /**
     * 是否启用Event-Name过滤器
     */
    private boolean enableEventFilters = true;

    /**
     * Event-Name 过滤列表（FusionPBX风格：event plain all + filter Event-Name xxx）
     */
    private List<String> eventNameFilters = new ArrayList<>(List.of(
            "CHANNEL_CREATE",
            "CHANNEL_ANSWER",
            "CHANNEL_HANGUP",
            "CHANNEL_HANGUP_COMPLETE",
            "CHANNEL_DESTROY",
            "CHANNEL_EXECUTE",
            "CHANNEL_EXECUTE_COMPLETE",
            "CHANNEL_STATE",
            "CHANNEL_CALLSTATE",
            "DTMF",
            "PRESENCE_IN",
            "CUSTOM",
            "API",
            "BACKGROUND_JOB"));

    /**
     * Event-Subclass 过滤列表（例如 conference::maintenance、sofia::register）
     */
    private List<String> eventSubclassFilters = new ArrayList<>();

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

    public Path resolveConfDirPath() {
        String configured = StringUtils.hasText(confDir) ? confDir.trim() : "deploy/freeswitch/conf";
        Path configuredPath = Path.of(configured);
        if (configuredPath.isAbsolute()) {
            return configuredPath.normalize();
        }

        Path workingDir = Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        Path fromWorkingDir = workingDir.resolve(configuredPath).normalize();
        if (Files.exists(fromWorkingDir)) {
            return fromWorkingDir;
        }

        Path parent = workingDir.getParent();
        if (parent != null) {
            Path fromParent = parent.resolve(configuredPath).normalize();
            if (Files.exists(fromParent)) {
                return fromParent;
            }
        }
        return fromWorkingDir;
    }
}
