/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-14 11:25:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:24:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.agent;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Call坐席数据访问接口
 */
public interface CallAgentRepository extends JpaRepository<CallAgentEntity, Long> {
    
    /**
     * 根据坐席ID查找坐席
     */
    Optional<CallAgentEntity> findByAgentId(String agentId);
    
    /**
     * 根据坐席状态查找坐席列表
     */
    List<CallAgentEntity> findByStatus(CallAgentEntity.AgentStatus status);
    
    /**
     * 根据坐席模式查找坐席列表
     */
    List<CallAgentEntity> findByMode(CallAgentEntity.AgentMode mode);
    
    /**
     * 检查坐席ID是否存在
     */
    boolean existsByAgentId(String agentId);
    
    /**
     * 根据坐席ID删除坐席
     */
    void deleteByAgentId(String agentId);
    
    /**
     * 查找所有就绪状态的坐席
     */
    List<CallAgentEntity> findByStatusAndMode(
        CallAgentEntity.AgentStatus status, 
        CallAgentEntity.AgentMode mode);
} 