/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-17 17:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-18 16:43:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.event;

import java.math.BigDecimal;

import org.springframework.context.ApplicationEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI Token使用统计事件
 * 用于解耦AI服务和Token统计服务
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LlmTokenUsageEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    /**
     * 组织UID
     */
    private final String orgUid;

    /**
     * AI提供商 (openai, baidu, zhipuai, etc.)
     */
    private final String aiProvider;

    /**
     * AI模型类型 (gpt-4, gpt-3.5, claude, gemini, etc.)
     */
    private final String aiModelType;

    /**
     * Prompt token数量
     */
    private final long promptTokens;

    /**
     * Completion token数量
     */
    private final long completionTokens;

    /**
     * 请求是否成功
     */
    private final boolean success;

    /**
     * 响应时间（毫秒）
     */
    private final long responseTime;

    /**
     * Token单价（USD）
     */
    private final BigDecimal tokenUnitPrice;

    public LlmTokenUsageEvent(Object source, String orgUid, String aiProvider, String aiModelType,
            long promptTokens, long completionTokens, boolean success, long responseTime, BigDecimal tokenUnitPrice) {
        super(source);
        this.orgUid = orgUid;
        this.aiProvider = aiProvider;
        this.aiModelType = aiModelType;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.success = success;
        this.responseTime = responseTime;
        this.tokenUnitPrice = tokenUnitPrice;
    }
} 