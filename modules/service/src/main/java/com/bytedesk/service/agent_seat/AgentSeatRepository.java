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
package com.bytedesk.service.agent_seat;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AgentSeatRepository extends JpaRepository<AgentSeatEntity, Long>, JpaSpecificationExecutor<AgentSeatEntity> {

    Optional<AgentSeatEntity> findByUid(String uid);

    Optional<AgentSeatEntity> findByOrgUidAndSeatNoAndDeletedFalse(String orgUid, String seatNo);

    List<AgentSeatEntity> findByOrgUidAndSeatNoAndDeletedFalseOrderByCreatedAtDesc(String orgUid, String seatNo);

    Optional<AgentSeatEntity> findByAssignedAgentUidAndDeletedFalse(String assignedAgentUid);

    List<AgentSeatEntity> findByAssignedAgentUidInAndDeletedFalse(Collection<String> assignedAgentUids);

    Page<AgentSeatEntity> findByOrgUidAndDeletedFalse(String orgUid, Pageable pageable);

    List<AgentSeatEntity> findByOrgUidAndDeletedFalseOrderByCreatedAtAsc(String orgUid);

    long countByOrgUidAndDeletedFalse(String orgUid);

    Boolean existsByUid(String uid);

    // Optional<AgentSeatEntity> findByNameAndOrgUidAndTypeAndDeletedFalse(String name, String orgUid, String type);

    // Boolean existsByPlatform(String platform);
}
