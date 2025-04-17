/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-14 07:07:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 11:33:43
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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RobotMessageRequest extends BaseRequest {
    
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
}
