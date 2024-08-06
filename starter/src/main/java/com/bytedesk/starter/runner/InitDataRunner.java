/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:17:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-02 09:54:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.robot.RobotService;
import com.bytedesk.core.asistant.AsistantService;
import com.bytedesk.core.category.CategoryService;
import com.bytedesk.core.channel.ChannelService;
import com.bytedesk.core.quartz.QuartzService;
// import com.bytedesk.core.quick_button.QuickButtonService;
import com.bytedesk.core.rbac.authority.AuthorityService;
import com.bytedesk.core.rbac.organization.OrganizationService;
import com.bytedesk.core.rbac.role.RoleService;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.service.agent.AgentService;
import com.bytedesk.kbase.faq.FaqService;
import com.bytedesk.kbase.knowledge_base.KnowledgebaseService;
import com.bytedesk.kbase.quick_reply.QuickReplyService;
import com.bytedesk.kbase.upload.UploadService;
import com.bytedesk.service.workgroup.WorkgroupService;
import com.bytedesk.team.department.DepartmentService;
import com.bytedesk.team.member.MemberService;

import lombok.extern.slf4j.Slf4j;

/**
 * init data - 初始化基础数据
 * https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/#features.spring-application.command-line-runner
 * 
 * @author bytedesk.com on 2019/4/21
 */
@Slf4j
@Component
public class InitDataRunner implements ApplicationRunner {

    @Value("${server.port}")
    private String port;

    @Autowired
    AsistantService asistantService;

    @Autowired
    ChannelService channelService;

    @Autowired
    AuthorityService authorityService;

    @Autowired
    RoleService roleService;

    @Autowired
    UserService userService;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    MemberService memberService;

    @Autowired
    KnowledgebaseService kownledgebaseService;

    @Autowired
    RobotService robotService;

    @Autowired
    AgentService agentService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    FaqService faqService;

    @Autowired
    WorkgroupService workgroupService;

    @Autowired
    UploadService uploadService;

    @Autowired
    ThreadService threadService;

    @Autowired
    QuartzService quartzService;

    @Autowired
    QuickReplyService quickReplyService;

    // @Autowired
    // LogEventProducer logEventProducer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // log.debug("application started, initiating data...");

        asistantService.initData();

        channelService.initData();

        authorityService.initData();

        roleService.initData();

        userService.initData();

        organizationService.initData();

        userService.updateInitData();

        departmentService.initData();

        memberService.initData();

        kownledgebaseService.initData();

        categoryService.initData();

        faqService.initData();

        // quickButtonService.initData();

        robotService.initData();

        agentService.initData();

        workgroupService.initData();

        uploadService.initUploadDir();

        threadService.initData();

        quartzService.initData();

        quickReplyService.initData();

        // logEventProducer.simulate();

        log.debug("application started. you can now open: http://127.0.0.1:{}/dev", port);
    }

}
