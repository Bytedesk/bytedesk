/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:17:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-29 13:40:05
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
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.llm.LlmService;
import com.bytedesk.ai.robot.RobotService;
import com.bytedesk.core.rbac.role.RoleService;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.upload.UploadService;
import com.bytedesk.team.department.DepartmentService;
import com.bytedesk.team.member.MemberService;
import com.bytedesk.team.organization.OrganizationService;

import lombok.extern.slf4j.Slf4j;

/**
 * https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/#features.spring-application.command-line-runner
 * 
 * @author bytedesk.com on 2019/4/21
 */
@Slf4j
@Component
public class InitDataRunner implements ApplicationRunner {

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
    LlmService llmService;

    @Autowired
    RobotService robotService;

    @Autowired
    UploadService uploadService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.debug("application started, init Default data, dont change the init order");

        roleService.initData();

        userService.initData();

        roleService.updateInitData();

        organizationService.initData();

        departmentService.initData();

        memberService.initData();

        llmService.initData();

        robotService.initData();

        uploadService.initUploadDir();
    }

}
