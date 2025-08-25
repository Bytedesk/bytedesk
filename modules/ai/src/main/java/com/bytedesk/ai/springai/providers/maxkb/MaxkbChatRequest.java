/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-23 07:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-23 07:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.maxkb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * MaxKB 聊天完成请求类 - OpenAI 兼容格式
 * https://maxkb.cn/docs/v1/dev_manual/APIKey_chat/#1-openai-api
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaxkbChatRequest {

    /**
     * 模型名称，例如 "gpt-3.5-turbo"
     */
    @JsonProperty("model")
    @Builder.Default
    private String model = "gpt-3.5-turbo";

    /**
     * 消息列表，每个消息包含 role 和 content
     * role 可以是 "system", "user", "assistant"
     */
    @JsonProperty("messages")
    private List<Map<String, Object>> messages;

    /**
     * 是否使用流式响应
     */
    @JsonProperty("stream")
    @Builder.Default
    private Boolean stream = false;

    /**
     * 生成文本的最大长度
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * 温度参数，控制生成文本的随机性 (0.0 - 2.0)
     */
    @JsonProperty("temperature")
    private Double temperature;

    /**
     * 核采样参数 (0.0 - 1.0)
     */
    @JsonProperty("top_p")
    private Double topP;

    /**
     * 频率惩罚参数 (-2.0 - 2.0)
     */
    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty;

    /**
     * 存在惩罚参数 (-2.0 - 2.0)
     */
    @JsonProperty("presence_penalty")
    private Double presencePenalty;

    /**
     * 停止词列表
     */
    @JsonProperty("stop")
    private List<String> stop;

    /**
     * 生成的响应数量
     */
    @JsonProperty("n")
    private Integer n;

    /**
     * 是否返回概率信息
     */
    @JsonProperty("logprobs")
    private Boolean logprobs;

    /**
     * 返回的概率信息数量
     */
    @JsonProperty("top_logprobs")
    private Integer topLogprobs;

    /**
     * 用户ID，用于追踪用户行为
     */
    @JsonProperty("user")
    private String user;
}
