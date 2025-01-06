/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-19 21:17:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-27 16:47:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.vendors.moonshot;

import org.springframework.stereotype.Service;

import com.agentsflex.core.llm.ChatOptions;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.llm.moonshot.MoonshotLlm;
import com.agentsflex.llm.moonshot.MoonshotLlmConfig;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.core.message.MessageProtobuf;

@Service
public class MoonshotService {

    public void sendWsMessage(String query, RobotLlm robotLlm, MessageProtobuf messageProtobuf) {
        //
        String prompt = robotLlm.getPrompt() + "\n" + query;
        // 
        MoonshotLlmConfig config = new MoonshotLlmConfig();
        ChatOptions chatOptions = new ChatOptions();
        chatOptions.setTemperature(0.3f);
        chatOptions.setMaxTokens(4096);
        config.setApiKey("sk-*****");
        config.setModel("moonshot-v1-8k");
        Llm llm = new MoonshotLlm(config);
        // String res = llm.chat("你叫什么名字",chatOptions);
        // System.out.println(res);
        llm.chatStream(prompt, (context, response) -> {
            AiMessage message = response.getMessage();
            System.out.println(message);
        },chatOptions);
    }
    
}
