/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-28
 * @Description: Repository for TicketAssignmentLogEntity.
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ticket.ticket.assignment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * JPA repository for ticket assignment audit logs.
 */
public interface TicketAssignmentLogRepository extends JpaRepository<TicketAssignmentLogEntity, Long>,
        JpaSpecificationExecutor<TicketAssignmentLogEntity> {

    List<TicketAssignmentLogEntity> findByTicketUidOrderByCreatedAtDesc(String ticketUid);
}
