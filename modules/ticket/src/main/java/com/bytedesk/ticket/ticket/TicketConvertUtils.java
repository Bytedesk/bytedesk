/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 22:30:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-17 22:32:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import org.modelmapper.ModelMapper;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.rbac.user.UserProtobuf;


public class TicketConvertUtils {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static TicketResponse convertToResponse(TicketEntity entity) {
        // 
        TicketResponse ticketResponse = modelMapper.map(entity, TicketResponse.class);
        // 
        if (StringUtils.hasText(entity.getUser())) {
            UserProtobuf user = JSON.parseObject(entity.getUser(), UserProtobuf.class);
            ticketResponse.setUser(user);
        }
        // 
        if (StringUtils.hasText(entity.getWorkgroup())) {
            UserProtobuf workgroup = JSON.parseObject(entity.getWorkgroup(), UserProtobuf.class);
            ticketResponse.setWorkgroup(workgroup);
        }
        // 
        if (StringUtils.hasText(entity.getAssignee())) {
            UserProtobuf assignee = JSON.parseObject(entity.getAssignee(), UserProtobuf.class);
            ticketResponse.setAssignee(assignee);
        }
        // 
        if (StringUtils.hasText(entity.getReporter())) {
            UserProtobuf reporter = JSON.parseObject(entity.getReporter(), UserProtobuf.class);
            ticketResponse.setReporter(reporter);
        }
        // 
        return ticketResponse;
        
    }
    
}
