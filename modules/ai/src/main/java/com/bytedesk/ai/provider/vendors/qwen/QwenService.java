/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-27 16:30:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-11 10:48:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.vendors.qwen;

import org.springframework.stereotype.Service;

import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.llm.qwen.QwenLlm;
import com.agentsflex.llm.qwen.QwenLlmConfig;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.core.message.MessageProtobuf;

@Service
public class QwenService {
    
    public void sendWsMessage(String query, RobotLlm robotLlm, MessageProtobuf messageProtobuf) {
        //
        String prompt = robotLlm.getPrompt() + "\n" + query;
        // 
        QwenLlmConfig config = new QwenLlmConfig();
        config.setApiKey("sk-28a6be3236****");
        config.setModel("qwen-turbo");

        Llm llm = new QwenLlm(config);
        llm.chatStream(prompt, (context, response) -> {
            AiMessage message = response.getMessage();
            System.out.println(">>>> " + message.getContent());
        });
    }

}
