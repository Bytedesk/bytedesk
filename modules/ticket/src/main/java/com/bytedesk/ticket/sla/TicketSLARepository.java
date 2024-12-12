/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 12:46:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 14:39:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.sla;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface TicketSLARepository extends JpaRepository<TicketSLAEntity, Long> {
    
    @Query("SELECT s FROM TicketSLAEntity s WHERE " +
           "(s.priority = ?1 OR s.priority IS NULL) AND " +
           "(s.categoryId = ?2 OR s.categoryId IS NULL) " +
           "ORDER BY s.priority DESC, s.categoryId DESC LIMIT 1")
    Optional<TicketSLAEntity> findMatchingSLA(String priority, Long categoryId);
} 