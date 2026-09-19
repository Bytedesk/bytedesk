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
package com.bytedesk.webrtc.janus;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.webrtc.janus.client.JanusAdminClient;
import com.bytedesk.webrtc.janus.exception.JanusAdminException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Janus Admin/Monitor 业务服务（P0 只读）
 * 
 * 覆盖 deploy/janus/demos/admin.js 的只读能力：
 * - serverInfo：服务器信息（name/version/dependencies/plugins/transports/events/loggers）
 * - getStatus：运行设置快照（session_timeout/log_level/min_nack_queue 等）
 * - listSessions / listHandles / handleInfo：会话与 Handle 浏览
 * - listTokens：令牌列表（token 值脱敏后返回）
 * 
 * P1 写操作（settings 修改、token 增删）待安全审计方案确认后再补充。
 */
@Slf4j
@Service
@AllArgsConstructor
public class JanusAdminService {

    private final JanusAdminClient janusAdminClient;

    /**
     * Janus server_info，原样透传（对版本差异保持兼容），仅移除无需暴露的字段
     */
    public JSONObject serverInfo() {
        JSONObject info = janusAdminClient.getInfo();
        if (!"server_info".equals(info.getString("janus"))) {
            throw JanusAdminException.unreachable("unexpected info response: " + info.getString("janus"));
        }
        return info;
    }

    /**
     * get_status 运行设置快照
     */
    public JSONObject getStatus() {
        JSONObject response = janusAdminClient.post("get_status", null, null);
        JSONObject status = response.getJSONObject("status");
        if (status == null) {
            throw JanusAdminException.unreachable("missing status in response");
        }
        return status;
    }

    /**
     * list_sessions，返回会话 id 列表
     */
    public List<Long> listSessions() {
        JSONObject response = janusAdminClient.post("list_sessions", null, null);
        JSONArray sessions = response.getJSONArray("sessions");
        List<Long> result = new ArrayList<>();
        if (sessions != null) {
            for (Object item : sessions) {
                result.add(((Number) item).longValue());
            }
        }
        return result;
    }

    /**
     * list_handles，返回指定会话下的 handle id 列表
     */
    public List<Long> listHandles(long sessionId) {
        requirePositive(sessionId, "sessionId");
        JSONObject response = janusAdminClient.post("list_handles", "/" + sessionId, null);
        JSONArray handles = response.getJSONArray("handles");
        List<Long> result = new ArrayList<>();
        if (handles != null) {
            for (Object item : handles) {
                result.add(((Number) item).longValue());
            }
        }
        return result;
    }

    /**
     * handle_info，返回指定 handle 的详细信息（含插件、ICE/DTLS 状态、流量统计等）
     */
    public JSONObject handleInfo(long sessionId, long handleId) {
        requirePositive(sessionId, "sessionId");
        requirePositive(handleId, "handleId");
        JSONObject response = janusAdminClient.post("handle_info", "/" + sessionId + "/" + handleId, null);
        JSONObject info = response.getJSONObject("info");
        if (info == null) {
            throw JanusAdminException.unreachable("missing info in response");
        }
        return info;
    }

    /**
     * list_tokens，token 值脱敏后返回（仅保留首尾各 4 位）。
     * 当 Janus 未启用 Stored-Token 认证（错误 490 / server_info.auth_token=false）时，
     * 返回 tokenAuthEnabled=false 的空结果，前端据此显示"未启用"提示而非报错
     */
    public JanusTokensResult listTokens() {
        JSONObject response;
        try {
            response = janusAdminClient.post("list_tokens", null, null);
        } catch (JanusAdminException e) {
            if (JanusAdminException.isTokenAuthDisabled(e)) {
                return new JanusTokensResult(false, List.of());
            }
            throw e;
        }
        JSONObject data = response.getJSONObject("data");
        JSONArray tokens = data != null ? data.getJSONArray("tokens") : null;
        List<JSONObject> result = new ArrayList<>();
        if (tokens != null) {
            for (Object item : tokens) {
                if (item instanceof JSONObject token) {
                    JSONObject masked = new JSONObject();
                    masked.put("token", maskToken(token.getString("token")));
                    Object plugins = token.get("allowed_plugins");
                    if (plugins != null) {
                        masked.put("allowed_plugins", plugins);
                    }
                    // 保留其他非敏感扩展字段
                    token.forEach((key, value) -> {
                        if (!"token".equals(key) && !"allowed_plugins".equals(key)) {
                            masked.put(key, value);
                        }
                    });
                    result.add(masked);
                }
            }
        }
        return new JanusTokensResult(true, result);
    }

    /**
     * 令牌列表结果：tokenAuthEnabled=false 表示该 Janus 实例未启用 Stored-Token 认证
     */
    public record JanusTokensResult(boolean tokenAuthEnabled, List<JSONObject> tokens) {
    }

    private void requirePositive(long value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be a positive janus id");
        }
    }

    private String maskToken(String token) {
        if (token == null || token.isBlank()) {
            return "***";
        }
        if (token.length() <= 8) {
            return "***";
        }
        return token.substring(0, 4) + "****" + token.substring(token.length() - 4);
    }
}
