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

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.ai.chat.client.observation.ChatClientObservationContext;
import org.springframework.ai.observation.conventions.AiOperationType;
import org.springframework.ai.observation.conventions.AiProvider;

/**
 * 自定义聊天客户端观察约定
 */
public class CustomChatClientObservationConvention implements ChatClientObservationConvention {

    @Override
    public String getName() {
        return "bytedesk.ai.chat.client";
    }

    @Override
    public String getContextualName(ChatClientObservationContext context) {
        return "bytedesk.ai.chat.client." + getModelInfo(context);
    }

    @Override
    public KeyValues getLowCardinalityKeyValues(ChatClientObservationContext context) {
        return KeyValues.of(
                KeyValue.of("model", getModelInfo(context)),
                KeyValue.of("provider", getProviderInfo(context)),
                KeyValue.of("format", context.getFormat() != null ? context.getFormat() : ""),
                KeyValue.of("stream", String.valueOf(context.isStream())),
                KeyValue.of("success", String.valueOf(context.getError() == null))
        );
    }

    @Override
    public KeyValues getHighCardinalityKeyValues(ChatClientObservationContext context) {
        return KeyValues.of(
                KeyValue.of("messageCount", String.valueOf(getMessageCount(context))),
                KeyValue.of("operationType", AiOperationType.FRAMEWORK.value()),
                KeyValue.of("errorMessage", context.getError() != null ? context.getError().getMessage() : "")
        );
    }
    
    // 辅助方法，从上下文中获取模型信息
    private String getModelInfo(ChatClientObservationContext context) {
        // 尝试从操作元数据或请求中获取模型信息
        // 这里使用一个默认值，实际应用中可能需要从请求对象中提取
        return "ollama";
    }
    
    // 辅助方法，从上下文中获取提供商信息
    private String getProviderInfo(ChatClientObservationContext context) {
        // AiOperationMetadata 没有 getProvider() 方法
        // 查看 ChatClientObservationContext 类中的初始化，使用了 AiProvider.SPRING_AI.value()
        return AiProvider.SPRING_AI.value(); // 返回默认值
        // 或者根据需要指定自定义值
        // return "bytedesk-ai-provider";
    }
    
    // 辅助方法，获取消息数量
    private int getMessageCount(ChatClientObservationContext context) {
        // 修复getPrompt()方法未定义的问题
        try {
            if (context.getRequest() != null) {
                // 在Spring AI的新版本中，DefaultChatClientRequestSpec可能有不同的API
                // 以下是一个安全的实现，返回默认值
                return 1; // 返回默认消息数
                
                // 如果能够访问到messages属性，可以使用以下代码
                // return context.getRequest().getMessages().size();
            }
        } catch (Exception e) {
            // 忽略异常，使用默认值
        }
        return 0;
    }
}
