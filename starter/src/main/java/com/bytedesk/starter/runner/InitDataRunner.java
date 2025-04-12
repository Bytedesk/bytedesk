/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:17:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-12 22:14:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.runner;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.provider.LlmProviderInitializer;
import com.bytedesk.ai.robot.RobotInitializer;
import com.bytedesk.core.rbac.organization.OrganizationInitializer;
import com.bytedesk.core.rbac.role.RoleInitializer;
import com.bytedesk.core.rbac.user.UserInitializer;
import com.bytedesk.core.utils.NetworkUtils;
import com.bytedesk.kbase.faq.FaqInitializer;
import com.bytedesk.service.agent.AgentInitializer;
import com.bytedesk.service.workgroup.WorkgroupInitializer;
import com.bytedesk.team.member.MemberInitializer;

import lombok.extern.slf4j.Slf4j;

/**
 * @author bytedesk.com on 2019/4/21
 */
@Slf4j
@Component
public class InitDataRunner implements ApplicationRunner {

    @Autowired
    private RoleInitializer roleInitializer;

    @Autowired
    private UserInitializer userInitializer;

    @Autowired
    private OrganizationInitializer organizationInitializer;

    @Autowired
    private MemberInitializer memberInitializer;

    @Autowired
    private AgentInitializer agentInitializer;

    @Autowired
    private WorkgroupInitializer workgroupInitializer;

    @Autowired
    private FaqInitializer faqInitializer;

    @Autowired
    private RobotInitializer robotInitializer;

    @Autowired
    private LlmProviderInitializer llmProviderInitializer;

    @Value("${application.version}")
    private String version;
    
    @Value("${server.port}")
    private String port;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 集中执行必要的初始化，方便管理和控制初始化顺序，避免分散执行因依赖关系导致初始化失败
        roleInitializer.init();

        // userInitializer.init();

        // organizationInitializer.init();

        // memberInitializer.init();

        // agentInitializer.init();

        // workgroupInitializer.init();

        // faqInitializer.init();

        // robotInitializer.init();

        // llmProviderInitializer.init();

        // 在应用的主类或配置类中
        // TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        // TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        String localIP = NetworkUtils.getFirstNonLoopbackIP();
        List<String> allIPs = NetworkUtils.getLocalIPs();

        log.info("bytedesk.im v{} started at:", version);
        log.info("Local Access:  http://127.0.0.1:{}", port);
        log.info("Network Access: http://{}:{}", localIP, port);
        
        if (allIPs.size() > 1) {
            log.info("Other Network IPs:");
            allIPs.stream()
                .filter(ip -> !ip.equals(localIP))
                .forEach(ip -> log.info("http://{}:{}", ip, port));
        }
    }

}
