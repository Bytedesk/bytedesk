/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 18:50:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 13:16:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.statistic;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketStatisticRepository extends JpaRepository<TicketStatisticEntity, Long>, JpaSpecificationExecutor<TicketStatisticEntity> {

    Optional<TicketStatisticEntity> findByUid(String uid);

    Optional<TicketStatisticEntity> findByWorkgroupUid(String workgroupUid);

    // List<TicketStatisticEntity> findByAssigneeUid(String assigneeUid);    
    
} 