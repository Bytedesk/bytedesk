package com.bytedesk.ai.provider.ollama;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.ollama4j.OllamaAPI;

/**
 * https://github.com/ollama4j/ollama4j
 * https://ollama4j.github.io/ollama4j/intro
 */
@Configuration
public class Ollama4jConfig {

    @Value("${spring.ai.ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.request-timeout-seconds:120}")
    private int ollamaRequestTimeoutSeconds;

    @Bean(name = "ollama4jApi")
    OllamaAPI getOllamaAPI() {
        OllamaAPI ollamaAPI = new OllamaAPI(ollamaBaseUrl);
        // https://ollama4j.github.io/ollama4j/apis-extras/verbosity
        ollamaAPI.setVerbose(true);
        ollamaAPI.setRequestTimeoutSeconds(ollamaRequestTimeoutSeconds);
        return ollamaAPI;
    }
    
}
