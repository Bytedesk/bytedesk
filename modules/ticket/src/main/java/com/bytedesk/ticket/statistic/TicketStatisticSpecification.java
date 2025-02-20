/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-20 12:53:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 13:48:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.statistic;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TicketStatisticSpecification extends BaseSpecification {

    
    
    public static Specification<TicketStatisticEntity> search(TicketStatisticRequest request) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));

            // workgroupUid
            if (StringUtils.hasText(request.getWorkgroupUid())) {
                predicates.add(criteriaBuilder.equal(root.get("workgroupUid"), request.getWorkgroupUid()));
            }

            // assigneeUid
            if (StringUtils.hasText(request.getAssigneeUid())) {
                predicates.add(criteriaBuilder.equal(root.get("assigneeUid"), request.getAssigneeUid()));
            }

            // statisticStartTime
            if (StringUtils.hasText(request.getStatisticStartTime())) {
                LocalDateTime startDate = LocalDateTime.parse(request.getStatisticStartTime(), formatter);
                predicates.add(criteriaBuilder.equal(root.get("statisticStartTime"), startDate));
            }
            
            // statisticEndTime
            if (StringUtils.hasText(request.getStatisticEndTime())) {
                predicates.add(criteriaBuilder.equal(root.get("statisticEndTime"), request.getStatisticEndTime()));
            }

            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
