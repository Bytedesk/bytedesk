/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-21 14:23:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 17:03:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * 智能工单填写、智能小结、智能质检
 * @Author: jackning
 */
@Service
@RequiredArgsConstructor
public class RobotAgentService {

    private final RobotRestService robotRestService;

    public RobotRequest autoFillTicket(RobotRequest request) {
        return request;
    }


    
}
