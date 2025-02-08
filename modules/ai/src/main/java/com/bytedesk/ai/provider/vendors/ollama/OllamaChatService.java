/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-27 14:58:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-08 21:27:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.vendors.ollama;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
import com.agentsflex.llm.ollama.OllamaLlm;
import com.agentsflex.llm.ollama.OllamaLlmConfig;
import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.provider.LlmProviderEntity;
import com.bytedesk.ai.provider.LlmProviderRestService;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotTypeEnum;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.kbase.upload.UploadVectorStore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OllamaChatService {

    @Value("${spring.ai.ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.chat.options.model}")
    private String ollamaDefaultModel;

    @Autowired
    private IMessageSendService messageSendService;

    @Autowired
    private LlmProviderRestService llmProviderRestService;

    @Autowired
    private UploadVectorStore uploadVectorStore;

    // private final String PROMPT_BLUEPRINT = """
    //         根据提供的文档信息回答问题，文档信息如下:
    //         {context}
    //         问题:
    //         {query}
    //         当用户提出的问题无法根据文档内容进行回复或者你也不清楚时，回复:未查找到相关问题答案.
    //         """;

    // RAG智能客服提示模板
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

    // private final String PROMPT_QA_TEMPLATE = """
    //         基于以下给定的文本，生成一组高质量的问答对。请遵循以下指南:

    //         1. 问题部分：
    //         - 为同一个主题创建多个不同表述的问题，确保问题的多样性
    //         - 每个问题应考虑用户可能的多种问法，例如：
    //           - 直接询问（如"什么是...？"）
    //           - 请求确认（如"是否可以说...？"）
    //           - 如何做（如"如何实现...？"）
    //           - 为什么（如"为什么需要...？"）
    //           - 比较类（如"...和...有什么区别？"）

    //         2. 答案部分：
    //         - 答案应该准确、完整且易于理解
    //         - 使用简洁清晰的语言
    //         - 适当添加示例说明
    //         - 可以包含关键步骤或要点
    //         - 必要时提供相关概念解释

    //         3. 格式要求：
    //         - 以JSON数组形式输出
    //         - 每个问答对包含question和answer字段
    //         - 可选添加type字段标识问题类型
    //         - 可选添加tags字段标识相关标签

    //         4. 质量控制：
    //         - 确保问答对之间不重复
    //         - 问题应该有实际意义和价值
    //         - 答案需要准确且有帮助
    //         - 覆盖文本中的重要信息点

    //         5. 示例格式：
    //         {
    //           "qaPairs": [
    //             {
    //               "id": "1",
    //               "question": "问题描述",
    //               "answer": "答案内容",
    //               "tags": ["标签1", "标签2"]
    //             }
    //           ]
    //         }

    //         给定文本：
    //         {chunk}

    //         请基于这个文本生成问答对
    //         """;

    public void sendSseMessage(String query, RobotLlm robotLlm, MessageProtobuf messageProtobuf) {
        //
        String prompt = robotLlm.getPrompt() + "\n" + query;
        // 
        OllamaLlmConfig config = new OllamaLlmConfig();
        config.setEndpoint(ollamaBaseUrl);
        config.setModel(ollamaDefaultModel);
        config.setDebug(true);

        Llm llm = new OllamaLlm(config);
        llm.chatStream(prompt, (context, response) -> {
            // {"content":"有一","fullContent":"有一","messageContent":"有一","status":"MIDDLE"}
            // {"completionTokens":11,"content":"","fullContent":"有一只深海鱼，每天都自由地游来游去，但它却一点也不开心。\n为什么呢？\n因为它压力很大。","messageContent":"有一只深海鱼，每天都自由地游来游去，但它却一点也不开心。\n为什么呢？\n因为它压力很大。","status":"END","totalTokens":27}
            log.info(JSON.toJSONString(response.getMessage()));
            // AiMessage aiMessage = response.getMessage();
        });
    }

    public void sendWsMessage(String query, RobotLlm robotLlm, MessageProtobuf messageProtobuf) {

        String providerName = robotLlm.getProvider();
        Optional<LlmProviderEntity> providerOptional = llmProviderRestService.findByName(providerName, LevelEnum.ORGANIZATION.name());
        if (!providerOptional.isPresent()) {
            log.error("ollama provider {} not exist", providerName);
            return;
        }
        //
        String prompt = robotLlm.getPrompt() + "\n" + query;
        // 
        OllamaLlmConfig config = new OllamaLlmConfig();
        config.setEndpoint(providerOptional.get().getApiUrl());
        config.setModel(robotLlm.getModel());
        config.setDebug(true);
        log.info("ollama config: {}", JSON.toJSONString(config));

        Llm llm = new OllamaLlm(config);
        llm.chatStream(prompt, (context, response) -> {
            // {"content":"有一","fullContent":"有一","messageContent":"有一","status":"MIDDLE"}
            // {"completionTokens":11,"content":"","fullContent":"有一只深海鱼，每天都自由地游来游去，但它却一点也不开心。\n为什么呢？\n因为它压力很大。","messageContent":"有一只深海鱼，每天都自由地游来游去，但它却一点也不开心。\n为什么呢？\n因为它压力很大。","status":"END","totalTokens":27}
            log.info("ollama response: {}", JSON.toJSONString(response.getMessage()));
            AiMessage aiMessage = response.getMessage();
            messageProtobuf.setType(MessageTypeEnum.STREAM);
            messageProtobuf.setContent(aiMessage.getContent());
            messageSendService.sendProtobufMessage(messageProtobuf);
            // 
            if (aiMessage.getStatus().equals(MessageStatus.END)) {
                log.info("ollama aiMessage.getStatus() == END");
            }
        });
    }

    public void sendWsKbMessage(String query, RobotEntity robot, MessageProtobuf messageProtobuf) {
        // 
        String providerName = robot.getLlm().getProvider();
        Optional<LlmProviderEntity> providerOptional = llmProviderRestService.findByName(providerName, LevelEnum.ORGANIZATION.name());
        if (!providerOptional.isPresent()) {
            log.error("ollama provider {} not exist", providerName);
            return;
        }
        //
        String prompt = robot.getLlm().getPrompt();
        if (robot.getType().equals(RobotTypeEnum.SERVICE.name())) {
            List<String> contentList = uploadVectorStore.searchText(query, robot.getKbUid());
            String context = String.join("\n", contentList);
            String history = ""; // TODO: 历史对话上下文，此处暂不使用
            prompt = PROMPT_TEMPLATE.replace("{context}", context).replace("{query}", query).replace("{history}",
                    history);
            log.info("ollama sendWsKbMessage prompt 1 {}", prompt);
        } else {
            prompt = prompt + "\n" + query;
            log.info("ollama sendWsKbMessage prompt 2 {}", prompt);
        }
        //
        OllamaLlmConfig config = new OllamaLlmConfig();
        config.setEndpoint(providerOptional.get().getApiUrl());
        config.setModel(robot.getLlm().getModel());
        config.setDebug(true);
        log.info("ollama config: {}", JSON.toJSONString(config));

        Llm llm = new OllamaLlm(config);
        llm.chatStream(prompt, (context, response) -> {
            // {"content":"有一","fullContent":"有一","messageContent":"有一","status":"MIDDLE"}
            // {"completionTokens":11,"content":"","fullContent":"有一只深海鱼，每天都自由地游来游去，但它却一点也不开心。\n为什么呢？\n因为它压力很大。","messageContent":"有一只深海鱼，每天都自由地游来游去，但它却一点也不开心。\n为什么呢？\n因为它压力很大。","status":"END","totalTokens":27}
            log.info("ollama response: {}", JSON.toJSONString(response.getMessage()));
            AiMessage aiMessage = response.getMessage();
            messageProtobuf.setType(MessageTypeEnum.STREAM);
            messageProtobuf.setContent(aiMessage.getContent());
            messageSendService.sendProtobufMessage(messageProtobuf);
            // 
            if (aiMessage.getStatus().equals(MessageStatus.END)) {
                log.info("ollama aiMessage.getStatus() == END");
            }
        });

    
    }
    
}
