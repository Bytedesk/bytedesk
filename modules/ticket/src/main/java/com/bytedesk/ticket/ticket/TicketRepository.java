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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketRepository extends JpaRepository<TicketEntity, Long>, JpaSpecificationExecutor<TicketEntity> {

    Optional<TicketEntity> findByUid(String uid);

    Optional<TicketEntity> findByTicketNumber(String ticketNumber);

    boolean existsByTicketNumber(String ticketNumber);

    Optional<TicketEntity> findByOrgUidAndTicketNumber(String orgUid, String ticketNumber);

    boolean existsByOrgUidAndTicketNumber(String orgUid, String ticketNumber);

    Optional<TicketEntity> findByProcessInstanceId(String processInstanceId);

    Optional<TicketEntity> findFirstByOrgUidAndThreadUidOrderByCreatedAtDesc(String orgUid, String threadUid);

    Page<TicketEntity> findByOrgUidAndThreadTopic(String orgUid, String threadTopic, Pageable pageable);

    Page<TicketEntity> findByOrgUidAndVisitorThreadUid(String orgUid, String visitorThreadUid, Pageable pageable);

    List<TicketEntity> findByWorkgroupUidContainingAndCreatedAtBetween(
        String workgroupUid, ZonedDateTime startTime, ZonedDateTime endTime);
        
    List<TicketEntity> findByDepartmentUidAndCreatedAtBetween(
            String departmentUid, ZonedDateTime startTime, ZonedDateTime endTime);

    List<TicketEntity> findByAssigneeContainingAndCreatedAtBetween(
        String assigneeUid, ZonedDateTime startTime, ZonedDateTime endTime);

    // orgUid, startTime, endTime
    List<TicketEntity> findByOrgUidAndCreatedAtBetween(
        String orgUid, ZonedDateTime startTime, ZonedDateTime endTime);

    long countByStatus(String status);
    long countByStatusNot(String status);

    long countByOrgUidAndStatusAndDeletedFalse(String orgUid, String status);

    long countByOrgUidAndWorkgroupUidAndStatusAndDeletedFalse(String orgUid, String workgroupUid, String status);

    long countByOrgUidAndDepartmentUidAndStatusAndDeletedFalse(String orgUid, String departmentUid, String status);
} 