/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 10:14:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:20:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.config;

import java.net.InetSocketAddress;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.call.esl.client.inbound.Client;
import com.bytedesk.call.esl.client.inbound.InboundConnectionFailure;
import com.bytedesk.call.esl.client.internal.IModEslApi;
import com.bytedesk.call.esl.client.transport.CommandResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Call配置类
 * 
 * 该配置类用于设置Call ESL客户端连接和事件监听器。
 * https://github.com/esl-client/esl-client?tab=readme-ov-file
 * 
 * @author jackning
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "bytedesk.call.freeswitch", name = "enabled", havingValue = "true", matchIfMissing = false)
public class CallConfig {
        
    private final CallFreeswitchProperties callFreeswitchProperties;
    
    private final CallEventListener callEventListener;

    /**
     * 配置Call ESL客户端
     */
    @Bean
    public Client eslClient() {
        Client inboundClient = new Client();
        
        // 连接重试配置
        int maxRetries = Math.max(1, callFreeswitchProperties.getMaxRetries());
        int retryDelayMs = Math.max(500, callFreeswitchProperties.getRetryDelayMs());
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.info("第{}次尝试连接Call ESL: {}:{}", 
                        attempt, callFreeswitchProperties.getServer(), callFreeswitchProperties.getEslPort());
                        
                // 增加更长的超时时间
                inboundClient.connect(
                    new InetSocketAddress(
                        callFreeswitchProperties.getServer(),
                        callFreeswitchProperties.getEslPort()
                    ),
                    callFreeswitchProperties.getEslPassword(),
                    callFreeswitchProperties.getConnectTimeoutSeconds()
                );
                    
                // 验证连接是否真正建立
                if (inboundClient.canSend()) {
                    // 注册事件监听器
                    inboundClient.addEventListener(callEventListener);
                    
                    // 订阅事件（默认 all）
                    String subscriptions = callFreeswitchProperties.getEventSubscriptions();
                    CommandResponse subscriptionResp = inboundClient.setEventSubscriptions(
                            IModEslApi.EventFormat.PLAIN,
                            subscriptions == null || subscriptions.isBlank() ? "all" : subscriptions
                    );
                    logCommandResponse("setEventSubscriptions", subscriptionResp);

                    // FusionPBX风格过滤器：event plain all + filter Event-Name/Event-Subclass
                    registerEventFilters(inboundClient);
                    
                        log.info(CallI18nConsts.CONFIG_ESL_CONNECTED, 
                            callFreeswitchProperties.getServer(), callFreeswitchProperties.getEslPort());
                    
                    // 连接成功，跳出重试循环
                    break;
                } else {
                    log.warn(CallI18nConsts.CONFIG_ESL_CONNECTED_UNSTABLE);
                    throw new InboundConnectionFailure("Connection established but cannot send commands");
                }
                
            } catch (InboundConnectionFailure e) {
                log.error(CallI18nConsts.CONFIG_ESL_CONNECT_ATTEMPT_FAILED, attempt, e.getMessage());
                
                // 检查具体的错误类型
                if (e.getMessage() != null) {
                    if (e.getMessage().contains("rude-rejection") || e.getMessage().contains("Access Denied")) {
                        log.error(CallI18nConsts.CONFIG_ESL_ACL_REJECTED_REASON);
                        log.error(CallI18nConsts.CONFIG_ESL_PASSWORD_WRONG, callFreeswitchProperties.getEslPassword());
                        log.error(CallI18nConsts.CONFIG_ESL_IP_NOT_ALLOWED);
                        log.error(CallI18nConsts.CONFIG_ESL_SOCKET_CONFIG_RESTRICTED);
                        log.error(CallI18nConsts.CONFIG_ESL_FIREWALL_BLOCKED);
                        
                        // ACL拒绝错误通常不需要重试
                        if (attempt == maxRetries) {
                            log.error(CallI18nConsts.CONFIG_ESL_ALL_ATTEMPTS_REJECTED);
                        }
                    } else if (e.getMessage().contains("Connection refused") || e.getMessage().contains("timeout")) {
                        log.error(CallI18nConsts.CONFIG_ESL_NETWORK_ISSUE_REASON);
                        log.error(CallI18nConsts.CONFIG_ESL_SERVICE_NOT_RUNNING);
                        log.error(CallI18nConsts.CONFIG_ESL_PORT_BLOCKED, callFreeswitchProperties.getEslPort());
                        log.error(CallI18nConsts.CONFIG_ESL_NETWORK_TIMEOUT);
                    }
                }
                
                // 如果不是最后一次尝试，等待后重试
                if (attempt < maxRetries) {
                    try {
                        log.info(CallI18nConsts.CONFIG_ESL_WAIT_RETRY, retryDelayMs);
                        Thread.sleep(retryDelayMs);
                        retryDelayMs *= 2; // 指数退避
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error(CallI18nConsts.CONFIG_ESL_RETRY_INTERRUPTED);
                        break;
                    }
                } else {
                    log.error(CallI18nConsts.CONFIG_ESL_FINAL_FAILURE, maxRetries);
                }
            }
        }
        
        return inboundClient;
    }

    private void registerEventFilters(Client inboundClient) {
        if (!callFreeswitchProperties.isEnableEventFilters()) {
            log.info(CallI18nConsts.CONFIG_ESL_SKIP_FILTER_REGISTER);
            return;
        }

        List<String> eventNameFilters = callFreeswitchProperties.getEventNameFilters();
        if (eventNameFilters != null) {
            for (String eventName : eventNameFilters) {
                if (eventName == null || eventName.isBlank()) {
                    continue;
                }
                CommandResponse resp = inboundClient.addEventFilter("Event-Name", eventName.trim());
                logCommandResponse("filter Event-Name=" + eventName, resp);
            }
        }

        List<String> eventSubclassFilters = callFreeswitchProperties.getEventSubclassFilters();
        if (eventSubclassFilters != null) {
            for (String eventSubclass : eventSubclassFilters) {
                if (eventSubclass == null || eventSubclass.isBlank()) {
                    continue;
                }
                CommandResponse resp = inboundClient.addEventFilter("Event-Subclass", eventSubclass.trim());
                logCommandResponse("filter Event-Subclass=" + eventSubclass, resp);
            }
        }
    }

    private void logCommandResponse(String action, CommandResponse response) {
        if (response == null) {
            log.warn(CallI18nConsts.ESL_ACTION_EMPTY_RESPONSE, action);
            return;
        }
        if (response.isOk()) {
            log.info(CallI18nConsts.ESL_ACTION_SUCCESS, action, response.getReplyText());
        } else {
            log.warn(CallI18nConsts.ESL_ACTION_FAILED, action, response.getReplyText());
        }
    }

}
