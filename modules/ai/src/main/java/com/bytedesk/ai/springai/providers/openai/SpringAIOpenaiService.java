/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 11:09:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.openai;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.openai.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIOpenaiService extends BaseSpringAIService {

    @Autowired(required = false)
    private Optional<OpenAiChatModel> openaiChatModel;

    public SpringAIOpenaiService() {
        super(); // 调用基类的无参构造函数
    }
    
    /**
     * 根据机器人配置创建动态的OpenAiChatModel
     * 
     * @param llm 机器人LLM配置
     * @return 配置了特定模型的OpenAiChatModel
     */
    private OpenAiChatModel createDynamicChatModel(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getModel()) || !openaiChatModel.isPresent()) {
            // 如果没有指定模型或设置，使用默认配置
            return openaiChatModel.orElse(null);
        }

        try {
            // 我们不能直接访问底层API客户端，因此不能创建新的模型实例
            // 替代方案：直接使用默认模型，但在运行时提供自定义选项
            OpenAiChatModel chatModel = openaiChatModel.get();
            项对象
            // 在使用模型时可以传递自定义选项
            // 修改调用代码以在每次请求中提供定制的选项
            return chatModel;
            
            // 注意：需要在后续调用中通过Prompt设置特定的选项
        } catch (Exception e) {
            log.error("Error handling model customization for model {}", llm.getModel(), e);.builder() 创建一个新的模型实例
            return openaiChatModel.orElse(null);
        }del.builder()
    }

    @Override           .build();
    protected void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf) {   } catch (Exception e) {
        // 从messageProtobuf的extra字段中获取llm配置            log.error("Error creating dynamic chat model for model {}", llm.getModel(), e);
        RobotLlm llm = null;eturn openaiChatModel.orElse(null);
        try {
            // 这里假设extra中有robotLlm字段，实际中可能需要调整
            if (messageProtobuf.getExtra() != null) {
                // 根据实际情况从消息中获取LLM配置
                // 如果无法获取，将使用默认配置, MessageProtobuf messageProtobuf) {
            }
        } catch (Exception e) {
            log.warn("Failed to extract LLM config from message, using default model", e);
        }/ 这里假设extra中有robotLlm字段，实际中可能需要调整
.getExtra() != null) {
        // 获取适当的模型实例
        OpenAiChatModel chatModel = (llm != null) ? createDynamicChatModel(llm) : openaiChatModel.orElse(null);       // 如果无法获取，将使用默认配置
                    }
        if (chatModel == null) {eption e) {
            messageProtobuf.setType(MessageTypeEnum.ERROR);
            messageProtobuf.setContent("OpenAI服务不可用");}
            messageSendService.sendProtobufMessage(messageProtobuf);
            return;
        }eateDynamicChatModel(llm) : openaiChatModel.orElse(null);
        
        chatModel.stream(prompt).subscribe(el == null) {
                response -> {   messageProtobuf.setType(MessageTypeEnum.ERROR);
                    if (response != null) {    messageProtobuf.setContent("OpenAI服务不可用");
                        log.info("Openai API response metadata: {}", response.getMetadata());Message(messageProtobuf);
                        List<Generation> generations = response.getResults();
                        for (Generation generation : generations) {
                            AssistantMessage assistantMessage = generation.getOutput();
                            String textContent = assistantMessage.getText();

                            messageProtobuf.setType(MessageTypeEnum.STREAM);
                            messageProtobuf.setContent(textContent);e.getMetadata());
                            messageSendService.sendProtobufMessage(messageProtobuf);                        List<Generation> generations = response.getResults();
                        }
                    }ration.getOutput();
                },
                error -> {
                    log.error("Openai API error: ", error);       messageProtobuf.setType(MessageTypeEnum.STREAM);
                    messageProtobuf.setType(MessageTypeEnum.ERROR);          messageProtobuf.setContent(textContent);
                    messageProtobuf.setContent("服务暂时不可用，请稍后重试");  messageSendService.sendProtobufMessage(messageProtobuf);
                    messageSendService.sendProtobufMessage(messageProtobuf);
                },
                () -> {
                    log.info("Chat stream completed");
                    // 发送流结束标记  log.error("Openai API error: ", error);
                    // messageProtobuf.setType(MessageTypeEnum.STREAM_END);sageProtobuf.setType(MessageTypeEnum.ERROR);
                    // messageProtobuf.setContent(""); // 或者可以是任何结束标记用，请稍后重试");
                    // messageSendService.sendProtobufMessage(messageProtobuf);dService.sendProtobufMessage(messageProtobuf);
                });
    }

    @Override // 发送流结束标记
    protected String generateFaqPairs(String prompt) {               // messageProtobuf.setType(MessageTypeEnum.STREAM_END);
        return openaiChatModel.map(model -> model.call(prompt)).orElse("");                    // messageProtobuf.setContent(""); // 或者可以是任何结束标记
    }       // messageSendService.sendProtobufMessage(messageProtobuf);

    @Override
    protected String processPromptSync(String message) {
        try {    @Override
            return openaiChatModel.map(model -> model.call(message)) String generateFaqPairs(String prompt) {
                    .orElse("Openai service is not available");rompt)).orElse("");
        } catch (Exception e) {
            log.error("Openai API sync error: ", e);
            return "服务暂时不可用，请稍后重试";
        }mptSync(String message) {
    }
map(model -> model.call(message))
    @Override           .orElse("Openai service is not available");
    protected void processPromptSSE(Prompt prompt, MessageProtobuf messageProtobufQuery,  MessageProtobuf messageProtobufReply, SseEmitter emitter) {   } catch (Exception e) {
        // 从messageProtobuf中提取RobotLlm信息            log.error("Openai API sync error: ", e);
        RobotLlm llm = null;eturn "服务暂时不可用，请稍后重试";
        try {
            // 此处根据实际应用逻辑从消息中获取机器人配置
            // 例如可以从messageProtobufQuery的extra字段中获取机器人配置
            if (messageProtobufQuery.getExtra() != null) {
                // 从extra中解析RobotLlm配置Prompt prompt, MessageProtobuf messageProtobufQuery,  MessageProtobuf messageProtobufReply, SseEmitter emitter) {
                // 此处实现需根据实际应用逻辑调整
            }
        } catch (Exception e) {
            log.warn("Failed to extract robot info, using default model", e);配置
        }/ 例如可以从messageProtobufQuery的extra字段中获取机器人配置
Query.getExtra() != null) {
        // 获取适当的模型实例
        OpenAiChatModel chatModel = (llm != null) ? createDynamicChatModel(llm) : openaiChatModel.orElse(null);       // 此处实现需根据实际应用逻辑调整
                    }
        if (chatModel == null) {eption e) {
            handleSseError(new RuntimeException("OpenAI service not available"), messageProtobufQuery, messageProtobufReply, emitter);
            return;}
        }
        
        chatModel.stream(prompt).subscribe(odel chatModel = (llm != null) ? createDynamicChatModel(llm) : openaiChatModel.orElse(null);
                response -> {
                    try {if (chatModel == null) {
                        if (response != null) {tion("OpenAI service not available"), messageProtobufQuery, messageProtobufReply, emitter);
                            List<Generation> generations = response.getResults();
                            for (Generation generation : generations) {
                                AssistantMessage assistantMessage = generation.getOutput();
                                String textContent = assistantMessage.getText();
                                log.info("Openai API response metadata: {}, text {}",
                                        response.getMetadata(), textContent);
                                //
                                // StringUtils.hasLength() 检查字符串非 null 且长度大于 0，允许包含空格
                                if (StringUtils.hasLength(textContent)) {
                                    messageProtobufReply.setContent(textContent);sistantMessage assistantMessage = generation.getOutput();
                                    messageProtobufReply.setType(MessageTypeEnum.STREAM);
                                    // 保存消息到数据库}, text {}",
                                    persistMessage(messageProtobufQuery, messageProtobufReply);
                                    String messageJson = messageProtobufReply.toJson();
                                    // 发送SSE事件hasLength() 检查字符串非 null 且长度大于 0，允许包含空格
                                    emitter.send(SseEmitter.event()
                                            .data(messageJson)
                                            .id(messageProtobufReply.getUid())tobufReply.setType(MessageTypeEnum.STREAM);
                                            .name("message"));
                                }obufQuery, messageProtobufReply);
                            }toJson();
                        }
                    } catch (Exception e) {   emitter.send(SseEmitter.event()
                        log.error("Error sending SSE event", e);               .data(messageJson)
                        handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);                   .id(messageProtobufReply.getUid())
                    } .name("message"));
                },
                error -> {
                    log.error("Openai API SSE error: ", error);   }
                    handleSseError(error, messageProtobufQuery, messageProtobufReply, emitter);  } catch (Exception e) {
                },g.error("Error sending SSE event", e);
                () -> { messageProtobufReply, emitter);
                    log.info("Openai API SSE complete");
                    try {
                        // 发送流结束标记> {
                        messageProtobufReply.setType(MessageTypeEnum.STREAM_END);error);
                        messageProtobufReply.setContent(""); // 或者可以是任何结束标记eSseError(error, messageProtobufQuery, messageProtobufReply, emitter);
                        // 保存消息到数据库
                        persistMessage(messageProtobufQuery, messageProtobufReply);
                        String messageJson = messageProtobufReply.toJson();
                        // 发送SSE事件
                        emitter.send(SseEmitter.event()
                                .data(messageJson)_END);
                                .id(messageProtobufReply.getUid())tobufReply.setContent(""); // 或者可以是任何结束标记
                                .name("message"));
                        emitter.complete();obufQuery, messageProtobufReply);
                    } catch (Exception e) {toJson();
                        log.error("Error completing SSE", e);
                    }tter.event()
                });geJson)
    }id())
               .name("message"));
    // 添加新的辅助方法处理SSE错误     emitter.complete();
    private void handleSseError(Throwable error, MessageProtobuf messageProtobufQuery,                } catch (Exception e) {
                               MessageProtobuf messageProtobufReply, SseEmitter emitter) {                    log.error("Error completing SSE", e);
        try {
            messageProtobufReply.setType(MessageTypeEnum.ERROR);
            messageProtobufReply.setContent("服务暂时不可用，请稍后重试");
            // 保存消息到数据库
            persistMessage(messageProtobufQuery, messageProtobufReply);
            String messageJson = messageProtobufReply.toJson();buf messageProtobufQuery, 
                    MessageProtobuf messageProtobufReply, SseEmitter emitter) {
            emitter.send(SseEmitter.event()
                    .data(messageJson);
                    .id(messageProtobufReply.getUid())messageProtobufReply.setContent("服务暂时不可用，请稍后重试");
                    .name("message"));
            emitter.complete();obufQuery, messageProtobufReply);
        } catch (Exception e) {toJson();
            log.error("Error handling SSE error", e);
            try {tter.event()
                emitter.completeWithError(e);geJson)
            } catch (Exception ex) {)
                log.error("Failed to complete emitter with error", ex);   .name("message"));
            }
        }
    }
ry {
    public Optional<OpenAiChatModel> getOpenaiChatModel() {       emitter.completeWithError(e);
        return openaiChatModel;       } catch (Exception ex) {
    }                log.error("Failed to complete emitter with error", ex);
    
    public boolean isServiceHealthy() {
        if (!openaiChatModel.isPresent()) {
            return false;
        }tOpenaiChatModel() {

        try {
            // 发送一个简单的测试请求来检测服务是否响应
            String response = processPromptSync("test");    public boolean isServiceHealthy() {
            return !response.contains("不可用") && !response.equals("Openai service is not available");openaiChatModel.isPresent()) {
        } catch (Exception e) {
            log.error("Error checking OpenAI service health", e);
            return false;
        }
    }
}se = processPromptSync("test");
        }
    }
}
