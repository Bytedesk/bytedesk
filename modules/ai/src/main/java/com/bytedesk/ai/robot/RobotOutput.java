/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-23 14:46:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 14:50:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于AI生成问答对格式化输出
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RobotOutput {
    
    private List<String> chunks;

    private List<QA> qaList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private class QA {
        private String question;
        private String answer;
    }
}
