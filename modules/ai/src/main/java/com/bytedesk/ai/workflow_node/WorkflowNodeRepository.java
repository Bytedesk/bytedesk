/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-20 12:52:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow_node;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bytedesk.ai.workflow.WorkflowEntity;

public interface WorkflowNodeRepository extends JpaRepository<WorkflowNodeEntity, Long>, JpaSpecificationExecutor<WorkflowNodeEntity> {

    Optional<WorkflowNodeEntity> findByUid(String uid);

    Boolean existsByUid(String uid);

    Optional<WorkflowNodeEntity> findByNameAndOrgUidAndTypeAndDeletedFalse(String name, String orgUid, String type);

    // === 新增的节点管理方法 ===
    
    /**
     * 根据节点UID查找节点（使用继承的uid字段）
     */
    Optional<WorkflowNodeEntity> findByUidAndDeletedFalse(String uid);

    /**
     * 检查节点UID是否存在（使用继承的uid字段）
     */
    boolean existsByUidAndDeletedFalse(String uid);

    /**
     * 根据工作流查找所有节点，按执行顺序排序
     */
    List<WorkflowNodeEntity> findByWorkflowOrderBySortOrderAsc(WorkflowEntity workflow);

    /**
     * 根据工作流和节点类型查找节点
     */
    List<WorkflowNodeEntity> findByWorkflowAndType(WorkflowEntity workflow, String type);

    /**
     * 根据工作流查找启用的节点，按执行顺序排序
     */
    List<WorkflowNodeEntity> findByWorkflowAndEnabledTrueOrderBySortOrderAsc(WorkflowEntity workflow);

    /**
     * 根据工作流和状态查找节点
     */
    List<WorkflowNodeEntity> findByWorkflowAndStatus(WorkflowEntity workflow, String status);

    /**
     * 根据工作流和父节点UID查找子节点
     */
    List<WorkflowNodeEntity> findByWorkflowAndParentNodeUid(WorkflowEntity workflow, String parentNodeUid);

    /**
     * 查找工作流的根节点（没有父节点的节点）
     */
    List<WorkflowNodeEntity> findByWorkflowAndParentNodeUidIsNull(WorkflowEntity workflow);

    /**
     * 统计工作流中特定类型的节点数量
     */
    long countByWorkflowAndType(WorkflowEntity workflow, String type);

    /**
     * 统计工作流中特定状态的节点数量
     */
    long countByWorkflowAndStatus(WorkflowEntity workflow, String status);

    /**
     * 查找工作流中执行失败的节点
     */
    @Query("SELECT n FROM WorkflowNodeEntity n WHERE n.workflow = :workflow AND n.status = 'FAIL'")
    List<WorkflowNodeEntity> findFailedNodes(@Param("workflow") WorkflowEntity workflow);

    /**
     * 查找工作流中正在执行的节点
     */
    @Query("SELECT n FROM WorkflowNodeEntity n WHERE n.workflow = :workflow AND n.status = 'PROCESSING'")
    List<WorkflowNodeEntity> findProcessingNodes(@Param("workflow") WorkflowEntity workflow);

    /**
     * 查找工作流中已完成的节点
     */
    @Query("SELECT n FROM WorkflowNodeEntity n WHERE n.workflow = :workflow AND n.status = 'SUCCESS'")
    List<WorkflowNodeEntity> findCompletedNodes(@Param("workflow") WorkflowEntity workflow);

    /**
     * 根据工作流和节点名称查找节点
     */
    Optional<WorkflowNodeEntity> findByWorkflowAndName(WorkflowEntity workflow, String name);

    /**
     * 查找工作流中的开始节点
     */
    @Query("SELECT n FROM WorkflowNodeEntity n WHERE n.workflow = :workflow AND n.type = 'start' AND n.enabled = true")
    List<WorkflowNodeEntity> findStartNodes(@Param("workflow") WorkflowEntity workflow);

    /**
     * 查找工作流中的结束节点
     */
    @Query("SELECT n FROM WorkflowNodeEntity n WHERE n.workflow = :workflow AND n.type = 'end' AND n.enabled = true")
    List<WorkflowNodeEntity> findEndNodes(@Param("workflow") WorkflowEntity workflow);

    /**
     * 查找指定时间范围内执行的节点
     */
    @Query("SELECT n FROM WorkflowNodeEntity n WHERE n.workflow = :workflow " +
           "AND n.executionStartTime >= :startTime AND n.executionEndTime <= :endTime")
    List<WorkflowNodeEntity> findNodesExecutedBetween(
            @Param("workflow") WorkflowEntity workflow,
            @Param("startTime") java.time.ZonedDateTime startTime,
            @Param("endTime") java.time.ZonedDateTime endTime);

    /**
     * 删除工作流的所有节点
     */
    void deleteByWorkflow(WorkflowEntity workflow);

    /**
     * 查找需要重试的节点（失败且允许重试的）
     */
    @Query("SELECT n FROM WorkflowNodeEntity n WHERE n.workflow = :workflow " +
           "AND n.status = 'FAIL' AND n.enabled = true")
    List<WorkflowNodeEntity> findRetryableNodes(@Param("workflow") WorkflowEntity workflow);

    /**
     * 获取工作流执行统计信息
     */
    @Query("SELECT n.status, COUNT(n) FROM WorkflowNodeEntity n WHERE n.workflow = :workflow GROUP BY n.status")
    List<Object[]> getWorkflowExecutionStatistics(@Param("workflow") WorkflowEntity workflow);

    /**
     * 查找超时的执行节点
     */
    @Query("SELECT n FROM WorkflowNodeEntity n WHERE n.status = 'PROCESSING' " +
           "AND n.executionStartTime < :timeoutBefore")
    List<WorkflowNodeEntity> findTimeoutNodes(@Param("timeoutBefore") java.time.ZonedDateTime timeoutBefore);

    // Boolean existsByPlatform(String platform);
}
