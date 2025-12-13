/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-20 12:04:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 18:56:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.controller;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.ai.utils.tools.DateTimeTools;
import com.bytedesk.ai.utils.tools.MathTools;
import com.bytedesk.ai.utils.tools.WeatherRequest;
import com.bytedesk.ai.utils.tools.WeatherService;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 工具调用
 * https://docs.spring.io/spring-ai/reference/api/tools.html
 * 大模型工具支持情况
 * https://docs.spring.io/spring-ai/reference/api/chat/comparison.html
 */
@Slf4j
@RestController
@RequestMapping("/spring/ai/api/v1/tools")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.ai.model.chat", havingValue = "zhipuai", matchIfMissing = false)
public class SpringAIToolsController {

        private final BytedeskProperties bytedeskProperties;

        private final ChatModel chatModel;

        private final ChatClient primaryChatClient;
        // Spring AI requires an explicit schema even for parameterless tools.
        private static final String EMPTY_OBJECT_SCHEMA = "{\"type\":\"object\",\"properties\":{}}";

        // http://127.0.0.1:9003/spring/ai/api/v1/tools/time?message=
        // get current date and time
        @GetMapping(value = "/time", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<JsonResult<?>> time(
                        @RequestParam(value = "message", defaultValue = "What day is tomorrow?") String message) {

                if (!bytedeskProperties.getDebug()) {
                        return ResponseEntity.ok(JsonResult.error("Service is not available"));
                }

                String response = primaryChatClient
                                .prompt(message)
                                .tools(new DateTimeTools())
                                .call()
                                .content();
                log.info("response: {}", response);

                return ResponseEntity.ok(JsonResult.success(response));
        }

        // set alarm
        // http://127.0.0.1:9003/spring/ai/api/v1/tools/alarm?message=
        @GetMapping("/alarm")
        public ResponseEntity<JsonResult<?>> alarm(
                        @RequestParam(value = "message", defaultValue = "Can you set an alarm 10 minutes from now?") String message) {

                if (!bytedeskProperties.getDebug()) {
                        return ResponseEntity.ok(JsonResult.error("Service is not available"));
                }

                String response = primaryChatClient
                                .prompt(message)
                                .tools(new DateTimeTools())
                                .call()
                                .content();
                // The alarm has been set for 10 minutes from now.
                log.info("response: {}", response);

                return ResponseEntity.ok(JsonResult.success(response));
        }

        // http://127.0.0.1:9003/spring/ai/api/v1/tools/method-tool-callback?message=
        @GetMapping("/method-tool-callback")
        public ResponseEntity<JsonResult<?>> methodToolCallback(
                        @RequestParam(value = "message", defaultValue = "What is the current date and time?") String message) {

                if (!bytedeskProperties.getDebug()) {
                        return ResponseEntity.ok(JsonResult.error("Service is not available"));
                }

                Method method = ReflectionUtils.findMethod(DateTimeTools.class, "getCurrentDateTimeMethodToolCallback");
                ToolCallback toolCallback = MethodToolCallback.builder()
                                .toolDefinition(ToolDefinition.builder()
                                                .name("getCurrentDateTimeMethodToolCallback")
                                                .description("Get the current date and time in the user's timezone")
                                                .inputSchema(EMPTY_OBJECT_SCHEMA)
                                                .build())
                                .toolMethod(method)
                                .toolObject(new DateTimeTools())
                                .build();

                String response = primaryChatClient
                                .prompt(message)
                                .tools(toolCallback)
                                .call()
                                .content();
                log.info("methodToolCallback response: {}", response);

                return ResponseEntity.ok(JsonResult.success(response));
        }

        // weather
        // http://127.0.0.1:9003/spring/ai/api/v1/tools/weather?message=
        // https://docs.spring.io/spring-ai/reference/api/tools.html#_programmatic_specification_functiontoolcallback
        @GetMapping("/weather")
        public ResponseEntity<JsonResult<?>> weather(
                        @RequestParam(value = "message", defaultValue = "What is the weather in Beijing?") String message) {

                if (!bytedeskProperties.getDebug()) {
                        return ResponseEntity.ok(JsonResult.error("Service is not available"));
                }

                ToolCallback toolCallback = FunctionToolCallback
                                .builder("currentWeather", new WeatherService())
                                .description("Get the weather in location")
                                .inputType(WeatherRequest.class)
                                .build();

                String response = primaryChatClient
                                .prompt(message)
                                .tools(toolCallback)
                                .call()
                                .content();
                log.info("weather response: {}", response);

                return ResponseEntity.ok(JsonResult.success(response));
        }

        // http://127.0.0.1:9003/spring/ai/api/v1/tools/user-controlled
        @GetMapping("/user-controlled")
        public ResponseEntity<JsonResult<?>> userControlled() {

                if (!bytedeskProperties.getDebug()) {
                        return ResponseEntity.ok(JsonResult.error("Service is not available"));
                }

                ToolCallingManager toolCallingManager = DefaultToolCallingManager.builder().build();
                ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
                String conversationId = UUID.randomUUID().toString();

                ChatOptions chatOptions = ToolCallingChatOptions.builder()
                                .toolCallbacks(ToolCallbacks.from(new MathTools()))
                                .internalToolExecutionEnabled(false)
                                .build();
                Prompt prompt = new Prompt(
                                        List.of(new SystemMessage("You are a helpful assistant."),
                                                new UserMessage("What is 6 * 8?")),
                                chatOptions);
                chatMemory.add(conversationId, prompt.getInstructions());

                Prompt promptWithMemory = new Prompt(chatMemory.get(conversationId), chatOptions);
                ChatResponse chatResponse = chatModel.call(promptWithMemory);
                chatMemory.add(conversationId, chatResponse.getResult().getOutput());

                while (chatResponse.hasToolCalls()) {
                        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(promptWithMemory,
                                        chatResponse);
                        chatMemory.add(conversationId, toolExecutionResult.conversationHistory()
                                        .get(toolExecutionResult.conversationHistory().size() - 1));
                        promptWithMemory = new Prompt(chatMemory.get(conversationId), chatOptions);
                        chatResponse = chatModel.call(promptWithMemory);
                        chatMemory.add(conversationId, chatResponse.getResult().getOutput());
                }

                UserMessage newUserMessage = new UserMessage("What did I ask you earlier?");
                chatMemory.add(conversationId, newUserMessage);

                ChatResponse newResponse = chatModel.call(new Prompt(chatMemory.get(conversationId)));

                return ResponseEntity.ok(JsonResult.success(newResponse.getResult().getOutput()));
        }

}
