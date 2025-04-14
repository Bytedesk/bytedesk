/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-14 07:07:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 10:14:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message;

import com.bytedesk.core.base.BaseResponse;

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
public class RobotMessageResponse extends BaseResponse {
    
    private String topic;

    private String threadUid;
    
    // 使用content作为question
    // answer
    private String answer;

    // 使用user作为提问者，robot回答者
    private String robot;

    /**
     * @{org.springframework.ai.chat.metadata.Usage}
     */
    private Integer promptTokens;

    private Integer completionTokens;

    private Integer totalTokens;
}
