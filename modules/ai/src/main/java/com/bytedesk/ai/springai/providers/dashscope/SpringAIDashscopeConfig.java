/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:30:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-07 08:54:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dashscope;

// import org.springframework.ai.chat.client.ChatClient;
// import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
// import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
// import org.springframework.ai.chat.memory.InMemoryChatMemory;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

// import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
// import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioTranscriptionApi;
// import com.alibaba.cloud.ai.dashscope.api.DashScopeSpeechSynthesisApi;
// import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionModel;
// import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisModel;
// import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
// import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;

// /**
//  * Spring AI Alibaba
//  * https://java2ai.com/docs/dev/get-started/
//  * https://github.com/alibaba/spring-ai-alibaba
//  * https://java2ai.com/docs/dev/tutorials/basics/chat-client/?spm=4347728f.63599dc2.0.0.8c026e97YbEM8P
//  * Examples:
//  * https://github.com/springaialibaba/spring-ai-alibaba-examples
//  * 阿里云百炼大模型获取api key：
//  * https://bailian.console.aliyun.com/?apiKey=1#/api-key
//  * 阿里云百炼大模型模型列表：
//  * https://bailian.console.aliyun.com/?spm=a2c4g.11186623.0.0.11c67980m5X2VR#/model-market
//  */
// @Configuration
// @ConditionalOnProperty(name = "spring.ai.dashscope.chat.enabled", havingValue = "true")
// public class SpringAIDashscopeConfig {

//     private static final String DEFAULT_PROMPT = "你是一个博学的智能聊天助手，请根据用户提问回答！";

//     @Value("${spring.ai.dashscope.api-key:sk-xxx}")
//     private String dashScopeApiKey;

//     @Value("${spring.ai.dashscope.chat.options.model:deepseek-r1}")
//     private String dashScopeChatOptionsModel;

//     @Value("${spring.ai.dashscope.chat.options.temperature:0.7}")
//     private double dashScopeChatOptionsTemperature;

//     @Value("${spring.ai.dashscope.chat.options.topP:3}")
//     private double dashScopeChatOptionsTopP;

//     @Bean("bytedeskDashScopeApi")
//     @ConditionalOnProperty(name = "spring.ai.dashscope.chat.enabled", havingValue = "true")
//     DashScopeApi bytedeskDashScopeApi() {
//         return new DashScopeApi(dashScopeApiKey);
//     }

//     @Bean("bytedeskDashScopeChatClientBuilder")
//     @ConditionalOnProperty(name = "spring.ai.dashscope.chat.enabled", havingValue = "true")
//     ChatClient.Builder bytedeskDashScopeChatClientBuilder() {
//         return ChatClient.builder(bytedeskDashScopeChatModel());
//     }

//     @Bean("bytedeskDashScopeChatOptions")
//     @ConditionalOnProperty(name = "spring.ai.dashscope.chat.enabled", havingValue = "true")
//     DashScopeChatOptions bytedeskDashScopeChatOptions() {
//         return DashScopeChatOptions.builder()
//                 .withModel(dashScopeChatOptionsModel)
//                 .withTopP(dashScopeChatOptionsTopP)
//                 .withTemperature(dashScopeChatOptionsTemperature)
//                 .build();
//     }

//     @Bean("bytedeskDashScopeChatClient")
//     @ConditionalOnProperty(name = "spring.ai.dashscope.chat.enabled", havingValue = "true")
//     ChatClient bytedeskDashScopeChatClient() {
//         return ChatClient.builder(bytedeskDashScopeChatModel())
//                 .defaultSystem(DEFAULT_PROMPT)
//                 // 实现 Chat Memory 的 Advisor
//                 // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
//                 .defaultAdvisors(
//                         new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
//                 // 实现 Logger 的 Advisor
//                 .defaultAdvisors(
//                         new SimpleLoggerAdvisor())
//                 // 设置 ChatClient 中 ChatModel 的 Options 参数
//                 .defaultOptions(bytedeskDashScopeChatOptions())
//                 .build();
//     }
    
//     @Bean("bytedeskDashScopeChatModel")
//     @ConditionalOnProperty(name = "spring.ai.dashscope.chat.enabled", havingValue = "true")
//     DashScopeChatModel bytedeskDashScopeChatModel() {
//         return new DashScopeChatModel(bytedeskDashScopeApi(), bytedeskDashScopeChatOptions());
//     }

//     @Bean("bytedeskDashScopeAudioTranscriptionApi")
//     @ConditionalOnProperty(name = "spring.ai.dashscope.audio.transcription.enabled", havingValue = "true")
//     DashScopeAudioTranscriptionApi bytedeskDashScopeAudioTranscriptionApi() {
//         return new DashScopeAudioTranscriptionApi(dashScopeApiKey);
//     }
    
//     @Bean("bytedeskDashScopeAudioTranscriptionModel")
//     @ConditionalOnProperty(name = "spring.ai.dashscope.audio.transcription.enabled", havingValue = "true")
//     DashScopeAudioTranscriptionModel bytedeskDashScopeAudioTranscriptionModel() {
//         return new DashScopeAudioTranscriptionModel(bytedeskDashScopeAudioTranscriptionApi());
//     }

//     @Bean("bytedeskDashScopeSpeechSynthesisApi")
//     @ConditionalOnProperty(name = "spring.ai.dashscope.audio.synthesis.enabled", havingValue = "true")
//     DashScopeSpeechSynthesisApi bytedeskDashScopeSpeechSynthesisApi() {
//         return new DashScopeSpeechSynthesisApi(dashScopeApiKey);
//     }

//     @Bean("bytedeskDashScopeSpeechSynthesisModel")
//     @ConditionalOnProperty(name = "spring.ai.dashscope.audio.synthesis.enabled", havingValue = "true")
//     DashScopeSpeechSynthesisModel bytedeskDashScopeSpeechSynthesisModel() {
//         return new DashScopeSpeechSynthesisModel(bytedeskDashScopeSpeechSynthesisApi());
//     }

    

// }
