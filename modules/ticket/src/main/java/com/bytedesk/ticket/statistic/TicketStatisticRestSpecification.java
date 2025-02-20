/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-20 12:53:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 12:53:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.statistic;

import org.springframework.data.jpa.domain.Specification;

public class TicketStatisticRestSpecification {
    
    public static Specification<TicketStatisticEntity> buildSpecification(TicketStatisticRequest request) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid());
        };
    }
}
