/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 14:34:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dify;

import com.bytedesk.core.base.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class DifyRequest extends BaseRequest {

    /**
     * 用户输入内容
     */
    private String query;

    /**
     * 输入变量，key-value对象，提供给 Prompt 模板使用
     */
    private Map<String, Object> inputs;

    /**
     * 响应模式：
     * - streaming 流式响应（推荐）
     * - blocking 阻塞式响应
     */
    private String responseMode;

    /**
     * 用户标识，用于定义终端用户的身份，必须与用户的真实 ID 保持一致
     */
    private String user;

    /**
     * 对话 ID，需要基于之前的对话继续对话时传入
     */
    private String conversationId;

    /**
     * 是否自动生成标题
     */
    private Boolean autoGenerateTitle;

    /**
     * 对话的新标题
     */
    private String name;

    /**
     * 分页查询时的第一条消息ID
     */
    private String firstId;

    /**
     * 分页查询时的最后一个对话ID
     */
    private String lastId;

    /**
     * 查询限制数量
     */
    private Integer limit;

    /**
     * 是否只返回置顶对话
     */
    private Boolean pinned;

    // 保留原有字段
    private String description;

    private String kbUid; // 对应知识库

    // 
    private Boolean enabled;

    private String apiUrl;

    private String apiKey;

}
