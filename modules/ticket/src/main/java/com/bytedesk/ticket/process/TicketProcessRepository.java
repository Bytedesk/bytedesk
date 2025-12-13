/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-15 16:45:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.process;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketProcessRepository extends JpaRepository<TicketProcessEntity, Long>, JpaSpecificationExecutor<TicketProcessEntity> {

    Optional<TicketProcessEntity> findByUid(String uid);

    Optional<TicketProcessEntity> findByKeyAndOrgUid(String key, String orgUid);

    Optional<TicketProcessEntity> findByKeyAndOrgUidAndType(String key, String orgUid, String type);

    List<TicketProcessEntity> findByOrgUidAndStatus(String orgUid, String status);

    // Boolean existsByPlatform(String platform);
}
