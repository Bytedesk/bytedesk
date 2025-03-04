/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-20 14:38:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 09:05:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.utils;

import org.modelmapper.ModelMapper;

import org.springframework.util.StringUtils;

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketResponse;

public class TicketConvertUtils {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static TicketResponse convertToResponse(TicketEntity entity) {
        // 
        TicketResponse ticketResponse = modelMapper.map(entity, TicketResponse.class);
        // 
        if (StringUtils.hasText(entity.getUserString())) {
            UserProtobuf user = entity.getUser();
            ticketResponse.setUser(user);
        }
        // 
        if (StringUtils.hasText(entity.getWorkgroupString())) {
            UserProtobuf workgroup = entity.getWorkgroup();
            ticketResponse.setWorkgroup(workgroup);
        }
        // 
        if (StringUtils.hasText(entity.getAssigneeString())) {
            UserProtobuf assignee = entity.getAssignee();
            ticketResponse.setAssignee(assignee);
        }
        // 
        if (StringUtils.hasText(entity.getReporterString())) {
            UserProtobuf reporter = entity.getReporter();
            ticketResponse.setReporter(reporter);
        }
        // 
        return ticketResponse;
        
    }
    
    
}
