/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 12:44:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 16:37:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.agi.attachment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketAttachmentRepository extends JpaRepository<TicketAttachmentEntity, Long> {
    
    Page<TicketAttachmentEntity> findByTicketId(Long ticketId, Pageable pageable);
    
    // Page<TicketAttachmentEntity> findByUserId(Long userId, Pageable pageable);
    
    // Page<TicketAttachmentEntity> findByTicketIdAndType(Long ticketId, String type, Pageable pageable);
    
    // Page<TicketAttachmentEntity> findByTypeAndReferenceId(String type, Long referenceId, Pageable pageable);
} 