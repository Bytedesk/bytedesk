/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-14 07:07:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-02 08:22:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RobotMessageRequest extends BaseRequest {
    
    private static final long serialVersionUID = 1L;

    
    private String topic;

    private String threadUid;

    private String userNickname;

    private String robotNickname;

    private String status;
    
    // 可以在这里添加特有的字段（如果有的话）

    // 使用content作为question
    // 回答内容
    private String answer;

    // 使用user作为提问者
    private String user;

    // robot回答者
    private String robot;

    // 是否未搜索到到答案
    private Boolean isUnAnswered;

    /**
     * @{org.springframework.ai.chat.metadata.Usage}
     */
    private Integer promptTokens;

    private Integer completionTokens;

    private Integer totalTokens;

    // 存储传入到大模型的完整prompt内容
    private String prompt;

    // 大模型提供商（如：openai、zhipuai、baidu等）
    private String aiProvider;

    // 大模型名称（如：gpt-4、glm-4、ernie-bot-4等）
    private String aiModel;
}
