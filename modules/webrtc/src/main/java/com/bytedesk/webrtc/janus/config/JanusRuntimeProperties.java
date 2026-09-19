/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-19 00:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-19 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is prohibited – see the LICENSE for details.
 *   仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   技术/商务联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.webrtc.janus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.Getter;

/**
 * Janus Admin/Monitor 运行时配置
 * 
 * 复用 bytedesk.webrtc.janus.* 命名空间（各 profile 的 webrtc.properties），
 * 真实连接地址、密钥均由配置注入，代码中不写死任何演示地址。
 */
@Getter
@Component
public class JanusRuntimeProperties {

    @Value("${bytedesk.webrtc.janus.enabled:false}")
    private boolean enabled;

    @Value("${bytedesk.webrtc.janus.admin.enabled:false}")
    private boolean adminEnabled;

    /**
     * Janus Admin/Monitor HTTP API 地址，例如 http://127.0.0.1:18091/admin
     */
    @Value("${bytedesk.webrtc.janus.admin.http-url:}")
    private String adminHttpUrl;

    /**
     * Admin API 密钥，须与 janus.jcfg 的 admin_secret 一致
     */
    @Value("${bytedesk.webrtc.janus.admin.secret:}")
    private String adminSecret;

    /**
     * 单次 Admin API 请求超时（毫秒）
     */
    @Value("${bytedesk.webrtc.janus.admin.timeout-ms:3000}")
    private long adminTimeoutMs;

    public boolean isAdminConfigured() {
        return adminEnabled && StringUtils.hasText(adminHttpUrl) && StringUtils.hasText(adminSecret);
    }
}
