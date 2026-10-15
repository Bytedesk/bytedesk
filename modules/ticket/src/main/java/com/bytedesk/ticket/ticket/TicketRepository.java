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
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import jakarta.persistence.LockModeType;

public interface TicketRepository extends JpaRepository<TicketEntity, Long>, JpaSpecificationExecutor<TicketEntity> {

    Optional<TicketEntity> findByUid(String uid);

    /**
     * 悲观行锁读取工单：用于分配阶段互斥（创建监听与会话补分配竞争同一工单时，
     * 持锁重读后再判断是否需要选人/写入，避免双写处理人）。
     * H2/MySQL/PostgreSQL/Oracle 均支持 SELECT ... FOR UPDATE。
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from bytedesk_ticket t where t.uid = :uid")
    Optional<TicketEntity> findByUidForAssignment(@Param("uid") String uid);

    /**
     * 可见性列表过滤：一次性取本组织（可按类型缩小）未删除工单引用的全部设置 uid。
     * 用于按“工单自身绑定设置”的可见性构建组合谓词，避免逐工单访问设置。
     */
    @Query("select distinct t.ticketSettingsUid from bytedesk_ticket t "
            + "where t.orgUid = :orgUid and t.deleted = false "
            + "and (:type is null or t.type = :type) "
            + "and t.ticketSettingsUid is not null and t.ticketSettingsUid <> ''")
    List<String> findDistinctTicketSettingsUids(@Param("orgUid") String orgUid, @Param("type") String type);

    Optional<TicketEntity> findByTicketNumber(String ticketNumber);

    boolean existsByTicketNumber(String ticketNumber);

    Optional<TicketEntity> findByOrgUidAndTicketNumber(String orgUid, String ticketNumber);

    boolean existsByOrgUidAndTicketNumber(String orgUid, String ticketNumber);

    Optional<TicketEntity> findByProcessInstanceId(String processInstanceId);

    Optional<TicketEntity> findFirstByOrgUidAndThreadUidOrderByCreatedAtDesc(String orgUid, String threadUid);

    Page<TicketEntity> findByOrgUidAndThreadTopic(String orgUid, String threadTopic, Pageable pageable);

    Page<TicketEntity> findByOrgUidAndVisitorThreadUid(String orgUid, String visitorThreadUid, Pageable pageable);

    Page<TicketEntity> findByOrgUidAndVisitorThreadTopic(String orgUid, String visitorThreadTopic, Pageable pageable);

    Slice<TicketStatisticRow> findByOrgUidAndWorkgroupUidContainingAndDeletedFalseAndCreatedAtBetween(
        String orgUid, String workgroupUid, ZonedDateTime startTime, ZonedDateTime endTime, Pageable pageable);

    Slice<TicketStatisticRow> findByOrgUidAndDepartmentUidAndDeletedFalseAndCreatedAtBetween(
            String orgUid, String departmentUid, ZonedDateTime startTime, ZonedDateTime endTime, Pageable pageable);

    Slice<TicketStatisticRow> findByOrgUidAndAssigneeContainingAndDeletedFalseAndCreatedAtBetween(
        String orgUid, String assigneeUid, ZonedDateTime startTime, ZonedDateTime endTime, Pageable pageable);

    Slice<TicketStatisticRow> findByOrgUidAndDeletedFalseAndCreatedAtBetween(
        String orgUid, ZonedDateTime startTime, ZonedDateTime endTime, Pageable pageable);

    long countByStatus(String status);
    long countByStatusNot(String status);

    long countByOrgUidAndStatusAndDeletedFalse(String orgUid, String status);

    long countByOrgUidAndWorkgroupUidAndStatusAndDeletedFalse(String orgUid, String workgroupUid, String status);

    long countByOrgUidAndDepartmentUidAndStatusAndDeletedFalse(String orgUid, String departmentUid, String status);

    /** Count active tickets for a member (assigned, not closed/cancelled) */
    long countByAssigneeContainingAndStatusNotAndStatusNot(String assigneeUid, String status1, String status2);

    /** Find most recently created ticket in a department that has a non-empty assignee */
    Optional<TicketEntity> findFirstByDepartmentUidAndAssigneeNotAndAssigneeNotOrderByCreatedAtDesc(
            String departmentUid, String emptyJson, String emptyJson2);

    /**
     * Find most recently created ticket in an organization that has a non-empty assignee.
     * 用于内部工单未选部门时的轮询/最近分配回退（department_uid 为空的工单）。
     */
    Optional<TicketEntity> findFirstByOrgUidAndDepartmentUidIsNullAndAssigneeNotAndAssigneeNotOrderByCreatedAtDesc(
            String orgUid, String emptyJson, String emptyJson2);
} 