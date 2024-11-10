/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 10:50:24
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
import java.util.Map;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.model.LlmModelService;
import com.bytedesk.ai.provider.LlmProviderService;
import com.bytedesk.ai.robot.RobotJsonService.ModelJson;
import com.bytedesk.ai.robot.RobotJsonService.ProviderJson;
import com.bytedesk.ai.robot.RobotJsonService.RobotJson;
import com.bytedesk.core.constant.BytedeskConsts;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RobotInitializer  implements SmartInitializingSingleton {

    private final RobotService robotService;

    private final RobotJsonService robotJsonService;

    private final LlmProviderService llmProviderService;

    private final LlmModelService llmModelService;

    @Override
    public void afterSingletonsInstantiated() {
        init();
    }

    // @PostConstruct
    public void init() {
        //
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        if (!robotService.existsByUid(BytedeskConsts.DEFAULT_ROBOT_UID)) {
            robotService.createDefaultRobot(orgUid, BytedeskConsts.DEFAULT_ROBOT_UID);
        }
        if (!robotService.existsByUid(BytedeskConsts.DEFAULT_AGENT_ASSISTANT_UID)) {
            robotService.createDefaultAgentAssistantRobot(orgUid, BytedeskConsts.DEFAULT_AGENT_ASSISTANT_UID);
        }
        //
        Map<String, ProviderJson> providerJsonMap = robotJsonService.loadProviders();
        for (Map.Entry<String, ProviderJson> entry : providerJsonMap.entrySet()) {
            String providerName = entry.getKey();
            ProviderJson providerJson = entry.getValue();
            if (!llmProviderService.existsByName(providerName)) {
                llmProviderService.createFromProviderJson(providerName, providerJson);
            }
        }
        //
        Map<String, List<ModelJson>> modelJsonMap = robotJsonService.loadModels();
        for (Map.Entry<String, List<ModelJson>> entry : modelJsonMap.entrySet()) {
            String providerName = entry.getKey();
            List<ModelJson> modelJsons = entry.getValue();
            for (ModelJson modelJson : modelJsons) {
                if (!llmModelService.existsByUid(modelJson.getUid())) {
                    llmModelService.createFromModelJson(providerName, modelJson);
                }
            }
        }
        //
        List<RobotJson> robotJsons = robotJsonService.loadRobots();
        for (RobotJson robotJson : robotJsons) {
            if (!robotService.existsByUid(robotJson.getUid())) {
                robotService.createRobotFromJson(robotJson);
            }
        }
    }
    
}
