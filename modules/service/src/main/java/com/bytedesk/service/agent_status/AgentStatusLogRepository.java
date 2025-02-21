/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-11 11:15:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 16:39:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AgentStatusLogRepository extends JpaRepository<AgentStatusLogEntity, Long>, JpaSpecificationExecutor<AgentStatusLogEntity> {
    
    List<AgentStatusLogEntity> findByOrgUidAndCreatedAtBetween(String orgUid, LocalDateTime startTime, LocalDateTime endTime);

    List<AgentStatusLogEntity> findByAgentUidAndCreatedAtBetween(String agentUid, LocalDateTime startTime, LocalDateTime endTime);

    List<AgentStatusLogEntity> findByOrgUid(String orgUid);
}
