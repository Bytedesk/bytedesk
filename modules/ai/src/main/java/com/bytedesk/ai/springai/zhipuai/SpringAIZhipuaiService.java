/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 16:58:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-06 17:40:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.zhipuai;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import java.util.Optional;

import com.bytedesk.ai.robot.RobotConsts;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.springai.SpringAIVectorService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bytedesk.ai.springai.BaseSpringAIService;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.zhipuai.chat.enabled", havingValue = "true")
public class SpringAIZhipuaiService extends BaseSpringAIService {

    private final ZhiPuAiChatModel bytedeskZhipuaiChatModel;

    public SpringAIZhipuaiService(
            @Qualifier("bytedeskZhipuaiChatModel") ZhiPuAiChatModel bytedeskZhipuaiChatModel,
            Optional<SpringAIVectorService> springAIVectorService,
            IMessageSendService messageSendService) {
        super(springAIVectorService, messageSendService);
        this.bytedeskZhipuaiChatModel = bytedeskZhipuaiChatModel;
    }

    @Override
    protected void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf) {
        bytedeskZhipuaiChatModel.stream(prompt).subscribe(
            response -> {
                if (response != null) {
                    log.info("Zhipuai API response metadata: {}", response.getMetadata());
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
                log.error("Zhipuai API error: ", error);
                messageProtobuf.setType(MessageTypeEnum.ERROR);
                messageProtobuf.setContent("服务暂时不可用，请稍后重试");
                messageSendService.sendProtobufMessage(messageProtobuf);
            },
            () -> log.info("Chat stream completed")
        );
    }

    @Override
    protected String generateFaqPairs(String prompt) {
        return bytedeskZhipuaiChatModel.call(prompt);
    }

    public void sendWsKbMessage(String query, RobotEntity robot, MessageProtobuf messageProtobuf) {
        Assert.hasText(query, "Query must not be empty");
        Assert.notNull(robot, "RobotEntity must not be null");
        Assert.notNull(messageProtobuf, "MessageProtobuf must not be null");
        Assert.isTrue(springAIVectorService.isPresent(), "SpringAIVectorService must not be null");
        //
        List<String> contentList = springAIVectorService.get().searchText(query, robot.getKbUid());
        String context = String.join("\n", contentList);
        String history = "";
        // prompt = PROMPT_TEMPLATE.replace("{context}", context)
        // .replace("{query}", query)
        // .replace("{history}", history);
        String prompt = robot.getLlm().getPrompt() + "\n" +
                "用户查询: " + query + "\n" +
                "历史聊天记录: " + history + "\n" +
                "搜索结果: " + context;

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(robot.getLlm().getPrompt()));
        messages.add(new UserMessage(prompt));

        Prompt aiPrompt = new Prompt(messages);

        processPrompt(aiPrompt, messageProtobuf);
    }

    public void sendWsMessage(String query, RobotLlm robotLlm, MessageProtobuf messageProtobuf) {
        Assert.hasText(query, "Query must not be empty");
        Assert.notNull(robotLlm, "RobotLlm must not be null");
        Assert.notNull(messageProtobuf, "MessageProtobuf must not be null");
        //
        String prompt = robotLlm.getPrompt() + "\n" + query;
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(robotLlm.getPrompt()));
        messages.add(new UserMessage(prompt));

        Prompt aiPrompt = new Prompt(messages);

        processPrompt(aiPrompt, messageProtobuf);
    }

    public void sendWsKbAutoReply(String query, String kbUid, MessageProtobuf messageProtobuf) {
        Assert.hasText(query, "Query must not be empty");
        Assert.hasText(kbUid, "Knowledge base UID must not be empty");
        Assert.notNull(messageProtobuf, "MessageProtobuf must not be null");
        Assert.isTrue(springAIVectorService.isPresent(), "SpringAIVectorService must not be null");
        //
        List<String> contentList = springAIVectorService.get().searchText(query, kbUid);
        String context = String.join("\n", contentList);
        String prompt = RobotConsts.PROMPT_BLUEPRINT.replace("{context}", context).replace("{query}", query);
        log.info("sendWsAutoReply prompt {}", prompt);

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(prompt));
        messages.add(new UserMessage(prompt));

        Prompt aiPrompt = new Prompt(messages);

        processPrompt(aiPrompt, messageProtobuf);
    }

    public String generateFaqPairsAsync(String chunk) {
        if (!StringUtils.hasText(chunk)) {
            return "";
        }

        String prompt = RobotConsts.PROMPT_LLM_GENERATE_FAQ_TEMPLATE.replace("{chunk}", chunk);
        return generateFaqPairs(prompt);
    }

    public void generateFaqPairsSync(String chunk) {
        if (!StringUtils.hasText(chunk)) {
            return;
        }

        String prompt = RobotConsts.PROMPT_LLM_GENERATE_FAQ_TEMPLATE.replace("{chunk}", chunk);

        int maxRetries = 3;
        int retryCount = 0;
        int retryDelay = 1000;

        while (retryCount < maxRetries) {
            try {
                String result = generateFaqPairs(prompt);
                log.info("FAQ generation result: {}", result);
                return;
            } catch (Exception e) {
                retryCount++;
                if (retryCount == maxRetries) {
                    log.error("Failed to generate FAQ pairs after {} retries", maxRetries, e);
                    throw new RuntimeException("Failed to generate FAQ pairs", e);
                }

                try {
                    Thread.sleep(retryDelay * (1 << (retryCount - 1)));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while retrying", ie);
                }
            }
        }
    }

}
