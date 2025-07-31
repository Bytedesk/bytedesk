/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 10:09:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-08 11:52:54
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

//     List<QueueMemberEntity> findBySummaryStatus(String summaryStatus);

//     List<QueueMemberEntity> findBySummaryStatusOrderByPriorityDesc(String summaryStatus);
    
//     // 修改：使用queue.uid替代queueUid
//     @Query("SELECT COUNT(m) FROM QueueMemberEntity m WHERE m.queue.uid = :queueUid AND m.summaryStatus = :summaryStatus")
//     int countByQueueUidAndSummaryStatus(@Param("queueUid") String queueUid, @Param("summaryStatus") String summaryStatus);

//     @Query("SELECT COUNT(m) FROM QueueMemberEntity m " +
//            "WHERE m.queue.uid = :queueUid AND m.priority > :priority " +
//            "AND m.summaryStatus = 'WAITING'")
//     int countByQueueUidAndPriorityGreaterThan(
//         @Param("queueUid") String queueUid, 
//         @Param("priority") int priority);

//     @Query("SELECT COUNT(m) FROM QueueMemberEntity m " +
//            "WHERE m.queue.uid = :queueUid " +
//            "AND m.summaryStatus IN ('COMPLETED', 'CANCELLED', 'TIMEOUT', 'REJECTED')")
//     int countByQueueUidAndEndStatusIsTrue(@Param("queueUid") String queueUid);

//     @Query("SELECT AVG(TIMESTAMPDIFF(SECOND, m.enqueueTime, " +
//            "CASE WHEN m.acceptTime IS NULL THEN CURRENT_TIMESTAMP ELSE m.acceptTime END)) " +
//            "FROM QueueMemberEntity m " +
//            "WHERE m.queue.uid = :queueUid AND m.summaryStatus = 'WAITING'")
//     Double calculateAverageWaitTime(@Param("queueUid") String queueUid);

//     // 按工作组查询
//     @Query("SELECT m FROM QueueMemberEntity m WHERE m.queue.uid = :queueUid AND m.summaryStatus = :summaryStatus")
//     List<QueueMemberEntity> findByQueueUidAndSummaryStatus(@Param("queueUid") String queueUid, @Param("summaryStatus") String summaryStatus);

//     // 按优先级查询
//     @Query("SELECT m FROM QueueMemberEntity m WHERE m.queue.uid = :queueUid ORDER BY m.priority DESC")
//     List<QueueMemberEntity> findByQueueUidOrderByPriorityDesc(@Param("queueUid") String queueUid);

//     // 修改：使用JSON_EXTRACT从agent字段中提取uid
//     @Query(value = "SELECT qm FROM QueueMemberEntity qm WHERE qm.thread.agent LIKE CONCAT('%', :agentUid, '%')")
//     List<QueueMemberEntity> findByAgentUid(@Param("agentUid") String agentUid);
    
//     // 统计客服的活跃会话数
//     @Query(value = "SELECT COUNT(qm) FROM QueueMemberEntity qm WHERE qm.thread.agent LIKE CONCAT('%', :agentUid, '%') AND qm.summaryStatus = 'SERVING'")
//     int countChattingThreadsByAgent(@Param("agentUid") String agentUid);
    
//     // 获取客服的活跃会话列表
//     @Query(value = "SELECT qm.uid FROM QueueMemberEntity qm WHERE qm.thread.agent LIKE CONCAT('%', :agentUid, '%') AND qm.summaryStatus = 'SERVING'")
//     List<String> findChattingThreadsByAgent(@Param("agentUid") String agentUid);
    
//     // 获取会话分配的客服
//     @Query(value = "SELECT qm.thread.agent FROM QueueMemberEntity qm WHERE qm.uid = :threadUid")
//     String getAssignedAgent(@Param("threadUid") String threadUid);
    
//     // 更新会话分配的客服 - 由于是JSON格式，可能需要构建新的JSON
//     @Modifying
//     @Query(value = "UPDATE ThreadEntity t SET t.agent = :agentJson WHERE t.uid = (SELECT qm.thread.uid FROM QueueMemberEntity qm WHERE qm.uid = :threadUid)")
//     void updateAssignedAgent(@Param("threadUid") String threadUid, @Param("agentJson") String agentJson);
    
//     // 统计客服的总会话数
//     @Query(value = "SELECT COUNT(qm) FROM QueueMemberEntity qm WHERE qm.thread.agent LIKE CONCAT('%', :agentUid, '%')")
//     int countTotalThreadsByAgent(@Param("agentUid") String agentUid);
    
//     // 统计客服的已解决会话数
//     @Query(value = "SELECT COUNT(qm) FROM QueueMemberEntity qm WHERE qm.thread.agent LIKE CONCAT('%', :agentUid, '%') AND qm.summaryStatus = 'RESOLVED'")
//     int countResolvedThreadsByAgent(@Param("agentUid") String agentUid);

//     // 按访客查询 - 使用字符串匹配
//     @Query(value = "SELECT qm FROM QueueMemberEntity qm WHERE qm.thread.user LIKE CONCAT('%', :visitorUid, '%')")
//     List<QueueMemberEntity> findByVisitorUid(@Param("visitorUid") String visitorUid);

//     // 修改为使用字符串like查询
//     @Query(value = "SELECT qm FROM QueueMemberEntity qm WHERE qm.thread.user LIKE CONCAT('%', :visitorUid, '%')")
//     List<QueueMemberEntity> findByVisitorContainingUid(@Param("visitorUid") String visitorUid);

//     // 统计相关查询
//     @Query("SELECT new map(" +
//            "COUNT(m) as total, " +
//            "SUM(CASE WHEN m.summaryStatus = 'WAITING' THEN 1 ELSE 0 END) as waiting, " +
//            "SUM(CASE WHEN m.summaryStatus = 'PROCESSING' THEN 1 ELSE 0 END) as processing, " +
//            "AVG(TIMESTAMPDIFF(SECOND, m.enqueueTime, " +
//            "CASE WHEN m.acceptTime IS NULL THEN CURRENT_TIMESTAMP ELSE m.acceptTime END)) as avgWaitTime) " +
//            "FROM QueueMemberEntity m " +
//            "WHERE m.queue.uid = :queueUid")
//     Map<String, Object> getQueueStats(@Param("queueUid") String queueUid);

//     // 清理过期数据
//     @Query("DELETE FROM QueueMemberEntity m " +
//            "WHERE m.queue.uid = :queueUid " +
//            "AND m.closeTime < :beforeTime " +
//            "AND m.summaryStatus IN ('COMPLETED', 'CANCELLED', 'TIMEOUT', 'REJECTED')")
//     @Modifying
//     int cleanupExpiredMembers(
//         @Param("queueUid") String queueUid,
//         @Param("beforeTime") ZonedDateTime beforeTime);

//     // 添加新的查询方法，通过线程UID和队列UID查询队列成员
//     @Query("SELECT qm FROM QueueMemberEntity qm WHERE qm.thread.uid = :threadUid AND qm.queue.uid = :queueUid")
//     Optional<QueueMemberEntity> findByThreadUidAndQueueUid(@Param("threadUid") String threadUid, @Param("queueUid") String queueUid);
    
    // 查找指定队列下的所有队列成员
    // @Query("SELECT qm FROM QueueMemberEntity qm WHERE qm.queue.uid = :queueUid")
    // List<QueueMemberEntity> findByQueueUid(@Param("queueUid") String queueUid);
}
