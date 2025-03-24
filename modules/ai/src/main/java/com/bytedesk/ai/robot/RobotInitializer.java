/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-24 17:00:42
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
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@AllArgsConstructor
public class RobotInitializer implements SmartInitializingSingleton {

    private final RobotRestService robotService;

    private final AuthorityRestService authorityService;
    
    @Override
    public void afterSingletonsInstantiated() {
        initRobot();
        initPermissions();
    }
    
    private void initRobot() {
        // 导入平台智能体
        robotService.initRobotJson();
        // 
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        String robotUid = BytedeskConsts.DEFAULT_ROBOT_UID; // 默认机器人UID
        // 为初始组织创建一个机器人
        robotService.initDefaultRobot(orgUid, robotUid);
    }

    private void initPermissions() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = RobotPermissions.ROBOT_PREFIX + permission.name();
            authorityService.createForPlatform(permissionValue);
        }
    }
}