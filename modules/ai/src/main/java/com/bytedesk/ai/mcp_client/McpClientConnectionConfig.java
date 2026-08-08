package com.bytedesk.ai.mcp_client;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.bytedesk.ai.mcp_server.McpServerEntity;

import lombok.Builder;

@Builder
public record McpClientConnectionConfig(
        String uid,
        String name,
        McpTransportTypeEnum transportType,
        boolean enabled,
        String serverUrl,
        String authType,
        String authCredential,
        Map<String, String> customHeaders,
        String stdioCommand,
        List<String> stdioArgs,
        String stdioWorkDir,
        Map<String, String> environmentVars,
        int connectionTimeoutMs,
        int requestTimeoutMs,
        int maxRetries,
        List<String> toolAllowList) {

    public static McpClientConnectionConfig fromEntity(McpServerEntity entity, McpClientProperties properties) {
        ServerConfigPayload serverConfig = parseServerConfig(entity.getServerConfig());
        return McpClientConnectionConfig.builder()
                .uid(entity.getUid())
                .name(entity.getName())
                .transportType(resolveTransportType(serverConfig.transportType()))
                .enabled(Boolean.TRUE.equals(entity.getEnabled()))
                .serverUrl(serverConfig.serverUrl())
                .authType(serverConfig.authType())
                .authCredential(serverConfig.authCredential())
                .customHeaders(serverConfig.customHeaders() != null ? serverConfig.customHeaders() : Collections.emptyMap())
                .stdioCommand(serverConfig.stdioCommand())
                .stdioArgs(serverConfig.stdioArgs() != null ? serverConfig.stdioArgs() : Collections.emptyList())
                .stdioWorkDir(serverConfig.stdioWorkDir())
                .environmentVars(serverConfig.environmentVars() != null ? serverConfig.environmentVars() : Collections.emptyMap())
                .connectionTimeoutMs(resolveInt(serverConfig.connectionTimeoutMs(), properties.getConnectionTimeoutMs()))
                .requestTimeoutMs(resolveInt(serverConfig.requestTimeoutMs(), properties.getRequestTimeoutMs()))
                .maxRetries(resolveInt(serverConfig.maxRetries(), 3))
                .toolAllowList(serverConfig.toolAllowList() != null ? serverConfig.toolAllowList() : Collections.emptyList())
                .build();
    }

    private static McpTransportTypeEnum resolveTransportType(String transportType) {
        if (!StringUtils.hasText(transportType)) {
            return McpTransportTypeEnum.STREAMABLE_HTTP;
        }
        try {
            return McpTransportTypeEnum.valueOf(transportType.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return McpTransportTypeEnum.STREAMABLE_HTTP;
        }
    }

    private static int resolveInt(Integer value, int defaultValue) {
        return value != null && value > 0 ? value : defaultValue;
    }

    private static ServerConfigPayload parseServerConfig(String json) {
        if (!StringUtils.hasText(json)) {
            return ServerConfigPayload.empty();
        }
        try {
            ServerConfigPayload payload = JSON.parseObject(json, new TypeReference<ServerConfigPayload>() {
            });
            return payload != null ? payload : ServerConfigPayload.empty();
        } catch (Exception ex) {
            return ServerConfigPayload.empty();
        }
    }

    private record ServerConfigPayload(
            String transportType,
            String serverUrl,
            String authType,
            String authCredential,
            Map<String, String> customHeaders,
            String stdioCommand,
            List<String> stdioArgs,
            String stdioWorkDir,
            Map<String, String> environmentVars,
            Integer connectionTimeoutMs,
            Integer requestTimeoutMs,
            Integer maxRetries,
            List<String> toolAllowList) {

        private static ServerConfigPayload empty() {
            return new ServerConfigPayload(null, null, null, null, Collections.emptyMap(), null, Collections.emptyList(), null,
                    Collections.emptyMap(), null, null, null, Collections.emptyList());
        }
    }
}