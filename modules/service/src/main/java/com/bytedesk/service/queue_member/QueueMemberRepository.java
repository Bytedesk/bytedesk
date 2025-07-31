/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 10:09:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-31 16:40:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Repository;

@Repository
public interface QueueMemberRepository extends JpaRepository<QueueMemberEntity, Long>, JpaSpecificationExecutor<QueueMemberEntity> {

    Optional<QueueMemberEntity> findByUid(String uid);

    List<QueueMemberEntity> findByOrgUidAndCreatedAtBetweenAndResolved(String orgUid, ZonedDateTime startTime, ZonedDateTime endTime, boolean resolved);
    
    List<QueueMemberEntity> findByOrgUidAndCreatedAtBetweenAndAgentAcceptType(String orgUid, ZonedDateTime startTime, ZonedDateTime endTime, String acceptType);
    // 修改查询方法，使用 JPQL 通过关联的 Thread 实体的 uid 字段查询
    @Query("SELECT qm FROM QueueMemberEntity qm WHERE qm.thread.uid = :threadUid")
    Optional<QueueMemberEntity> findByThreadUid(@Param("threadUid") String threadUid);

    // 统计指定组织在指定日期范围内的会话总数
    @Query("SELECT COUNT(qm) FROM QueueMemberEntity qm WHERE qm.orgUid = :orgUid AND qm.createdAt >= :startDate AND qm.createdAt <= :endDate")
    Long countByOrgUidAndDateBetween(@Param("orgUid") String orgUid, @Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);

    // 统计指定工作组在指定日期范围内的会话总数
    @Query("SELECT COUNT(qm) FROM QueueMemberEntity qm WHERE qm.orgUid = :orgUid AND qm.workgroupQueue IS NOT NULL AND qm.createdAt >= :startDate AND qm.createdAt <= :endDate")
    Long countByWorkgroupUidAndDateBetween(@Param("orgUid") String orgUid, @Param("workgroupUid") String workgroupUid, @Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);

    // 统计指定客服在指定日期范围内的会话总数
    @Query("SELECT COUNT(qm) FROM QueueMemberEntity qm WHERE qm.thread.agent LIKE CONCAT('%', :agentUid, '%') AND qm.createdAt >= :startDate AND qm.createdAt <= :endDate")
    Long countByAgentUidAndDateBetween(@Param("agentUid") String agentUid, @Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);

    // 统计指定机器人在指定日期范围内的会话总数
    @Query("SELECT COUNT(qm) FROM QueueMemberEntity qm WHERE qm.orgUid = :orgUid AND qm.robotQueue IS NOT NULL AND qm.createdAt >= :startDate AND qm.createdAt <= :endDate")
    Long countByRobotUidAndDateBetween(@Param("orgUid") String orgUid, @Param("robotUid") String robotUid, @Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);

}
