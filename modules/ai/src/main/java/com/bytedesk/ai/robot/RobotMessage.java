/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 12:41:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-02 13:08:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 转化为json存储到message表中content字段
 * @{com.bytedesk.core.message.Message}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RobotMessage {
    // 
    private String question;
    
    private String answer;

    public void clearAnswer() {
        this.answer = "";
    }

    public void appendAnswer(String answer) {
        this.answer += answer;
    }
    
    /**
     * 参考：
     * @{com.zhipu.oapi.service.v4.model.Usage}
     */
    @Builder.Default
    private Integer promptTokens = 0;

    @Builder.Default
    private Integer completionTokens = 0;

    @Builder.Default
    private Integer totalTokens = 0;

}
