/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 13:45:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-18 22:16:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.config;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
@Description("Flowable Workflow Configuration - Flowable BPMN workflow engine configuration for ticket processing")
public class FlowableConfig {

    // @Autowired
    // private RepositoryService repositoryService;
    
    // @Autowired
    // private TicketProcessInstanceListener processInstanceListener;
    
    // @Autowired
    // private RuntimeService runtimeService;
    
    // @PostConstruct
    // public void init() {
    //     runtimeService.addEventListener(processInstanceListener);
    // }
    
    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> engineConfigurationConfigurer() {
        return engineConfiguration -> {
            engineConfiguration.setEnableProcessDefinitionInfoCache(true);
            engineConfiguration.setDatabaseSchemaUpdate("true");
        };
    }

    // @PostConstruct
    // public void deployProcessDefinitions() {
    //     // 部署流程定义和规则文件
    //     repositoryService.createDeployment()
    //         .addClasspathResource("processes/agent-ticket-process.bpmn20.xml")
    //         .addClasspathResource("processes/group-ticket-process.bpmn20.xml")
    //         .addClasspathResource("dmn/ticket-priority-rules.dmn")  // 添加规则文件
    //         .deploy();
    // }
    
    // 可选：实现多租户数据源
    // private MultiTenantDataSource createMultiTenantDataSource() {
    //     // 实现多租户数据源逻辑
    //     return new MultiTenantDataSource() {
    //         @Override
    //         public DataSource getDataSource(String tenantId) {
    //             // 根据tenantId返回对应的数据源
    //             return null;
    //         }
    //     };
    // }
} 