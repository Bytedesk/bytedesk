/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-13 18:16:47
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

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.robot.RobotJsonService.RobotJson;
import com.bytedesk.core.category.CategoryConsts;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RobotInitializer implements SmartInitializingSingleton {

    private final RobotRestService robotService;

    private final RobotJsonService robotJsonService;

    private final CategoryRestService categoryService;

    @Override
    public void afterSingletonsInstantiated() {
        initRobot();
        // initRobotCategory();
        initRobotJson();
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
    }

    // private void initRobotCategory() {
    //     //
    //     String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
    //     String[] categories = {
    //         RobotConsts.CATEGORY_JOB,
    //         RobotConsts.CATEGORY_LANGUAGE,
    //         RobotConsts.CATEGORY_TOOL,
    //         RobotConsts.CATEGORY_WRITING,
    //         RobotConsts.CATEGORY_RAG
    //     };
    //     for (String category : categories) {
    //         if (!categoryService.existsByNameAndOrgUidAndDeletedFalse(category, orgUid)) {
    //             CategoryRequest categoryRequest = CategoryRequest.builder()
    //                  .name(category)
    //                  .orderNo(0)
    //                  .level(LevelEnum.PLATFORM.name())
    //                  .platform(BytedeskConsts.PLATFORM_BYTEDESK)
    //                  .build();
    //             categoryRequest.setType(CategoryConsts.CATEGORY_TYPE_ROBOT);
    //             categoryRequest.setOrgUid(orgUid);
    //             categoryService.create(categoryRequest);
    //         }
    //     }
    // }

    private void initRobotJson() {
        // 
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        List<RobotJson> robotJsons = robotJsonService.loadRobots();
        for (RobotJson robotJson : robotJsons) {
            if (!robotService.existsByUid(robotJson.getUid())) {
                CategoryResponse categoryResponse = null;
                Optional<CategoryEntity> categoryOptional = categoryService.findByNameAndTypeAndOrgUidAndPlatform(robotJson.getCategory(), CategoryConsts.CATEGORY_TYPE_ROBOT, orgUid, BytedeskConsts.PLATFORM_BYTEDESK);
                if (!categoryOptional.isPresent()) {
                    CategoryRequest categoryRequest = CategoryRequest.builder()
                     .name(robotJson.getCategory())
                     .orderNo(0)
                     .level(LevelEnum.PLATFORM.name())
                     .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                     .build();
                    categoryRequest.setType(CategoryConsts.CATEGORY_TYPE_ROBOT);
                    categoryRequest.setOrgUid(orgUid);
                    categoryResponse = categoryService.create(categoryRequest);
                } else {
                    categoryResponse = categoryService.convertToResponse(categoryOptional.get());
                }
                robotService.createRobotFromJson(robotJson, categoryResponse.getUid());
            }
        }
    }

    
}
