/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-31 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.config;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.model.Generation;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 备用ChatModel实现
 * 当没有其他ChatModel可用时使用此实现
 */
@Slf4j
public class FallbackChatModel implements ChatModel {

    @Override
    public ChatResponse call(Prompt prompt) {
        log.warn("FallbackChatModel is being used - please configure a proper ChatModel implementation");
        
        // 创建一个简单的响应
        String responseText = "AI服务暂时不可用，请稍后再试。请确保已正确配置ChatModel实现（如Ollama、OpenAI等）。";
        
        AssistantMessage assistantMessage = new AssistantMessage(responseText);
        Generation generation = new Generation(assistantMessage);
        return new ChatResponse(List.of(generation));
    }

    @Override
    public String call(String message) {
        return call(new Prompt(new UserMessage(message))).getResult().getOutput().getText();
    }
}
