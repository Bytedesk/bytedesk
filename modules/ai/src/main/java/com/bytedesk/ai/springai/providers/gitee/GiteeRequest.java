/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-02 23:15:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-02 23:15:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.gitee;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class GiteeRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * Gitee AI API 基础URL
     */
    private String apiUrl;

    /**
     * Gitee AI API Key
     */
    private String apiKey;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 用户消息/提示
     */
    private String message;

    /**
     * 温度参数 (0.0-2.0)
     */
    private Double temperature;

    /**
     * Top-p 参数 (0.0-1.0)
     */
    private Double topP;

    /**
     * 最大tokens数量
     */
    private Integer maxTokens;

    /**
     * 流式输出
     */
    private Boolean stream;
}
