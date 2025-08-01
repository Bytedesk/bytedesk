/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-13 22:44:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-13 23:18:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.ai.robot_message.RobotMessageEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class RobotMessageUnanswered extends ApplicationEvent {

    private static final long serialVersionUID = 1L; 

    private final RobotMessageEntity robotMessage;

    public RobotMessageUnanswered(Object source, RobotMessageEntity robotMessage) {
        super(source);
        this.robotMessage = robotMessage;
    }

}
