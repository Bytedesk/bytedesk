/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-24 21:34:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RobotInitializer implements SmartInitializingSingleton {

    private final RobotRestService robotService;
    
    @Override
    public void afterSingletonsInstantiated() {
        initRobot();
    }

    // @PostConstruct
    private void initRobot() {
        //
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        if (!robotService.existsByUid(BytedeskConsts.DEFAULT_ROBOT_UID)) {
            robotService.createDefaultRobot(orgUid, BytedeskConsts.DEFAULT_ROBOT_UID);
        }
        if (!robotService.existsByUid(BytedeskConsts.DEFAULT_AGENT_ASSISTANT_UID)) {
            robotService.createDefaultAgentAssistantRobot(orgUid, BytedeskConsts.DEFAULT_AGENT_ASSISTANT_UID);
        }
        // 
        String level = LevelEnum.ORGANIZATION.name();
        robotService.initRobotJson(orgUid, level);
        // 
    }   
}