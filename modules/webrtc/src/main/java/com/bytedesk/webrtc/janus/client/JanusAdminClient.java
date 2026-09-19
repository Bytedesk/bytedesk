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
package com.bytedesk.webrtc.janus.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.webrtc.janus.config.JanusRuntimeProperties;
import com.bytedesk.webrtc.janus.exception.JanusAdminException;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * Janus Admin/Monitor HTTP API 客户端
 * 
 * 协议参考 deploy/janus/demos/admin.js（该文件中的 server 地址仅为演示，不可复用）。
 * 真实地址与密钥来自 webrtc.properties 的 bytedesk.webrtc.janus.admin.* 配置。
 * 
 * 协议约定：
 * - GET  {base}/info                       服务器信息
 * - POST {base}                            get_status / list_sessions / list_tokens 等
 * - POST {base}/{sessionId}                list_handles
 * - POST {base}/{sessionId}/{handleId}     handle_info
 * - 所有 POST 请求体均携带 transaction 与 admin_secret
 */
@Slf4j
@Component
public class JanusAdminClient {

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final JanusRuntimeProperties properties;

    private final HttpClient httpClient;

    public JanusAdminClient(JanusRuntimeProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(Math.max(500L, properties.getAdminTimeoutMs())))
                .build();
    }

    /**
     * GET {base}/info，返回 Janus server_info 原始 JSON
     */
    public JSONObject getInfo() {
        // 注意：必须拼上 /info，GET 到 {base} 根路径会被 Janus 判为 454 Request payload missing
        String url = baseUrl() + "/info";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(properties.getAdminTimeoutMs()))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        return execute(url, request);
    }

    /**
     * POST {base}{path}，请求体自动注入 janus/transaction/admin_secret 与附加参数
     * 
     * @param command janus 命令，如 get_status / list_sessions / list_handles / handle_info / list_tokens
     * @param path    相对路径，如 "" / "/{sessionId}" / "/{sessionId}/{handleId}"，仅允许由服务层拼接的数字段
     * @param extra   附加参数，可为 null
     */
    public JSONObject post(String command, String path, JSONObject extra) {
        String url = baseUrl() + sanitizePath(path);
        JSONObject body = new JSONObject();
        body.put("janus", command);
        body.put("transaction", randomTransaction());
        body.put("admin_secret", properties.getAdminSecret());
        if (extra != null) {
            body.putAll(extra);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(properties.getAdminTimeoutMs()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toJSONString()))
                .build();
        return execute(url, request);
    }

    private String baseUrl() {
        if (!properties.isAdminConfigured()) {
            throw JanusAdminException.disabled();
        }
        String url = properties.getAdminHttpUrl().strip();
        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    /**
     * 路径只允许空串或纯数字段（/123、/123/456），防止路径注入
     */
    private String sanitizePath(String path) {
        if (!StringUtils.hasText(path)) {
            return "";
        }
        String normalized = path.strip();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        for (String segment : normalized.split("/")) {
            if (segment.isEmpty()) {
                continue;
            }
            for (char c : segment.toCharArray()) {
                if (!Character.isDigit(c)) {
                    throw new IllegalArgumentException("invalid janus path segment: digits only");
                }
            }
        }
        return normalized;
    }

    private JSONObject execute(String url, HttpRequest request) {
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw JanusAdminException.unreachable("interrupted");
        } catch (IOException e) {
            log.warn("janus admin request failed: {}", e.getClass().getSimpleName());
            throw JanusAdminException.unreachable(e.getMessage());
        }

        int httpStatus = response.statusCode();
        if (httpStatus < 200 || httpStatus >= 300) {
            // 403 由 Janus 在 secret 错误时返回
            if (httpStatus == HttpStatus.FORBIDDEN.value()) {
                throw JanusAdminException.secretMismatch();
            }
            throw JanusAdminException.unreachable("http status " + httpStatus);
        }

        String responseBody = response.body();
        if (!StringUtils.hasText(responseBody)) {
            throw JanusAdminException.unreachable("empty response body");
        }

        JSONObject json;
        try {
            json = JSON.parseObject(responseBody);
        } catch (Exception e) {
            throw JanusAdminException.unreachable("invalid json response");
        }
        if (json == null) {
            throw JanusAdminException.unreachable("empty json response");
        }

        // Janus 业务错误：{"janus":"error","error":{"code":403,"reason":"..."}}
        if ("error".equals(json.getString("janus"))) {
            JSONObject error = json.getJSONObject("error");
            int code = error != null ? error.getIntValue("code") : -1;
            String reason = error != null ? error.getString("reason") : null;
            if (code == 403) {
                throw JanusAdminException.secretMismatch();
            }
            throw JanusAdminException.ofCode(code, reason);
        }
        return json;
    }

    private String randomTransaction() {
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            sb.append(CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    @PreDestroy
    public void close() {
        // java.net.http.HttpClient 无显式 close（JDK21），保留扩展点
    }
}
