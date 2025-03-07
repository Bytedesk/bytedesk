/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-07 11:06:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-07 11:07:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.gray_release;

import lombok.Getter;

/**
 * 灰度发布功能枚举
 */
@Getter
public enum GrayReleaseFeature {
    
    PROACTIVE_TRIGGER("proactive_trigger", "机器人主动触发功能"),
    SMART_ROUTING("smart_routing", "智能路由功能"),
    SENTIMENT_ANALYSIS("sentiment_analysis", "情感分析功能"),
    AUTO_REPLY("auto_reply", "自动回复功能"),
    KNOWLEDGE_PUSH("knowledge_push", "知识库推送功能"),
    CHAT_SUMMARY("chat_summary", "对话总结功能"),
    INTENT_RECOGNITION("intent_recognition", "意图识别功能"),
    MULTI_ROUND_CHAT("multi_round_chat", "多轮对话功能");

    private final String code;        // 功能代码
    private final String description; // 功能描述

    GrayReleaseFeature(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据功能代码获取功能枚举
     */
    public static GrayReleaseFeature getByCode(String code) {
        for (GrayReleaseFeature feature : values()) {
            if (feature.getCode().equals(code)) {
                return feature;
            }
        }
        return null;
    }
}
