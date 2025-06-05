/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-31 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-05 18:54:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.config;

// import org.springframework.ai.chat.model.ChatModel;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Primary;

// import lombok.extern.slf4j.Slf4j;

// /**
//  * ChatModel配置类
//  * 提供ChatModel的默认实现，当没有其他ChatModel可用时作为备用
//  */
// @Slf4j
// @Configuration
// public class ChatModelConfig {

//     /**
//      * 当没有其他ChatModel bean时，提供一个默认的ChatModel实现
//      * 这个bean只有在启用Java AI功能时才会创建
//      */
//     @Bean
//     @Primary
//     @ConditionalOnMissingBean(ChatModel.class)
//     @ConditionalOnProperty(name = "bytedesk.features.java-ai", havingValue = "true", matchIfMissing = false)
//     public ChatModel defaultChatModel() {
//         log.warn("No ChatModel implementation found, using fallback implementation");
        
//         // 返回一个简单的ChatModel实现作为备用
//         return new FallbackChatModel();
//     }
// }
