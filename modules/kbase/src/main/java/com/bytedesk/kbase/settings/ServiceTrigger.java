/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-07 10:27:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 15:17:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 触发条件配置
 * 示例JSON:
 * {
 *     "conditions": [
 *         {
 *             "type": "no_response",
 *             "timeout": 300
 *         },
 *         {
 *             "type": "keyword_match",
 *             "keywords": ["价格", "费用"],
 *             "message": "您似乎对价格感兴趣，这里有一些相关信息..."
 *         },
 *         {
 *             "type": "visit_count",
 *             "threshold": 3,
 *             "message": "欢迎您再次访问..."
 *         },
 *         {
 *             "type": "page_stay",
 *             "timeout": 300,
 *             "message": "您似乎在浏览页面，需要帮助吗？"
 *         },
 *         {
 *             "type": "time_range",
 *             "start": "09:00",
 *             "end": "18:00",
 *             "message": "您似乎在浏览页面，需要帮助吗？"
 *         },
 *         {
 *             "type": "auto_guide",
 *             "message": "您似乎在浏览页面，需要帮助吗？"
 *         },
 *         {
 *             "type": "queue_remind",
 *             "message": "您似乎在浏览页面，需要帮助吗？"
 *         }
 *     ]
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceTrigger {
    
    private List<TriggerCondition> conditions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TriggerCondition {
        private String type;           // 触发类型
        private Integer timeout;       // 超时时间（秒）
        private List<String> keywords; // 关键词列表
        private Integer threshold;     // 阈值
        private String message;        // 触发后发送的消息
        
        // 触发类型常量
        public static final String TYPE_NO_RESPONSE = "no_response";       // 无响应触发
        public static final String TYPE_KEYWORD_MATCH = "keyword_match";   // 关键词匹配触发
        public static final String TYPE_VISIT_COUNT = "visit_count";       // 访问次数触发
        public static final String TYPE_PAGE_STAY = "page_stay";          // 页面停留触发
        public static final String TYPE_TIME_RANGE = "time_range";        // 时间范围触发
        public static final String TYPE_AUTO_GUIDE = "auto_guide";        // 自动引导
        public static final String TYPE_QUEUE_REMIND = "queue_remind";    // 排队提醒
        /**
         * 验证触发条件是否有效
         */
        public Boolean isValid() {
            if (type == null) {
                return false;
            }
            
            switch (type) {
                case TYPE_NO_RESPONSE:
                    return timeout != null && timeout > 0;
                case TYPE_KEYWORD_MATCH:
                    return keywords != null && !keywords.isEmpty();
                case TYPE_VISIT_COUNT:
                    return threshold != null && threshold > 0;
                case TYPE_PAGE_STAY:
                    return timeout != null && timeout > 0;
                case TYPE_TIME_RANGE:
                    return true; // 时间范围触发可能有其他特定字段
                default:
                    return false;
            }
        }
    }

    /**
     * 验证整个触发配置是否有效
     */
    public Boolean isValid() {
        if (conditions == null || conditions.isEmpty()) {
            return false;
        }
        
        return conditions.stream().allMatch(TriggerCondition::isValid);
    }

    /**
     * 检查是否可以触发条件
     * @param userUid 用户ID
     * @param settings 服务设置
     * @param condition 触发条件
     * @return 是否可以触发
     */
    public Boolean canTrigger(String userUid, ServiceSettings settings, TriggerCondition condition) {
        // 1. 检查基本开关
        if (!settings.getEnableProactiveTrigger()) {
            return false;
        }

        // 2. 检查灰度发布
        // if (!settings.getGrayReleaseConfig().isUserInGrayRelease(userUid, "proactive_trigger")) {
        //     return false;
        // }

        // 3. 检查触发条件是否有效
        return condition.isValid();
    }

    // 创建默认触发配置
    public static ServiceTrigger createDefaultTrigger() {
        return ServiceTrigger.builder()
        .conditions(Arrays.asList(
            ServiceTrigger.TriggerCondition.builder()
                .type(ServiceTrigger.TriggerCondition.TYPE_NO_RESPONSE)
                .timeout(300)
                .message("您好，需要帮助吗？")
                .build(),
            ServiceTrigger.TriggerCondition.builder()
                .type(ServiceTrigger.TriggerCondition.TYPE_KEYWORD_MATCH)
                .keywords(Arrays.asList("价格", "费用"))
                .message("您似乎对价格感兴趣，这里有一些相关信息...")
                .build()
        ))
        .build();
    }

}
