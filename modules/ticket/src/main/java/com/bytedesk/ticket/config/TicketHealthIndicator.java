/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-05 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-05 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.config;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;

/**
 * Ticket模块健康检查
 * 监控工单系统相关服务：Flowable工作流引擎、LDAP连接、数据库等
 */
@Slf4j
@Component
public class TicketHealthIndicator implements HealthIndicator {

    @Value("${bytedesk.ticket.ldap.enabled:false}")
    private boolean ldapEnabled;

    @Value("${bytedesk.ticket.ldap.server:ldap.bytedesk.com}")
    private String ldapServer;

    @Value("${bytedesk.ticket.ldap.port:389}")
    private int ldapPort;

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private RepositoryService repositoryService;

    @Autowired(required = false)
    private RuntimeService runtimeService;

    @Override
    public Health health() {
        try {
            Health.Builder builder = Health.up();

            // 检查数据库连接
            if (dataSource != null) {
                try (Connection connection = dataSource.getConnection()) {
                    builder.withDetail("database-status", "Connected");
                } catch (Exception e) {
                    log.error("Ticket database health check failed", e);
                    builder.down()
                           .withDetail("database-status", "Connection Failed")
                           .withDetail("database-error", e.getMessage());
                }
            }

            // 检查Flowable工作流引擎
            if (repositoryService != null && runtimeService != null) {
                try {
                    long deploymentCount = repositoryService.createDeploymentQuery().count();
                    long processInstanceCount = runtimeService.createProcessInstanceQuery().count();
                    
                    builder.withDetail("flowable-status", "Active")
                           .withDetail("flowable-deployments", deploymentCount)
                           .withDetail("flowable-active-processes", processInstanceCount);
                } catch (Exception e) {
                    log.error("Flowable health check failed", e);
                    builder.down()
                           .withDetail("flowable-status", "Error")
                           .withDetail("flowable-error", e.getMessage());
                }
            } else {
                builder.withDetail("flowable-status", "Not Configured");
            }

            // 检查LDAP连接
            if (ldapEnabled) {
                try (Socket socket = new Socket(ldapServer, ldapPort)) {
                    socket.setSoTimeout(3000);
                    builder.withDetail("ldap-status", "Connected")
                           .withDetail("ldap-server", ldapServer)
                           .withDetail("ldap-port", ldapPort);
                } catch (IOException e) {
                    log.error("LDAP health check failed", e);
                    builder.down()
                           .withDetail("ldap-status", "Connection Failed")
                           .withDetail("ldap-server", ldapServer)
                           .withDetail("ldap-port", ldapPort)
                           .withDetail("ldap-error", e.getMessage());
                }
            } else {
                builder.withDetail("ldap-status", "Disabled");
            }

            return builder.build();
            
        } catch (Exception e) {
            log.error("Ticket health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
