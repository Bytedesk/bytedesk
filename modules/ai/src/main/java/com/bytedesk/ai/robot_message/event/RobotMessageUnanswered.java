/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-13 22:44:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-13 22:46:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message.event;

import java.time.Clock;

import org.springframework.context.ApplicationEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RobotMessageUnanswered extends ApplicationEvent {

    private final RobotMessage 

    public RobotMessageUnanswered(Object source, Clock clock) {
        super(source, clock);
        //TODO Auto-generated constructor stub
    }

    
    
}
