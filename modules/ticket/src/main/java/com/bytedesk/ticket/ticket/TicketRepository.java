/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 18:50:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-08 14:51:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketRepository extends JpaRepository<TicketEntity, Long>, JpaSpecificationExecutor<TicketEntity> {

    Optional<TicketEntity> findByUid(String uid);

    Optional<TicketEntity> findByProcessInstanceId(String processInstanceId);

    List<TicketEntity> findByWorkgroupUidContainingAndCreatedAtBetween(
        String workgroupUid, LocalDateTime startTime, LocalDateTime endTime);
        
    List<TicketEntity> findByDepartmentUidAndCreatedAtBetween(
            String departmentUid, LocalDateTime startTime, LocalDateTime endTime);

    List<TicketEntity> findByAssigneeContainingAndCreatedAtBetween(
        String assigneeUid, LocalDateTime startTime, LocalDateTime endTime);

    // orgUid, startTime, endTime
    List<TicketEntity> findByOrgUidAndCreatedAtBetween(
        String orgUid, LocalDateTime startTime, LocalDateTime endTime);

    long countByStatus(String status);
    long countByStatusNot(String status);
} 