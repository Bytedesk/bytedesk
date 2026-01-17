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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bytedesk.service.queue.QueueEntity;

import jakarta.persistence.LockModeType;

public interface QueueMemberRepository
                extends JpaRepository<QueueMemberEntity, Long>, JpaSpecificationExecutor<QueueMemberEntity> {

        /**
         * 仅查询用于“客服响应时长统计”的必要字段，避免加载完整实体。
         */
        @Query("SELECT qm.visitorFirstMessageAt, qm.agentFirstResponseAt, qm.agentAvgResponseLength " +
               "FROM QueueMemberEntity qm " +
               "WHERE qm.agentQueue.uid = :agentQueueUid AND qm.deleted = false")
        List<Object[]> findAgentResponseStatsRows(@Param("agentQueueUid") String agentQueueUid);

        Optional<QueueMemberEntity> findByUid(String uid);

        List<QueueMemberEntity> findByOrgUidAndCreatedAtBetweenAndResolved(String orgUid, ZonedDateTime startTime,
                        ZonedDateTime endTime, boolean resolved);

        List<QueueMemberEntity> findByOrgUidAndCreatedAtBetweenAndAgentAcceptType(String orgUid,
                        ZonedDateTime startTime,
                        ZonedDateTime endTime, String acceptType);

        // 修改查询方法，使用 JPQL 通过关联的 Thread 实体的 uid 字段查询
        @Query("SELECT qm FROM QueueMemberEntity qm WHERE qm.thread.uid = :threadUid AND qm.deleted = false")
        Optional<QueueMemberEntity> findByThreadUid(@Param("threadUid") String threadUid);

        // 批量查询：用于队列列表展示（减少 N+1）
        @Query("SELECT qm FROM QueueMemberEntity qm JOIN FETCH qm.thread t WHERE t.uid IN :threadUids AND qm.deleted = false")
        List<QueueMemberEntity> findByThreadUids(@Param("threadUids") List<String> threadUids);

        // 统计指定组织在指定日期范围内的会话总数
        @Query("SELECT COUNT(qm) FROM QueueMemberEntity qm WHERE qm.orgUid = :orgUid AND qm.createdAt >= :startDate AND qm.createdAt <= :endDate")
        Long countByOrgUidAndDateBetween(@Param("orgUid") String orgUid, @Param("startDate") ZonedDateTime startDate,
                        @Param("endDate") ZonedDateTime endDate);

        // 统计指定工作组在指定日期范围内的会话总数
        @Query("SELECT COUNT(qm) FROM QueueMemberEntity qm WHERE qm.orgUid = :orgUid AND qm.workgroupQueue IS NOT NULL AND qm.createdAt >= :startDate AND qm.createdAt <= :endDate")
        Long countByWorkgroupUidAndDateBetween(@Param("orgUid") String orgUid,
                        @Param("workgroupUid") String workgroupUid,
                        @Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);

        // 统计指定客服在指定日期范围内的会话总数
        @Query("SELECT COUNT(qm) FROM QueueMemberEntity qm WHERE qm.thread.agent LIKE CONCAT('%', :agentUid, '%') AND qm.createdAt >= :startDate AND qm.createdAt <= :endDate")
        Long countByAgentUidAndDateBetween(@Param("agentUid") String agentUid,
                        @Param("startDate") ZonedDateTime startDate,
                        @Param("endDate") ZonedDateTime endDate);

        // 统计指定机器人在指定日期范围内的会话总数
        @Query("SELECT COUNT(qm) FROM QueueMemberEntity qm WHERE qm.orgUid = :orgUid AND qm.robotQueue IS NOT NULL AND qm.createdAt >= :startDate AND qm.createdAt <= :endDate")
        Long countByRobotUidAndDateBetween(@Param("orgUid") String orgUid, @Param("robotUid") String robotUid,
                        @Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);

        /**
         * 查找在指定时间之前仍未发送任何访客消息(visitorMessageCount=0)的排队成员
         */
        @Query("SELECT qm FROM QueueMemberEntity qm WHERE qm.visitorMessageCount = 0 AND qm.deleted = false AND qm.visitorEnqueueAt < :threshold")
        List<QueueMemberEntity> findIdleBefore(@Param("threshold") ZonedDateTime threshold);

        @Query("SELECT qm FROM QueueMemberEntity qm WHERE qm.agentQueue.uid = :agentQueueUid AND qm.deleted = false AND qm.thread.status = :threadStatus ORDER BY qm.queueNumber ASC")
        Optional<QueueMemberEntity> findFirstAgentQueueMemberByThreadStatus(
                        @Param("agentQueueUid") String agentQueueUid, @Param("threadStatus") String threadStatus);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT qm FROM QueueMemberEntity qm WHERE qm.agentQueue.uid = :agentQueueUid AND qm.deleted = false AND qm.thread.status = :threadStatus ORDER BY qm.queueNumber ASC")
        List<QueueMemberEntity> findAgentQueueHeadForUpdate(@Param("agentQueueUid") String agentQueueUid,
                        @Param("threadStatus") String threadStatus,
                        Pageable pageable);

        @Query("SELECT qm FROM QueueMemberEntity qm WHERE qm.workgroupQueue.uid = :workgroupQueueUid AND qm.deleted = false AND qm.thread.status = :threadStatus ORDER BY qm.queueNumber ASC")
        Optional<QueueMemberEntity> findFirstWorkgroupQueueMemberByThreadStatus(
                        @Param("workgroupQueueUid") String workgroupQueueUid, @Param("threadStatus") String threadStatus);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT qm FROM QueueMemberEntity qm WHERE qm.workgroupQueue.uid = :workgroupQueueUid AND qm.deleted = false AND qm.thread.status = :threadStatus ORDER BY qm.queueNumber ASC")
        List<QueueMemberEntity> findWorkgroupQueueHeadForUpdate(@Param("workgroupQueueUid") String workgroupQueueUid,
                        @Param("threadStatus") String threadStatus,
                        Pageable pageable);

        @Query("SELECT qm FROM QueueMemberEntity qm WHERE qm.workgroupQueue.uid = :workgroupQueueUid AND qm.deleted = false AND qm.thread.status = :threadStatus ORDER BY qm.queueNumber ASC")
        List<QueueMemberEntity> findWorkgroupQueueMembersByThreadStatus(
                        @Param("workgroupQueueUid") String workgroupQueueUid,
                        @Param("threadStatus") String threadStatus);

        @Query("SELECT qm FROM QueueMemberEntity qm WHERE qm.robotQueue.uid = :robotQueueUid AND qm.deleted = false AND qm.thread.status = :threadStatus ORDER BY qm.queueNumber ASC")
        Optional<QueueMemberEntity> findFirstRobotQueueMemberByThreadStatus(
                        @Param("robotQueueUid") String robotQueueUid, @Param("threadStatus") String threadStatus);

        @Query(value = "SELECT qm FROM QueueMemberEntity qm WHERE qm.agentQueue.uid = :agentQueueUid AND qm.deleted = false AND qm.thread.status = :threadStatus ORDER BY qm.queueNumber ASC",
                        countQuery = "SELECT COUNT(qm) FROM QueueMemberEntity qm WHERE qm.agentQueue.uid = :agentQueueUid AND qm.deleted = false AND qm.thread.status = :threadStatus")
        Page<QueueMemberEntity> findAgentQueueMembersByThreadStatus(
                        @Param("agentQueueUid") String agentQueueUid,
                        @Param("threadStatus") String threadStatus,
                        Pageable pageable);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT COALESCE(MAX(qm.queueNumber), 0) FROM QueueMemberEntity qm WHERE qm.deleted = false AND ((:queueType = 'AGENT' AND qm.agentQueue = :queue) OR (:queueType = 'WORKGROUP' AND qm.workgroupQueue = :queue) OR (:queueType = 'ROBOT' AND qm.robotQueue = :queue))")
        Integer findMaxQueueNumberForQueue(@Param("queue") QueueEntity queue, @Param("queueType") String queueType);

        /**
         * 统计指定工作组队列UIDs中未分配客服（agentQueue 为 null）且处于排队状态的会话数
         * 用于计算某个客服所在工作组中等待分配的排队人数
         * 
         * @param workgroupQueueUids 工作组队列UID列表
         * @param threadStatus 线程状态（如 QUEUING）
         * @return 未分配客服的排队会话数
         */
        @Query("SELECT COUNT(qm) FROM QueueMemberEntity qm WHERE qm.workgroupQueue.uid IN :workgroupQueueUids AND qm.agentQueue IS NULL AND qm.deleted = false AND qm.thread.status = :threadStatus")
        int countUnassignedQueuingByWorkgroupQueueUids(
                        @Param("workgroupQueueUids") List<String> workgroupQueueUids,
                        @Param("threadStatus") String threadStatus);

        /**
         * 统计指定客服队列中处于排队状态的会话数
         * 
         * @param agentQueueUid 客服队列UID
         * @param threadStatus 线程状态（如 QUEUING）
         * @return 排队会话数
         */
        @Query("SELECT COUNT(qm) FROM QueueMemberEntity qm WHERE qm.agentQueue.uid = :agentQueueUid AND qm.deleted = false AND qm.thread.status = :threadStatus")
        int countQueuingByAgentQueueUid(
                        @Param("agentQueueUid") String agentQueueUid,
                        @Param("threadStatus") String threadStatus);

}
