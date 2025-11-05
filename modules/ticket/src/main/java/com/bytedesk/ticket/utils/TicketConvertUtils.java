/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-20 14:38:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-26 10:20:34
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
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.StringUtils;

import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.ticket.process.TicketProcessResponse;
import com.bytedesk.ticket.process.TicketProcessRestService;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketResponse;

@Slf4j
@UtilityClass
public class TicketConvertUtils {

    private static ModelMapper getModelMapper() {
        return ApplicationContextHolder.getBean(ModelMapper.class);
    }

    public static TicketResponse convertToResponse(TicketEntity entity) {
        // 
        TicketResponse ticketResponse = getModelMapper().map(entity, TicketResponse.class);
        // 
        // if (StringUtils.hasText(entity.getUserString())) {
        //     UserProtobuf user = entity.getUser();
        //     ticketResponse.setUser(user);
        // }
        // 
        // if (StringUtils.hasText(entity.getDepartmentString())) {
        //     UserProtobuf workgroup = entity.getDepartment();
        //     ticketResponse.setWorkgroup(workgroup);
        // }
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
        // 加载关联的流程实体
        if (StringUtils.hasText(entity.getProcessEntityUid())) {
            try {
                TicketProcessRestService processRestService = ApplicationContextHolder.getBean(TicketProcessRestService.class);
                processRestService.findByUid(entity.getProcessEntityUid()).ifPresent(processEntity -> {
                    TicketProcessResponse processResponse = TicketProcessResponse.builder()
                        .name(processEntity.getName())
                        .key(processEntity.getKey())
                        .description(processEntity.getDescription())
                        .status(processEntity.getStatus())
                        .schema(processEntity.getSchema())
                        .deploymentId(processEntity.getDeploymentId())
                        .build();
                    processResponse.setUid(processEntity.getUid());
                    processResponse.setCreatedAt(processEntity.getCreatedAt());
                    processResponse.setUpdatedAt(processEntity.getUpdatedAt());
                    ticketResponse.setProcessEntity(processResponse);
                });
            } catch (Exception e) {
                // 如果获取流程失败,不影响工单查询
                log.warn("Failed to load process entity for ticket {}: {}", entity.getUid(), e.getMessage());
            }
        }
        // 
        return ticketResponse;
        
    }
    
    
}
