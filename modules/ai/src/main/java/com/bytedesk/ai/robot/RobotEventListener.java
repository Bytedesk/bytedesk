/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-12 07:17:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-23 11:16:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.kb.Kb;
import com.bytedesk.ai.kb.KbService;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.organization.Organization;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
// import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class RobotEventListener {

    private final RobotService robotService;

    private final KbService kbService;

    private final UidUtils uidUtils;

    @Order(2)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        Organization organization = (Organization) event.getSource();
        // User user = organization.getUser();
        String orgUid = organization.getUid();
        log.info("robot - organization created: {}", organization.getName());
        // 
        Kb kb = kbService.getKb(I18Consts.I18N_ROBOT_NICKNAME, organization.getUid());
        RobotLlm llm = RobotLlm.builder().build();

        Robot robot = Robot.builder()
                // .nickname(I18Consts.I18N_ROBOT_NICKNAME)
                .description(I18Consts.I18N_ROBOT_DESCRIPTION)
                .type(RobotTypeEnum.SERVICE)
                // .orgUid(organization.getUid())
                .kb(kb)
                .llm(llm)
                .build();
        robot.setUid(uidUtils.getCacheSerialUid());
        robot.setOrgUid(orgUid);
        robot.setNickname(I18Consts.I18N_ROBOT_NICKNAME);
        robot.setAvatar(AvatarConsts.DEFAULT_AVATAR_URL);
        //
        robotService.save(robot);
    }



}
