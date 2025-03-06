/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 17:56:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-06 16:14:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.dashscope;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.bytedesk.ai.springai.BaseSpringAIService;
import com.bytedesk.ai.springai.SpringAIVectorService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.dashscope.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIDashscopeService extends BaseSpringAIService {

    private final Optional<DashScopeChatModel> bytedeskDashScopeChatModel;

    public SpringAIDashscopeService(
            @Qualifier("bytedeskDashScopeChatModel") Optional<DashScopeChatModel> bytedeskDashScopeChatModel,
            Optional<SpringAIVectorService> springAIVectorService,
            IMessageSendService messageSendService) {
        super(springAIVectorService, messageSendService);
        this.bytedeskDashScopeChatModel = bytedeskDashScopeChatModel;
    }

    @Override
    protected void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf) {
        bytedeskDashScopeChatModel.ifPresent(model -> model.stream(prompt).subscribe(
            response -> {
                if (response != null) {
                    log.info("DashScope API response metadata: {}", response.getMetadata());
                    List<Generation> generations = response.getResults();
                    for (Generation generation : generations) {
                        AssistantMessage assistantMessage = generation.getOutput();
                        String textContent = assistantMessage.getText();

                        messageProtobuf.setType(MessageTypeEnum.STREAM);
                        messageProtobuf.setContent(textContent);
                        messageSendService.sendProtobufMessage(messageProtobuf);
                    }
                }
            },
            error -> {
                log.error("DashScope API error: ", error);
                messageProtobuf.setType(MessageTypeEnum.ERROR);
                messageProtobuf.setContent("服务暂时不可用，请稍后重试");
                messageSendService.sendProtobufMessage(messageProtobuf);
            },
            () -> log.info("Chat stream completed")
        ));
    }

    @Override
    protected String generateFaqPairs(String prompt) {
        return bytedeskDashScopeChatModel.map(model -> model.call(prompt)).orElse("");
    }
}
