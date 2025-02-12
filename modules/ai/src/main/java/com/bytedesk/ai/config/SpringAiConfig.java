package com.bytedesk.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SpringAiConfig {

    // https://docs.spring.io/spring-ai/reference/api/chatclient.html
    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("You are a friendly chat bot that answers question in the voice of a {voice}")
                .build();
    }


    
}
