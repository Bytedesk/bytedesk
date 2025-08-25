/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-25 13:59:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.ragflow;

import com.bytedesk.core.base.BaseRequest;
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
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class RagflowRequest extends BaseRequest {

    /**
     * RAGFlow 聊天ID
     */
    private String chatId;

    /**
     * RAGFlow 代理ID
     */
    private String agentId;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 消息列表
     */
    private List<Map<String, Object>> messages;

    /**
     * 是否流式响应
     */
    private Boolean stream;

    /**
     * 消息内容（简单消息）
     */
    private String messageContent;

    /**
     * 消息角色（user, assistant, system）
     */
    private String role;

    // 保留原有字段
    private String name;
    private String description;

    private String kbUid; // 对应知识库
    
    // 
    private Boolean enabled;

    private String apiUrl;

    private String apiKey;

}
