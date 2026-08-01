/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 14:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:37:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.config;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Call连接测试工具
 * 在应用程序启动时自动测试ESL连接
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "bytedesk.call.freeswitch", name = "enabled", havingValue = "true", matchIfMissing = false)
public class CallConnectionTester implements CommandLineRunner {

    private final CallFreeswitchProperties callProperties;

    @Override
    public void run(String... args) {
        log.info(CallI18nConsts.CONNECTION_TEST_START);
        testConnection();
    }

    /**
     * 测试Call连接
     */
    public void testConnection() {
        String server = callProperties.getServer();
        int port = callProperties.getEslPort();
        
        log.info(CallI18nConsts.CONNECTION_TEST_TARGET, server, port);
        
        try (Socket socket = new Socket()) {
            // 设置5秒连接超时
            socket.connect(new java.net.InetSocketAddress(server, port), 5000);
            
            if (socket.isConnected()) {
                log.info(CallI18nConsts.CONNECTION_TEST_NETWORK_SUCCESS, server, port);
                
                // 尝试读取Call的欢迎消息
                try {
                    socket.setSoTimeout(3000); // 3秒读取超时
                    byte[] buffer = new byte[1024];
                    int bytesRead = socket.getInputStream().read(buffer);
                    
                    if (bytesRead > 0) {
                        String response = new String(buffer, 0, bytesRead);
                        log.info(CallI18nConsts.CONNECTION_TEST_RESPONSE_RECEIVED, response.trim());
                        
                        if (response.contains("rude-rejection")) {
                            log.error(CallI18nConsts.CONNECTION_TEST_ACL_REJECTED);
                            log.error(CallI18nConsts.CONNECTION_TEST_SOLUTION);
                            log.error(CallI18nConsts.CONNECTION_TEST_SOLUTION_STEP_1);
                            log.error(CallI18nConsts.CONNECTION_TEST_SOLUTION_STEP_2);
                            log.error(CallI18nConsts.CONNECTION_TEST_SOLUTION_STEP_3);
                        } else if (response.contains("auth/request")) {
                            log.info(CallI18nConsts.CONNECTION_TEST_WAIT_AUTH);
                        }
                    }
                } catch (SocketTimeoutException e) {
                    log.warn(CallI18nConsts.CONNECTION_TEST_READ_TIMEOUT);
                } catch (IOException e) {
                    log.error(CallI18nConsts.CONNECTION_TEST_READ_FAILED, e.getMessage());
                }
            }
            
        } catch (IOException e) {
            log.error(CallI18nConsts.CONNECTION_TEST_CONNECT_FAILED, e.getMessage());
            
            if (e.getMessage().contains("Connection refused")) {
                log.error(CallI18nConsts.CONNECTION_TEST_POSSIBLE_REASON);
                log.error(CallI18nConsts.CONNECTION_TEST_REASON_SERVICE_NOT_RUNNING);
                log.error(CallI18nConsts.CONNECTION_TEST_REASON_PORT_NOT_OPEN, port);
                log.error(CallI18nConsts.CONNECTION_TEST_REASON_FIREWALL);
            } else if (e.getMessage().contains("timeout")) {
                log.error(CallI18nConsts.CONNECTION_TEST_POSSIBLE_REASON);
                log.error(CallI18nConsts.CONNECTION_TEST_REASON_NETWORK_TIMEOUT);
                log.error(CallI18nConsts.CONNECTION_TEST_REASON_SERVER_INVALID, server);
                log.error(CallI18nConsts.CONNECTION_TEST_REASON_ROUTE);
            }
            
            log.error(CallI18nConsts.CONNECTION_TEST_CURRENT_CONFIG, 
                    server, port, callProperties.getEslPassword());
        }
        
        log.info(CallI18nConsts.CONNECTION_TEST_FINISH);
    }

    /**
     * 提供连接诊断信息
     */
    public void printDiagnosticInfo() {
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_HEADER);
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_SERVER, callProperties.getServer());
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_PORT, callProperties.getEslPort());
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_PASSWORD, callProperties.getEslPassword());
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_ENABLED, callProperties.isEnabled());
        
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_GUIDE);
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_CHECK_SERVICE);
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_CHECK_SERVICE_CMD_1);
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_CHECK_SERVICE_CMD_2);
        
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_CHECK_PORT);
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_CHECK_PORT_CMD_1);
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_CHECK_PORT_CMD_2);
        
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_CHECK_TELNET);
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_CHECK_TELNET_CMD, callProperties.getServer(), callProperties.getEslPort());
        
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_CHECK_CONFIG);
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_CHECK_CONFIG_FILE);
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_CHECK_ACL_FILE);
        
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_CHECK_LOG);
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_CHECK_LOG_CMD);
        
        log.info(CallI18nConsts.CONNECTION_DIAGNOSTIC_FOOTER);
    }
}
