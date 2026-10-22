/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-11 11:15:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 15:05:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgentStatusRepository extends JpaRepository<AgentStatusEntity, Long>, JpaSpecificationExecutor<AgentStatusEntity> {

    Optional<AgentStatusEntity> findByUid(String uid);

    List<AgentStatusDashboardRow> findByOrgUidAndDeletedFalseAndCreatedAtBetween(String orgUid, ZonedDateTime startTime, ZonedDateTime endTime);

    // 实体列表查询：用于按客服分组统计在线/挂起时长（需要 agent JSON 中的客服 uid）
    List<AgentStatusEntity> findEntityByOrgUidAndDeletedFalseAndCreatedAtBetween(String orgUid,
                    ZonedDateTime startTime,
                    ZonedDateTime endTime);

    /**
     * 统计窗口状态日志：包含 [startTime, endTime] 内全部日志，
     * 以及每个客服在 startTime 之前最近一条（用于构建跨入查询范围的状态区间）。
     * 按 agent 分组取各自最新一条；多取的历史记录会在区间裁剪时被自然排除。
     */
    @Query("SELECT s FROM AgentStatusEntity s " +
                    "WHERE s.orgUid = :orgUid AND s.deleted = false " +
                    "AND s.createdAt < :endTime " +
                    "AND (s.createdAt >= :startTime OR s.createdAt IN (" +
                    "SELECT MAX(s2.createdAt) FROM AgentStatusEntity s2 " +
                    "WHERE s2.orgUid = :orgUid AND s2.deleted = false AND s2.createdAt < :startTime " +
                    "GROUP BY s2.agent)) " +
                    "ORDER BY s.createdAt ASC")
    List<AgentStatusEntity> findEntityByOrgUidAndDeletedFalseForStatisticWindow(
                    @Param("orgUid") String orgUid,
                    @Param("startTime") ZonedDateTime startTime,
                    @Param("endTime") ZonedDateTime endTime);

    // 修改为根据agent中是否含有uid进行查询
    List<AgentStatusEntity> findByAgentContainsAndCreatedAtBetween(String agentUid, ZonedDateTime startTime, ZonedDateTime endTime);
    // List<AgentStatusEntity> findByAgentUidAndCreatedAtBetween(String agentUid, ZonedDateTime startTime, ZonedDateTime endTime);

    List<AgentStatusEntity> findByOrgUid(String orgUid);

    Optional<AgentStatusEntity> findFirstByAgentContainsAndOrgUidAndDeletedFalseOrderByCreatedAtDesc(String agentUid, String orgUid);
}
