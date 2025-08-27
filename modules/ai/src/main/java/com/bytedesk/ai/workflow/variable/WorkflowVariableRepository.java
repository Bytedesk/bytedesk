/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-02 10:24:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-02 10:24:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow.variable;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 工作流变量仓库接口
 */
public interface WorkflowVariableRepository extends JpaRepository<WorkflowVariableEntity, String> {

    /**
     * 根据工作流UID查找变量列表
     */
    List<WorkflowVariableEntity> findByWorkflowUid(String workflowUid);
    
    /**
     * 根据工作流UID和节点UID查找局部变量列表
     */
    List<WorkflowVariableEntity> findByWorkflowUidAndNodeUid(String workflowUid, String nodeUid);
    
    /**
     * 根据工作流UID和变量名称查找变量
     */
    Optional<WorkflowVariableEntity> findByWorkflowUidAndName(String workflowUid, String name);
    
    /**
     * 根据工作流UID、节点UID和变量名称查找局部变量
     */
    Optional<WorkflowVariableEntity> findByWorkflowUidAndNodeUidAndName(String workflowUid, String nodeUid, String name);
    
    /**
     * 根据作用域查找变量列表
     */
    List<WorkflowVariableEntity> findByScope(String scope);
    
    /**
     * 删除工作流的所有变量
     */
    void deleteByWorkflowUid(String workflowUid);
    
    /**
     * 删除节点的所有局部变量
     */
    void deleteByWorkflowUidAndNodeUid(String workflowUid, String nodeUid);
    
    /**
     * 删除工作流中的指定变量
     */
    void deleteByWorkflowUidAndName(String workflowUid, String name);
    
    /**
     * 删除工作流节点中的指定局部变量
     */
    void deleteByWorkflowUidAndNodeUidAndName(String workflowUid, String nodeUid, String name);
}
