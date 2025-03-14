package com.bytedesk.ai.springai.ollama.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.bytedesk.ai.springai.ollama.fallback.FallbackChatClient;

@Configuration
public class AIChatClientConfig {

    @Bean
    @Primary
    @Conditional(OllamaNotAvailableCondition.class)
    public ChatClient fallbackChatClient() {
        return new FallbackChatClient();
    }
}
