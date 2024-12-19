/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-07 14:17:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-19 16:03:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.assignment;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bytedesk.service.agent.AgentEntity;

@Service 
public class SkillBasedAssignmentService {

    public AgentEntity selectAgent(List<AgentEntity> agents, List<String> requiredSkills) {
        // 1. 按技能匹配度筛选
        List<AgentEntity> qualified = agents.stream()
            .filter(a -> a.hasRequiredSkills(requiredSkills))
            .collect(Collectors.toList());
            
        // 2. 在匹配的客服中选择负载最小的
        return qualified.stream()
            .min(Comparator.comparingInt(AgentEntity::getCurrentThreadCount))
            .orElse(null);
    }
}