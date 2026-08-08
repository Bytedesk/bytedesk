/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-23 10:22:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 10:30:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.observability;

import java.util.List;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.observation.ChatClientObservationContext;
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.observation.conventions.AiOperationType;
import org.springframework.ai.observation.conventions.AiProvider;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Bytedesk 自定义 ChatClient 观测约定.
 *
 * <p>阶段 7A 调整：不再硬编码 {@code "ollama"} 模型名，而是从 {@link ChatClientRequest#prompt()}
 * 中动态解析 {@link ChatOptions#getModel()}；消息数也从 {@code prompt().getInstructions()}
 * 中真实统计，避免观测指标失真。</p>
 *
 * <p>同时采用 OpenTelemetry Gen AI 语义约定的标准键名（{@code gen_ai.*} / {@code spring.ai.*}），
 * 便于 Grafana / Zipkin 等后端直接识别：</p>
 * <ul>
 *   <li>低基数：{@code gen_ai.operation.name}、{@code gen_ai.system}、{@code spring.ai.kind}、
 *       {@code spring.ai.chat.client.stream}</li>
 *   <li>高基数：{@code spring.ai.chat.client.conversation.id}、{@code gen_ai.request.model}</li>
 * </ul>
 */
public class CustomChatClientObservationConvention implements ChatClientObservationConvention {

    /** 历史保留：输出格式上下文键，向后兼容旧版本 ChatClientAttributes.OUTPUT_FORMAT. */
    private static final String OUTPUT_FORMAT_KEY = "spring.ai.chat.client.output.format";

    @Override
    public String getName() {
        return "bytedesk.ai.chat.client";
    }

    @Override
    public String getContextualName(ChatClientObservationContext context) {
        return "bytedesk.ai.chat.client." + resolveModel(context);
    }

    @Override
    public KeyValues getLowCardinalityKeyValues(ChatClientObservationContext context) {
        return KeyValues.of(
                KeyValue.of("gen_ai.operation.name", AiOperationType.FRAMEWORK.value()),
                KeyValue.of("gen_ai.system", AiProvider.SPRING_AI.value()),
                KeyValue.of("spring.ai.kind", "chat_client"),
                KeyValue.of("spring.ai.chat.client.stream", String.valueOf(context.isStream())),
                KeyValue.of("bytedesk.ai.chat.client.format", resolveFormat(context)),
                KeyValue.of("bytedesk.ai.chat.client.success", String.valueOf(context.getError() == null))
        );
    }

    @Override
    public KeyValues getHighCardinalityKeyValues(ChatClientObservationContext context) {
        KeyValues keyValues = KeyValues.of(
                KeyValue.of("gen_ai.request.model", resolveModel(context)),
                KeyValue.of("bytedesk.ai.chat.client.message.count", String.valueOf(resolveMessageCount(context))),
                KeyValue.of("bytedesk.ai.chat.client.error.message",
                        context.getError() != null ? String.valueOf(context.getError().getMessage()) : "")
        );
        String conversationId = resolveConversationId(context);
        if (StringUtils.hasText(conversationId)) {
            keyValues = keyValues.and(KeyValue.of("spring.ai.chat.client.conversation.id", conversationId));
        }
        return keyValues;
    }

    /**
     * 从 {@link ChatClientObservationContext#getRequest()} 中安全解析模型名。
     * 若 prompt / options 不存在，回退到 {@code "unknown"}，不再硬编码 {@code "ollama"}。
     */
    private String resolveModel(ChatClientObservationContext context) {
        ChatClientRequest request = context.getRequest();
        if (request == null) {
            return "unknown";
        }
        Prompt prompt = request.prompt();
        if (prompt == null) {
            return "unknown";
        }
        ChatOptions options = prompt.getOptions();
        if (options != null && StringUtils.hasText(options.getModel())) {
            return options.getModel();
        }
        return "unknown";
    }

    /**
     * 从 prompt 的 instructions 中统计消息数。
     * 不再硬编码返回 {@code 1}。
     */
    private int resolveMessageCount(ChatClientObservationContext context) {
        ChatClientRequest request = context.getRequest();
        if (request == null) {
            return 0;
        }
        Prompt prompt = request.prompt();
        if (prompt == null) {
            return 0;
        }
        List<Message> instructions = prompt.getInstructions();
        return CollectionUtils.isEmpty(instructions) ? 0 : instructions.size();
    }

    /**
     * 从请求 context map 中读取历史遗留的输出格式信息，向后兼容。
     */
    private String resolveFormat(ChatClientObservationContext context) {
        ChatClientRequest request = context.getRequest();
        if (request == null || request.context() == null) {
            return "";
        }
        Object format = request.context().get(OUTPUT_FORMAT_KEY);
        return format instanceof String s ? s : "";
    }

    /**
     * 从请求 context map 中读取会话 ID（若 ChatMemory 设置了 conversation id）。
     */
    private String resolveConversationId(ChatClientObservationContext context) {
        ChatClientRequest request = context.getRequest();
        if (request == null || request.context() == null) {
            return null;
        }
        Object conversationId = request.context().get("spring.ai.chat.client.conversation.id");
        return conversationId instanceof String s ? s : null;
    }
}
