/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-12 12:09:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-17 12:24:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SpringAiConfig {

    // https://docs.spring.io/spring-ai/reference/api/chatclient.html
    @Bean("defaultChatClient")
    ChatClient defaultChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("You are a friendly chat bot that answers question in the voice of a {voice}")
                .build();
    }

    // chatModel bean
    // @Bean
    // ChatModel chatModel(ChatModel.Builder builder) {
    //     return builder
    //             .defaultSystem("You are a friendly chat bot that answers question in the voice of a {voice}")
    //             .build();
    // }

    // embeddingModel bean
    // @Bean
    // EmbeddingModel embeddingModel(EmbeddingModel.Builder builder) {
    //     return builder
    //             .defaultSystem("You are a friendly chat bot that answers question in the voice of a {voice}")
    //             .build();
    // }


}
