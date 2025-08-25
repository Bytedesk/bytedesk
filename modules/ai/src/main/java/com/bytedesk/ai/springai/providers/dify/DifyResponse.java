/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-25 13:58:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dify;

import com.bytedesk.core.base.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class DifyResponse extends BaseResponse {

    /**
     * 消息唯一标识符
     */
    private String id;

    /**
     * 对话 ID
     */
    private String conversationId;

    /**
     * 回答内容
     */
    private String answer;

    /**
     * Dify API 创建时间戳（与 BaseResponse 的 createdAt 区分）
     */
    private Integer difyCreatedAt;

    /**
     * 反馈评分，如果返回 like，代表赞，如果返回 dislike，代表踩，如果返回 null，代表没有反馈
     */
    private String feedback;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * 模型使用情况
     */
    private Usage usage;

    /**
     * 引用和归属分段列表
     */
    private List<RetrievalItem> retrieval;

    // 保留原有字段
    private String name;
    private String description;
    private String type;

    private String kbUid; // 对应知识库
    // 
    private Boolean enabled;

    private String apiUrl;

    private String apiKey;


    /**
     * 使用情况统计
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Usage {
        private Integer promptTokens;
        private Integer promptPrice;
        private String promptPriceUnit;
        private Integer completionTokens;
        private Integer completionPrice;
        private String completionPriceUnit;
        private Integer totalTokens;
        private Integer totalPrice;
        private String totalPriceUnit;
        private String currency;
        private Integer latency;
    }

    /**
     * 检索项
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RetrievalItem {
        private Integer position;
        private String documentId;
        private String documentName;
        private String title;
        private String content;
        private Double score;
        private String hitCount;
        private String wordCount;
        private String segmentPosition;
        private String indexNodeHash;
    }

    /**
     * 对话信息
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Conversation {
        private String id;
        private String name;
        private Map<String, Object> inputs;
        private String status;
        private String introduction;
        private Integer createdAt;
    }

    /**
     * 消息信息
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String id;
        private String conversationId;
        private Map<String, Object> inputs;
        private String query;
        private String answer;
        private String feedback;
        private List<RetrievalItem> retrieval;
        private Integer createdAt;
    }

    /**
     * 对话列表响应
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConversationListResponse {
        private Integer limit;
        private Boolean hasMore;
        private List<Conversation> data;
    }

    /**
     * 消息列表响应
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessageListResponse {
        private Integer limit;
        private Boolean hasMore;
        private List<Message> data;
    }
}
