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
 * MaxKB 带系统角色的消息请求类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaxkbSystemRoleRequest {

    /**
     * 系统角色描述
     */
    @JsonProperty("system_role")
    @Builder.Default
    private String systemRole = "你是杭州飞致云信息科技有限公司旗下产品 MaxKB 知识库问答系统的智能小助手，你的工作是帮助 MaxKB 用户解答使用中遇到的问题，用户找你回答问题时，你要把主题放在 MaxKB 知识库问答系统身上。";

    /**
     * 用户消息内容
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
