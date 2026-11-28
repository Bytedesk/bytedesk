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

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 阿里云百炼决策模型配置（{@code spring.ai.dashscope.decision.*}）。
 *
 * <p>由 {@link DashScopeDecisionConfig} 通过 {@code @EnableConfigurationProperties} 注册绑定；
 * apiKey 与 baseUrl 缺省继承 {@code spring.ai.dashscope.api-key} / {@code spring.ai.dashscope.base-url}
 * （见 starter 的 ai-dashscope.properties），此处只允许按需覆盖。</p>
 *
 * https://docs.bailian.console.aliyun.com/zh/model-studio/decision-model-api
 */
@Data
@ConfigurationProperties(prefix = "spring.ai.dashscope.decision")
public class DashScopeDecisionProperties {

    /** 是否启用决策模型接口（Controller 条件装配）。 */
    private Boolean enabled = Boolean.TRUE;

    /** 正式版 decision-model；预览版 decision-model-preview。 */
    private String model = "decision-model";

    /** 业务空间 ID；配置后按 SDK 规则拼接网关地址，优先级低于 baseUrl。 */
    private String workspaceId;

    /** 地域：cn-beijing（华北2 北京）/ ap-southeast-1（新加坡）。 */
    private String region = DashScopeDecisionEndpoint.DEFAULT_REGION;

    /** 显式 compatible-mode 端点（如代理场景），优先级最高。 */
    private String baseUrl;

    /** 可选：决策模型专用 key，为空时回退 spring.ai.dashscope.api-key。 */
    private String apiKey;

    /** 请求超时。 */
    private Duration timeout = Duration.ofSeconds(60);

}
