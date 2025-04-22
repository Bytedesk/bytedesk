/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 17:56:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-21 18:27:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dashscope;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.ai.robot.RobotProtobuf;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.dashscope.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIDashscopeService extends BaseSpringAIService {

    private final Counter aiRequestCounter;

    public SpringAIDashscopeService(MeterRegistry registry) {
        super(); // 调用基类的无参构造函数

        // 初始化监控指标
        this.aiRequestCounter = Counter.builder("bytedesk.ai.dashscope.requests")
                .description("Number of DashScope AI requests")
                .register(registry);
    }

    @Override
    protected void processPrompt(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply) {
        aiRequestCounter.increment();

        // 由于目前没有实现具体的DashScope调用逻辑，仅添加符合新接口的方法签名
        // 实际实现需要根据DashScope API进行适配
        messageProtobufReply.setType(MessageTypeEnum.ERROR);
        messageProtobufReply.setContent("DashScope服务尚未实现");
        messageSendService.sendProtobufMessage(messageProtobufReply);
    }

    @Override
    protected String generateFaqPairs(String prompt) {
        return "";
    }

    @Override
    protected String processPromptSync(String message) {
        return "";
    }

    @Override
    protected void processPromptSSE(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        
        // 由于目前没有实现具体的DashScope调用逻辑，仅添加符合新接口的方法签名
        try {
            messageProtobufReply.setType(MessageTypeEnum.ERROR);
            messageProtobufReply.setContent("DashScope服务尚未实现");
            persistMessage(messageProtobufQuery, messageProtobufReply);
            String messageJson = messageProtobufReply.toJson();
            
            emitter.send(SseEmitter.event()
                    .data(messageJson)
                    .id(messageProtobufReply.getUid())
                    .name("message"));
            emitter.complete();
        } catch (Exception e) {
            log.error("Error in DashScope SSE", e);
            try {
                emitter.completeWithError(e);
            } catch (Exception ex) {
                log.error("Failed to complete emitter with error", ex);
            }
        }
    }

}
