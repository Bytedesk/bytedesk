/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-27 16:41:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-27 16:45:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.coze;

import org.springframework.stereotype.Service;

import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.core.message.MessageProtobuf;

@Service
public class CozeService {
    
    public void sendWsMessage(String query, RobotLlm robotllm, MessageProtobuf messageProtobuf) {
        //
        // String textPrompt = robotllm.getPrompt() + "\n" + query;
        // TextPrompt prompt = new TextPrompt(textPrompt);
        // CozeLlm llm;
        // CozeChatOptions options;
        // // 
        // llm.chatStream(prompt, new StreamResponseListener<AiMessageResponse>() {
        //     @Override
        //     public void onMessage(ChatContext context, AiMessageResponse response) {
        //         AiMessage message = response.getMessage();
        //         System.out.print(message.getContent());
        //     }

        //     @Override
        //     public void onStart(ChatContext context) {
        //         StreamResponseListener.super.onStart(context);
        //     }

        //     @Override
        //     public void onStop(ChatContext context) {
        //         // stop 后才能拿到 token 用量等信息
        //         CozeChatContext ccc = (CozeChatContext) context;
        //         System.out.println(ccc.getUsage());
        //         StreamResponseListener.super.onStop(context);
        //     }
        // }, options);
    }
    
}
