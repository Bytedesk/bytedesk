/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-05 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-05 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.provider.zhipuai;

import org.springframework.util.StringUtils;

/**
 * 智谱 AI (z-ai-sdk) baseUrl 规范化工具
 *
 * <p>背景：z-ai-sdk（ai.z.openapi.ZhipuAiClient）内部基于 Retrofit 实现，
 * 存在两个硬性约束：</p>
 * <ul>
 * <li>Retrofit 的 {@code Builder.baseUrl()} 要求 baseUrl 必须以 "/" 结尾，
 * 否则抛出 {@code IllegalArgumentException: baseUrl must end in /: ...}；</li>
 * <li>SDK 的各 API 接口均为相对路径（如 {@code @POST("embeddings")}），
 * 因此 baseUrl 必须包含版本段 "/v4/"，否则实际请求地址 404。</li>
 * </ul>
 *
 * <p>用户在管理后台 Embedding Settings 页面常按官方文档填写的
 * "https://open.bigmodel.cn/api/paas"（无 /v4、无结尾斜杠）会导致构建客户端失败。
 * 此工具在使用方传入 baseUrl 前统一补齐 "/v4/" 与结尾 "/"。</p>
 */
public final class ZhipuaiUrlUtils {

    /** 智谱 AI 开放平台官方 API 地址（含版本段与结尾斜杠） */
    public static final String DEFAULT_ZHIPUAI_BASE_URL = "https://open.bigmodel.cn/api/paas/v4/";

    private static final String VERSION_SEGMENT = "/v4";

    private ZhipuaiUrlUtils() {
    }

    /**
     * 规范化智谱 AI baseUrl：
     * <ol>
     * <li>去除首尾空白与多余的结尾 "/"；</li>
     * <li>缺少 "/v4" 版本段时自动补齐（如 "https://open.bigmodel.cn/api/paas"）；</li>
     * <li>保证最终以 "/v4/" 结尾，满足 Retrofit baseUrl 校验。</li>
     * </ol>
     *
     * @param baseUrl 原始 baseUrl，可为 null/空白（返回官方默认地址）
     * @return 形如 "https://open.bigmodel.cn/api/paas/v4/" 的规范地址
     */
    public static String normalizeBaseUrl(String baseUrl) {
        if (!StringUtils.hasText(baseUrl)) {
            return DEFAULT_ZHIPUAI_BASE_URL;
        }
        String url = baseUrl.trim();
        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (url.isEmpty()) {
            return DEFAULT_ZHIPUAI_BASE_URL;
        }
        if (!url.endsWith(VERSION_SEGMENT)) {
            url = url + VERSION_SEGMENT;
        }
        return url + "/";
    }
}
