/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-17 17:09:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 17:15:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.utils.BdDateUtils;

import jakarta.persistence.criteria.Predicate;

public class ServiceStatisticSpecification extends BaseSpecification {
    
    public static Specification<ServiceStatisticEntity> search(ServiceStatisticRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));

            // workgroupUid
            if (StringUtils.hasText(request.getWorkgroupUid())) {
                predicates.add(criteriaBuilder.equal(root.get("workgroupUid"), request.getWorkgroupUid()));
            }

            // agentUid
            if (StringUtils.hasText(request.getAgentUid())) {
                predicates.add(criteriaBuilder.equal(root.get("agentUid"), request.getAgentUid()));
            }

            // robotUid
            if (StringUtils.hasText(request.getRobotUid())) {   
                predicates.add(criteriaBuilder.equal(root.get("robotUid"), request.getRobotUid()));
            }

            // statisticStartTime
            if (StringUtils.hasText(request.getStatisticStartTime())) { 
                LocalDateTime startDate = BdDateUtils.parseLocalDateTime(request.getStatisticStartTime());
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("statisticStartTime"), startDate));
            }

            // statisticEndTime
            if (StringUtils.hasText(request.getStatisticEndTime())) {
                LocalDateTime endDate = BdDateUtils.parseLocalDateTime(request.getStatisticEndTime());
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("statisticEndTime"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    
}
