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

/**
 * MaxKB 简单消息请求类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaxkbMessageRequest {

    /**
     * 消息内容
     */
    @JsonProperty("content")
    private String content;

    /**
     * 是否使用流式响应
     */
    @JsonProperty("stream")
    @Builder.Default
    private Boolean stream = false;

    /**
     * 模型名称
     */
    @JsonProperty("model")
    @Builder.Default
    private String model = "gpt-3.5-turbo";
}
