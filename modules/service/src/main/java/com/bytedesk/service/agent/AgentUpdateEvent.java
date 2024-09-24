/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-27 15:39:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-18 15:11:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import org.springframework.context.ApplicationEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AgentUpdateEvent extends ApplicationEvent {

    private final static long serialVersionUID = 1L;

    private Agent agent;

    private String updateType;

    public AgentUpdateEvent(Object source, Agent agent, String updateType) {
        super(source);
        this.agent = agent;
        this.updateType = updateType;
    }
}