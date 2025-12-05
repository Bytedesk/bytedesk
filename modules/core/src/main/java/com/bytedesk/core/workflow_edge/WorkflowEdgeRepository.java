/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-04 18:25:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow_edge;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bytedesk.core.workflow.WorkflowEntity;

/**
 * 工作流边数据访问层
 * 提供边的基础CRUD操作和复杂查询功能
 */
public interface WorkflowEdgeRepository extends JpaRepository<WorkflowEdgeEntity, Long>, JpaSpecificationExecutor<WorkflowEdgeEntity> {

    // ==================== 基础查询方法 ====================

    /**
     * 根据UID查找边
     */
    Optional<WorkflowEdgeEntity> findByUid(String uid);

    /**
     * 根据UID查找未删除的边
     */
    Optional<WorkflowEdgeEntity> findByUidAndDeletedFalse(String uid);

    /**
     * 检查UID是否存在
     */
    Boolean existsByUid(String uid);

    /**
     * 检查UID是否存在且未删除
     */
    Boolean existsByUidAndDeletedFalse(String uid);

    /**
     * 根据名称、组织UID和类型查找边
     */
    Optional<WorkflowEdgeEntity> findByNameAndOrgUidAndTypeAndDeletedFalse(String name, String orgUid, String type);

    // ==================== 工作流相关查询 ====================

    /**
     * 查找工作流的所有边
     */
    List<WorkflowEdgeEntity> findByWorkflowAndDeletedFalse(WorkflowEntity workflow);

    /**
     * 查找工作流中特定类型的边
     */
    List<WorkflowEdgeEntity> findByWorkflowAndTypeAndDeletedFalse(WorkflowEntity workflow, String type);

    /**
     * 查找工作流中启用的边
     */
    List<WorkflowEdgeEntity> findByWorkflowAndEnabledTrueAndDeletedFalse(WorkflowEntity workflow);

    /**
     * 查找工作流中禁用的边
     */
    List<WorkflowEdgeEntity> findByWorkflowAndEnabledFalseAndDeletedFalse(WorkflowEntity workflow);

    // ==================== 节点连接查询 ====================

    /**
     * 查找从指定节点出发的边
     */
    List<WorkflowEdgeEntity> findByWorkflowAndSourceNodeIdAndDeletedFalse(WorkflowEntity workflow, String sourceNodeId);

    /**
     * 查找到达指定节点的边
     */
    List<WorkflowEdgeEntity> findByWorkflowAndTargetNodeIdAndDeletedFalse(WorkflowEntity workflow, String targetNodeId);

    /**
     * 查找连接两个特定节点的边
     */
    List<WorkflowEdgeEntity> findByWorkflowAndSourceNodeIdAndTargetNodeIdAndDeletedFalse(
            WorkflowEntity workflow, String sourceNodeId, String targetNodeId);

    /**
     * 查找连接指定节点的所有边（作为源或目标）
     */
    @Query("SELECT e FROM WorkflowEdgeEntity e WHERE e.workflow = :workflow " +
           "AND (e.sourceNodeId = :nodeId OR e.targetNodeId = :nodeId) " +
           "AND e.deleted = false")
    List<WorkflowEdgeEntity> findEdgesConnectingNode(@Param("workflow") WorkflowEntity workflow, 
                                                     @Param("nodeId") String nodeId);

    // ==================== 端口连接查询 ====================

    /**
     * 查找从指定端口出发的边
     */
    List<WorkflowEdgeEntity> findByWorkflowAndSourceNodeIdAndSourcePortIdAndDeletedFalse(
            WorkflowEntity workflow, String sourceNodeId, String sourcePortId);

    /**
     * 查找到达指定端口的边
     */
    List<WorkflowEdgeEntity> findByWorkflowAndTargetNodeIdAndTargetPortIdAndDeletedFalse(
            WorkflowEntity workflow, String targetNodeId, String targetPortId);

    // ==================== 条件和权重查询 ====================

    /**
     * 查找有条件表达式的边
     */
    @Query("SELECT e FROM WorkflowEdgeEntity e WHERE e.workflow = :workflow " +
           "AND e.conditionExpression IS NOT NULL AND e.conditionExpression <> '' " +
           "AND e.deleted = false")
    List<WorkflowEdgeEntity> findConditionalEdges(@Param("workflow") WorkflowEntity workflow);

    /**
     * 查找指定权重范围的边
     */
    @Query("SELECT e FROM WorkflowEdgeEntity e WHERE e.workflow = :workflow " +
           "AND e.weight BETWEEN :minWeight AND :maxWeight " +
           "AND e.deleted = false ORDER BY e.weight ASC")
    List<WorkflowEdgeEntity> findEdgesByWeightRange(@Param("workflow") WorkflowEntity workflow,
                                                    @Param("minWeight") Integer minWeight,
                                                    @Param("maxWeight") Integer maxWeight);

    // ==================== 执行统计查询 ====================

    /**
     * 查找执行次数超过指定值的边
     */
    @Query("SELECT e FROM WorkflowEdgeEntity e WHERE e.workflow = :workflow " +
           "AND e.executionCount >= :minCount " +
           "AND e.deleted = false ORDER BY e.executionCount DESC")
    List<WorkflowEdgeEntity> findFrequentlyUsedEdges(@Param("workflow") WorkflowEntity workflow,
                                                     @Param("minCount") Long minCount);

    /**
     * 查找最近执行过的边
     */
    @Query("SELECT e FROM WorkflowEdgeEntity e WHERE e.workflow = :workflow " +
           "AND e.lastExecutionTime >= :since " +
           "AND e.deleted = false ORDER BY e.lastExecutionTime DESC")
    List<WorkflowEdgeEntity> findRecentlyExecutedEdges(@Param("workflow") WorkflowEntity workflow,
                                                       @Param("since") java.time.ZonedDateTime since);

    // ==================== 工作流分析查询 ====================

    /**
     * 统计工作流中各类型边的数量
     */
    @Query("SELECT e.type, COUNT(e) FROM WorkflowEdgeEntity e " +
           "WHERE e.workflow = :workflow AND e.deleted = false " +
           "GROUP BY e.type")
    List<Object[]> countEdgesByType(@Param("workflow") WorkflowEntity workflow);

    /**
     * 查找孤立的边（连接的节点不存在）
     * 注意：这个查询需要根据实际的节点表结构进行调整
     */
    @Query("SELECT e FROM WorkflowEdgeEntity e WHERE e.workflow = :workflow " +
           "AND e.deleted = false " +
           "AND (e.sourceNodeId NOT IN (SELECT n.uid FROM WorkflowNodeEntity n WHERE n.workflow = :workflow AND n.deleted = false) " +
           "OR e.targetNodeId NOT IN (SELECT n.uid FROM WorkflowNodeEntity n WHERE n.workflow = :workflow AND n.deleted = false))")
    List<WorkflowEdgeEntity> findOrphanedEdges(@Param("workflow") WorkflowEntity workflow);

    /**
     * 查找环形连接的边
     * 注意：这是一个简化的环检测，复杂的环检测需要在业务层实现
     */
    @Query("SELECT e FROM WorkflowEdgeEntity e WHERE e.workflow = :workflow " +
           "AND e.sourceNodeId = e.targetNodeId " +
           "AND e.deleted = false")
    List<WorkflowEdgeEntity> findSelfLoopEdges(@Param("workflow") WorkflowEntity workflow);

    // ==================== 批量操作 ====================

    /**
     * 批量启用/禁用边
     */
    @Query("UPDATE WorkflowEdgeEntity e SET e.enabled = :enabled " +
           "WHERE e.workflow = :workflow AND e.uid IN :edgeUids")
    int batchUpdateEdgeEnabled(@Param("workflow") WorkflowEntity workflow,
                              @Param("edgeUids") List<String> edgeUids,
                              @Param("enabled") Boolean enabled);

    /**
     * 批量删除连接指定节点的边
     */
    @Query("UPDATE WorkflowEdgeEntity e SET e.deleted = true " +
           "WHERE e.workflow = :workflow AND (e.sourceNodeId = :nodeId OR e.targetNodeId = :nodeId)")
    int batchDeleteEdgesConnectingNode(@Param("workflow") WorkflowEntity workflow,
                                      @Param("nodeId") String nodeId);

}
