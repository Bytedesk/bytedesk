/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 10:09:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-20 11:36:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import java.time.LocalDateTime;

public interface QueueMemberRepository extends JpaRepository<QueueMemberEntity, Long>, JpaSpecificationExecutor<QueueMemberEntity> {

    Optional<QueueMemberEntity> findByUid(String uid);

    Optional<QueueMemberEntity> findByThreadUid(String threadUid);

    List<QueueMemberEntity> findByStatus(String status);

    List<QueueMemberEntity> findByStatusOrderByPriorityDesc(String status);

    int countByQueueUidAndStatus(String queueUid, String status);

    @Query("SELECT COUNT(m) FROM QueueMemberEntity m " +
           "WHERE m.queueUid = :queueUid AND m.priority > :priority " +
           "AND m.status = 'WAITING'")
    int countByQueueUidAndPriorityGreaterThan(
        @Param("queueUid") String queueUid, 
        @Param("priority") int priority);

    @Query("SELECT COUNT(m) FROM QueueMemberEntity m " +
           "WHERE m.queueUid = :queueUid " +
           "AND m.status IN ('COMPLETED', 'CANCELLED', 'TIMEOUT', 'REJECTED')")
    int countByQueueUidAndEndStatusIsTrue(@Param("queueUid") String queueUid);

    @Query("SELECT AVG(TIMESTAMPDIFF(SECOND, m.enqueueTime, " +
           "CASE WHEN m.acceptTime IS NULL THEN CURRENT_TIMESTAMP ELSE m.acceptTime END)) " +
           "FROM QueueMemberEntity m " +
           "WHERE m.queueUid = :queueUid AND m.status = 'WAITING'")
    Double calculateAverageWaitTime(@Param("queueUid") String queueUid);

    // 按工作组查询
    List<QueueMemberEntity> findByQueueUidAndStatus(String queueUid, String status);

    // 按优先级查询
    List<QueueMemberEntity> findByQueueUidOrderByPriorityDesc(String queueUid);

    // 按客服查询
    List<QueueMemberEntity> findByAgentUid(String agentUid);

    // 按访客查询
    List<QueueMemberEntity> findByVisitorUid(String visitorUid);

    // 统计相关查询
    @Query("SELECT new map(" +
           "COUNT(m) as total, " +
           "SUM(CASE WHEN m.status = 'WAITING' THEN 1 ELSE 0 END) as waiting, " +
           "SUM(CASE WHEN m.status = 'PROCESSING' THEN 1 ELSE 0 END) as processing, " +
           "AVG(TIMESTAMPDIFF(SECOND, m.enqueueTime, " +
           "CASE WHEN m.acceptTime IS NULL THEN CURRENT_TIMESTAMP ELSE m.acceptTime END)) as avgWaitTime) " +
           "FROM QueueMemberEntity m " +
           "WHERE m.queueUid = :queueUid")
    Map<String, Object> getQueueStats(@Param("queueUid") String queueUid);

    // 清理过期数据
    @Query("DELETE FROM QueueMemberEntity m " +
           "WHERE m.queueUid = :queueUid " +
           "AND m.closeTime < :beforeTime " +
           "AND m.status IN ('COMPLETED', 'CANCELLED', 'TIMEOUT', 'REJECTED')")
    @Modifying
    int cleanupExpiredMembers(
        @Param("queueUid") String queueUid,
        @Param("beforeTime") LocalDateTime beforeTime);
}
