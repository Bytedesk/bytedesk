/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-26 15:10:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-11-26 15:10:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.aliyun.dashscope.decision;

import java.net.URI;

import org.springframework.util.StringUtils;

/**
 * 阿里云百炼决策模型端点解析。
 *
 * <p>决策模型走 TypeSafe System One 协议，端点固定挂在业务空间网关的 compatible-mode 路径下：
 * {@code POST {baseUrl}/systemone}，其中 baseUrl 形如
 * {@code https://{WorkspaceId}.cn-beijing.maas.aliyuncs.com/compatible-mode/v1}。</p>
 *
 * <p>解析顺序：</p>
 * <ol>
 *   <li>显式配置 {@code spring.ai.dashscope.decision.base-url}：原样使用（去掉末尾斜杠）。</li>
 *   <li>配置 {@code spring.ai.dashscope.decision.workspace-id}：按 SDK 规则拼接
 *       {@code https://{workspace}.{region}.maas.aliyuncs.com/compatible-mode/v1}。</li>
 *   <li>回退到 provider / 全局 {@code spring.ai.dashscope.base-url}：去除 {@code /api/v1} 等后缀后
 *       拼上 {@code /compatible-mode/v1}（本地业务空间网关通常配置为 {@code .../api/v1}）。</li>
 * </ol>
 *
 * https://docs.bailian.console.aliyun.com/zh/model-studio/decision-model-api
 */
public final class DashScopeDecisionEndpoint {

    /** 决策模型 SDK（DashScope Java SDK 2.23.3+）使用的业务空间网关模板。 */
    public static final String WORKSPACE_URL_TEMPLATE = "https://%s.%s.maas.aliyuncs.com/compatible-mode/v1";

    public static final String DEFAULT_REGION = "cn-beijing";

    /** System One 协议在 compatible-mode 下的路径前缀；Spring AI {@code SystemOneApi} 会自动追加 {@code /systemone}。 */
    public static final String COMPATIBLE_MODE_PREFIX = "/compatible-mode/v1";

    private DashScopeDecisionEndpoint() {
    }

    public static String resolve(String explicitBaseUrl, String workspaceId, String region, String fallbackBaseUrl) {
        if (StringUtils.hasText(explicitBaseUrl)) {
            return trimTrailingSlash(explicitBaseUrl.trim());
        }
        if (StringUtils.hasText(workspaceId)) {
            String resolvedRegion = StringUtils.hasText(region) ? region.trim() : DEFAULT_REGION;
            return String.format(WORKSPACE_URL_TEMPLATE, workspaceId.trim(), resolvedRegion);
        }
        if (StringUtils.hasText(fallbackBaseUrl)) {
            return fromDashScopeBaseUrl(fallbackBaseUrl);
        }
        return null;
    }

    /**
     * 将 DashScope 原生 baseUrl（{@code https://dashscope.aliyuncs.com[/api/v1]} 或业务空间网关
     * {@code https://{workspace}.cn-beijing.maas.aliyuncs.com/api/v1}）转换为 compatible-mode 端点。
     */
    public static String fromDashScopeBaseUrl(String baseUrl) {
        String trimmed = trimTrailingSlash(baseUrl.trim());
        try {
            URI uri = URI.create(trimmed);
            String path = uri.getPath();
            String root = uri.getScheme() != null ? uri.getScheme() + "://" + uri.getAuthority() : trimmed;
            if (!StringUtils.hasText(path) || "/".equals(path)) {
                return root + COMPATIBLE_MODE_PREFIX;
            }
            if (path.equals(COMPATIBLE_MODE_PREFIX) || path.startsWith(COMPATIBLE_MODE_PREFIX + "/")) {
                return trimmed;
            }
            if (path.equals("/v1") || path.startsWith("/v1/")) {
                // 部分网关直接以 /v1 暴露 systemone，保留原路径
                return trimmed;
            }
            // /api/v1、/api/v1/... 等原生路径：去掉后重新拼 compatible-mode
            return root + COMPATIBLE_MODE_PREFIX;
        } catch (IllegalArgumentException ex) {
            return trimmed + COMPATIBLE_MODE_PREFIX;
        }
    }

    private static String trimTrailingSlash(String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }
}
