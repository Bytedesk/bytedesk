/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-27 16:39:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-11 10:49:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.vendors.openai;

import org.springframework.stereotype.Service;

import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.core.message.MessageProtobuf;

@Service
public class OpenaiService {
    
    public void sendWsMessage(String query, RobotLlm robotLlm, MessageProtobuf messageProtobuf) {
        //
//         String prompt = robotLlm.getPrompt() + "\n" + query;
//         OpenAiLlmConfig config = new OpenAiLlmConfig();
//         config.setApiKey("sk-alQ9N********");
//         // config.setEndpoint("https://api.moonshot.cn");
//         // config.setModel("moonshot-v1-8k");
// //        config.setDebug(true);
//         // 
//         Llm llm = new OpenAiLlm(config);
// //        String response = llm.chat("请问你叫什么名字");
//         llm.chatStream(prompt, new StreamResponseListener<AiMessageResponse>() {
//             @Override
//             public void onMessage(ChatContext context, AiMessageResponse response) {
//                 System.out.println(response.getMessage().getContent());
//             }
//         });
    }
}
