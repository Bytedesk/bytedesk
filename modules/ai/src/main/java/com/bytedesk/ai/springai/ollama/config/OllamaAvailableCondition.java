/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-14 09:03:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-14 09:10:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.ollama.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.env.Environment;

import java.net.Socket;

public class OllamaAvailableCondition implements Condition {
    
    private static final Logger logger = LoggerFactory.getLogger(OllamaAvailableCondition.class);
    
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        boolean enabled = Boolean.parseBoolean(env.getProperty("spring.ai.ollama.chat.enabled", "false"));
        
        if (!enabled) {
            return false;
        }
        
        String host = env.getProperty("spring.ai.ollama.base-url", "http://localhost:11434");
        
        try {
            // 提取主机名和端口
            String hostname = host.replaceAll("https?://", "");
            int port = 11434; // 默认端口
            
            if (hostname.contains(":")) {
                String[] parts = hostname.split(":");
                hostname = parts[0];
                port = Integer.parseInt(parts[1]);
            }
            
            // 尝试连接服务
            try (Socket socket = new Socket(hostname, port)) {
                logger.info("Ollama 服务可用 - 主机: {}, 端口: {}", hostname, port);
                return true;
            } catch (Exception e) {
                logger.warn("Ollama 服务不可用 - 主机: {}, 端口: {}. 错误: {}", hostname, port, e.getMessage());
                return false;
            }
        } catch (Exception e) {
            logger.error("检查 Ollama 服务可用性时出错: {}", e.getMessage());
            return false;
        }
    }
}
