/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 16:59:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 15:45:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.ollama;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotTypeEnum;
import com.bytedesk.ai.springai.SpringAIVectorService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true")
public class SpringAIOllamaService {
    
    @Qualifier("bytedeskOllamaChatModel")
    private final Optional<OllamaChatModel> bytedeskOllamaChatModel;
    private final SpringAIVectorService springAIVectorService;
    private final IMessageSendService messageSendService;

    private final String PROMPT_TEMPLATE = """
              任务描述：根据用户的查询和文档信息回答问题，并结合历史聊天记录生成简要的回答。

              用户查询: {query}

              历史聊天记录: {history}

              搜索结果: {context}

              请根据以上信息生成一个简单明了的回答，确保信息准确且易于理解。
              当用户提出的问题无法根据文档内容进行回复或者你也不清楚时，回复:未查找到相关问题答案.
              另外，请提供更多相关的问答对。
              回答内容请以JSON格式输出，格式如下：
              {
                "answer": "回答内容",
                "additional_qa_pairs": [
                    {"question": "相关问题1", "answer": "相关答案1"},
                    {"question": "相关问题2", "answer": "相关答案2"}
                ]
              }
            """;

    private final String PROMPT_QA_TEMPLATE = """
            基于以下给定的文本，生成一组高质量的问答对。请遵循以下指南:

            1. 问题部分：
            - 为同一个主题创建多个不同表述的问题，确保问题的多样性
            - 每个问题应考虑用户可能的多种问法，例如：
              - 直接询问（如"什么是...？"）
              - 请求确认（如"是否可以说...？"）
              - 如何做（如"如何实现...？"）
              - 为什么（如"为什么需要...？"）
              - 比较类（如"...和...有什么区别？"）

            2. 答案部分：
            - 答案应该准确、完整且易于理解
            - 使用简洁清晰的语言
            - 适当添加示例说明
            - 可以包含关键步骤或要点
            - 必要时提供相关概念解释

            3. 格式要求：
            - 以JSON数组形式输出
            - 每个问答对包含question和answer字段
            - 可选添加type字段标识问题类型
            - 可选添加tags字段标识相关标签

            4. 质量控制：
            - 确保问答对之间不重复
            - 问题应该有实际意义和价值
            - 答案需要准确且有帮助
            - 覆盖文本中的重要信息点

            给定文本：
            {chunk}

            请基于这个文本生成问答对
            """;

    public void sendWsKbMessage(String query, RobotEntity robot, MessageProtobuf messageProtobuf) {
        String prompt;
        if (robot.getType().equals(RobotTypeEnum.SERVICE.name())) {
            List<String> contentList = springAIVectorService.searchText(query, robot.getKbUid());
            String context = String.join("\n", contentList);
            String history = "";
            prompt = PROMPT_TEMPLATE.replace("{context}", context)
                    .replace("{query}", query)
                    .replace("{history}", history);
        } else {
            prompt = robot.getLlm().getPrompt() + "\n" + query;
        }

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(robot.getLlm().getPrompt()));
        messages.add(new UserMessage(prompt));

        Prompt aiPrompt = new Prompt(messages);

        bytedeskOllamaChatModel.ifPresent(model -> model.stream(aiPrompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("Ollama API response metadata: {}", response.getMetadata());
                        // generations
                        List<Generation> generations = response.getResults();
                        for (Generation generation : generations) {
                            AssistantMessage assistantMessage = generation.getOutput();
                            String textContent = assistantMessage.getText();

                            log.info("Ollama API response assistantMessage: {}, textContent: {}", assistantMessage,
                                    textContent);
                            ChatGenerationMetadata metadata = generation.getMetadata();

                            // finishReason: STOP
                            log.info("Ollama API response metadata {}, finishReason: {}", metadata,
                                    metadata.getFinishReason());

                            messageProtobuf.setType(MessageTypeEnum.STREAM);
                            messageProtobuf.setContent(textContent);
                            messageSendService.sendProtobufMessage(messageProtobuf);

                            // if (metadata.getFinishReason().equals(FinishReason.STOP)) {
                            // messageProtobuf.setType(MessageTypeEnum.SUCCESS);
                            // messageSendService.sendProtobufMessage(messageProtobuf);
                            // }
                        }
                    }
                },
                error -> {
                    log.error("Ollama API error: ", error);
                    messageProtobuf.setType(MessageTypeEnum.ERROR);
                    messageProtobuf.setContent("服务暂时不可用，请稍后重试");
                    messageSendService.sendProtobufMessage(messageProtobuf);
                },  
                () -> log.info("Chat stream completed")));
    }

    // TODO：历史聊天记录
    public void sendWsMessage(String query, RobotLlm robotLlm, MessageProtobuf messageProtobuf) {

        String prompt = robotLlm.getPrompt() + "\n" + query;
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(robotLlm.getPrompt()));
        messages.add(new UserMessage(prompt));

        Prompt aiPrompt = new Prompt(messages);

        bytedeskOllamaChatModel.ifPresent(model -> model.stream(aiPrompt).subscribe(
                response -> {
                    if (response != null) {
                        log.info("Ollama API response metadata: {}", response.getMetadata());
                        // generations
                        List<Generation> generations = response.getResults();
                        for (Generation generation : generations) {
                            AssistantMessage assistantMessage = generation.getOutput();
                            String textContent = assistantMessage.getText();

                            log.info("Ollama API response assistantMessage: {}, textContent: {}", assistantMessage,
                                    textContent);
                            ChatGenerationMetadata metadata = generation.getMetadata();

                            // finishReason: STOP
                            log.info("Ollama API response metadata {}, finishReason: {}", metadata,
                                    metadata.getFinishReason());

                            messageProtobuf.setType(MessageTypeEnum.STREAM);
                            messageProtobuf.setContent(textContent);
                            messageSendService.sendProtobufMessage(messageProtobuf);

                            // if (metadata.getFinishReason().equals(FinishReason.STOP)) {
                            // messageProtobuf.setType(MessageTypeEnum.SUCCESS);
                            // messageSendService.sendProtobufMessage(messageProtobuf);
                            // }
                        }
                    }
                },
                error -> {
                    log.error("Ollama API error: ", error);
                    messageProtobuf.setType(MessageTypeEnum.ERROR);
                    messageProtobuf.setContent("服务暂时不可用，请稍后重试");
                    messageSendService.sendProtobufMessage(messageProtobuf);
                },  
                () -> log.info("Chat stream completed")));
    }

    public String generateFaqPairsAsync(String chunk) {
        if (!StringUtils.hasText(chunk)) {
            return "";
        }

        String prompt = PROMPT_QA_TEMPLATE.replace("{chunk}", chunk);
        if (bytedeskOllamaChatModel.isPresent()) {
            return bytedeskOllamaChatModel.get().call(prompt);
        }
        return "";
    }

    public void generateFaqPairsSync(String chunk) {
        if (!StringUtils.hasText(chunk)) {
            return;
        }

        String prompt = PROMPT_QA_TEMPLATE.replace("{chunk}", chunk);

        int maxRetries = 3;
        int retryCount = 0;
        int retryDelay = 1000;

        while (retryCount < maxRetries) {
            try {
                String result = bytedeskOllamaChatModel.get().call(prompt);
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
