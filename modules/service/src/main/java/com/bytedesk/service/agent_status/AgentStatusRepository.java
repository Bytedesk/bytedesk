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

public interface AgentStatusRepository extends JpaRepository<AgentStatusEntity, Long>, JpaSpecificationExecutor<AgentStatusEntity> {

    Optional<AgentStatusEntity> findByUid(String uid);
    
    List<AgentStatusEntity> findByOrgUidAndCreatedAtBetween(String orgUid, ZonedDateTime startTime, ZonedDateTime endTime);

    // 修改为根据agent中是否含有uid进行查询
    List<AgentStatusEntity> findByAgentContainsAndCreatedAtBetween(String agentUid, ZonedDateTime startTime, ZonedDateTime endTime);
    // List<AgentStatusEntity> findByAgentUidAndCreatedAtBetween(String agentUid, ZonedDateTime startTime, ZonedDateTime endTime);

    List<AgentStatusEntity> findByOrgUid(String orgUid);
}
