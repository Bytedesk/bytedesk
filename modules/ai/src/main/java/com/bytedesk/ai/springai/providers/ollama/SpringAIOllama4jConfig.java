/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-11 12:19:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 09:28:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.ollama;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import io.github.ollama4j.OllamaAPI;

// /**
//  * https://github.com/ollama4j/ollama4j
//  * https://ollama4j.github.io/ollama4j/intro
//  */
// @Configuration
// @ConditionalOnProperty(prefix = "spring.ai.ollama.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
// public class SpringAIOllama4jConfig {

//     @Value("${spring.ai.ollama.base-url}")
//     private String ollamaBaseUrl;

//     @Value("${spring.ai.ollama.request-timeout-seconds:120}")
//     private Integer ollamaRequestTimeoutSeconds;

//     @Bean(name = "ollama4jApi")
//     OllamaAPI getOllamaAPI() {
//         OllamaAPI ollamaAPI = new OllamaAPI(ollamaBaseUrl);
//         // https://ollama4j.github.io/ollama4j/apis-extras/verbosity
//         ollamaAPI.setVerbose(true);
//         ollamaAPI.setRequestTimeoutSeconds(ollamaRequestTimeoutSeconds);
//         return ollamaAPI;
//     }
    
// }
