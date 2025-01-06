/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-27 16:40:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-27 16:42:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.vendors.gitee;

import org.springframework.stereotype.Service;

import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.core.message.MessageProtobuf;

@Service
public class GiteeService {
    
    public void sendWsMessage(String query, RobotLlm robotLlm, MessageProtobuf messageProtobuf) {
        // String prompt = robotLlm.getPrompt() + "\n" + query;
    }
}
